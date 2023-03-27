package app.service.blob.media

import com.datastax.oss.driver.api.core.uuid.Uuids
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeEmitter
import io.reactivex.rxjava3.core.Single
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.util.UUID
import kotlin.concurrent.thread

abstract class LocalMediaBlobService(private val containerName: String): MediaBlobService {

    private val storageDir: String = System.getenv(LOCAL_CLOUD_BLOB_STORAGE_DIR)!!
    private val storageContainer: File = getStorageContainer()

    private fun getStorageContainer(): File {
        val storageDir = this.storageDir.correctDirPath()
        val containerName = this.containerName.correctDirPath()

        val storageContainerDirectory = File(storageDir + containerName)

        if (!storageContainerDirectory.mkdirs() && !storageContainerDirectory.isDirectory) {
            val msg = "Wrong dir path specified in \"$LOCAL_CLOUD_BLOB_STORAGE_DIR\"."
            logger.error(msg)
            throw RuntimeException(msg)
        }

        logger.info("Local cloud blob storage dir: ${storageContainerDirectory.absolutePath}")
        return storageContainerDirectory
    }

    private fun String.correctDirPath() = if (endsWith("/")) this else "$this/"

    override fun save(inputStream: InputStream, creatorId: UUID, private: Boolean): Single<UUID> {
        val id = Uuids.timeBased()
        return save(id, inputStream, creatorId, private)
            .andThen(Single.just(id))
    }

    override fun save(id: UUID, inputStream: InputStream, creatorId: UUID, private: Boolean): Completable = Completable.create {
        val blobContainerDir = File(storageContainer, id.toString())
        val file = createBlobDir(id, creatorId, private, blobContainerDir)

        if (file != null) {
            thread(true) {
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.transferTo(output)
                    }
                }
            }
        }
        it.onComplete()
    }

    override fun save(id: UUID, fileUpload: StreamingFileUpload, creatorId: UUID, private: Boolean): Single<Boolean> =
        Single.just(File(storageContainer, id.toString()))
        .flatMap {
            val file = createBlobDir(id, creatorId, private, it)
            if (file != null) Single.fromPublisher(fileUpload.transferTo(file)) else Single.just(false)
        }

    private fun createBlobDir(id: UUID, creatorId: UUID, private: Boolean, blobContainerDir: File): File? {
        if (!blobContainerDir.mkdir()) {
            logger.error("Container with id $id already exists!")
            return null
        }

        val dataFile = File(blobContainerDir, DATA)
        if (!dataFile.createNewFile()) {
            logger.error("Container with id $id already exists!")
            return null
        }

        val metadataFile = File(blobContainerDir, METADATA)
        if (!metadataFile.createNewFile()) {
            logger.error("Container with id $id already exists!")
            return null
        }

        if (!metadataFile.canWrite() && !metadataFile.setWritable(true)) {
            logger.error("Cannot write to metadata file in $id container!")
            return null
        }

        metadataFile.writeText(
            CREATOR_ID + SEPARATOR + creatorId + NEW_ENTRY_SEPARATOR +
            PRIVATE + SEPARATOR + private + NEW_ENTRY_SEPARATOR
        )

        return dataFile
    }

    override fun update(id: UUID, clientId: UUID, private: Boolean): Single<Boolean> = updateBlob(id, clientId, null, private)

    override fun update(id: UUID, clientId: UUID, inputStream: InputStream, private: Boolean?): Single<Boolean> = updateBlob(id, clientId, inputStream, private)

    private fun updateBlob(id: UUID, clientId: UUID, inputStream: InputStream?, private: Boolean?): Single<Boolean> = Single.create {
        val blobContainerDir = File(storageContainer, id.toString())
        val metadata = File(blobContainerDir, METADATA)
        if (metadata.isFile) {
            val lines = metadata.readLines().toMutableList()
            val clientIsCreator = metadataCreatorIdEquals(lines, clientId)

            val changedStatus = changePrivateStatus(lines, private, clientIsCreator, metadata)
            if (inputStream != null) {
                val file = File(blobContainerDir, DATA)
                val transferred = inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.transferTo(output)
                    }
                }
                it.onSuccess(transferred > 0 && (private == null || changedStatus))
            } else {
                it.onSuccess(private == null || changedStatus)
            }
        } else {
            it.onSuccess(false)
        }
    }

    private fun changePrivateStatus(lines: MutableList<String>, private: Boolean?, clientIsCreator: Boolean, metadata: File): Boolean {
        if (private == null) return false
        if (clientIsCreator) {
            val status = lines.firstOrNull { line -> line.startsWith(PRIVATE) }
            if (status != null) {
                val index = lines.indexOf(status)
                val data = status.split(SEPARATOR)
                if (data.size != 2) {
                    lines[index] = PRIVATE + SEPARATOR + private + NEW_ENTRY_SEPARATOR
                } else {
                    val value = data[1].toBooleanStrictOrNull()
                    if (value!=null) {
                        if (value!=private) {
                            lines[index] = PRIVATE + SEPARATOR + private + NEW_ENTRY_SEPARATOR
                        }
                    } else {
                        lines[index] = PRIVATE + SEPARATOR + private + NEW_ENTRY_SEPARATOR
                    }
                }
                metadata.writeText(lines.joinToString())
                return true
            }
        }
        return false
    }

    override fun downloadById(id: UUID): Maybe<out InputStream> = downloadById(id, null)

    override fun downloadById(id: UUID, clientId: UUID?): Maybe<out InputStream> = Maybe.create { emitter ->
        val containerDir = File(storageContainer, id.toString())
        val metadataFile = File(containerDir, METADATA)

        if (metadataFile.isFile) {
            val metadataLines = metadataFile.readLines()
            if (!metadataIsPrivate(metadataLines) || clientId == null || metadataCreatorIdEquals(metadataLines, clientId)) {
                ifBlobContainerHasDataEmitInputStream(
                    File(containerDir, DATA),
                    emitter
                )
            }
        } else {
            ifBlobContainerHasDataEmitInputStream(
                File(containerDir, DATA),
                emitter
            )
        }
        emitter.onComplete()
    }

    private fun ifBlobContainerHasDataEmitInputStream(blobContainerDataFile: File, emitter: MaybeEmitter<InputStream>) {
        if (blobContainerDataFile.isFile)
            emitter.onSuccess(blobContainerDataFile.inputStream())
    }

    override fun deleteById(id: UUID): Completable =
        Completable.fromCallable(File(storageContainer, id.toString())::deleteRecursively)

    override fun deleteById(id: UUID, clientId: UUID): Single<Boolean> = Single.create {
        val blobContainerDir = File(storageContainer, id.toString())
        val metadata = File(blobContainerDir, METADATA)

        if (metadataCreatorIdEquals(metadata, clientId)) {
            it.onSuccess(blobContainerDir.deleteRecursively())
        } else {
            it.onSuccess(false)
        }
    }

    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = Single.create {
        val blobContainerDir = File(storageContainer, id.toString())
        it.onSuccess(
            if (blobContainerDir.exists()) blobContainerDir.deleteRecursively() else false
        )
    }

    override fun deleteByIdIfExists(id: UUID, clientId: UUID): Single<Boolean> = Single.create {
        val blobContainerDir = File(storageContainer, id.toString())
        it.onSuccess(
            if (blobContainerDir.exists())
                if (metadataCreatorIdEquals(File(blobContainerDir, METADATA), clientId))
                    blobContainerDir.deleteRecursively()
                else {
                    println("is not creator")
                    false
                }
            else false
        )
    }

    private fun metadataCreatorIdEquals(metadataFile: File, clientId: UUID): Boolean =
        metadataFile.isFile && metadataCreatorIdEquals(metadataFile.readLines(), clientId)

    private fun metadataCreatorIdEquals(metadataLines: List<String>, clientId: UUID): Boolean {
        val creatorIdEntry = metadataLines
            .firstOrNull{ it.contains(CREATOR_ID) }

        val creatorIdValue = creatorIdEntry
            ?.split(SEPARATOR)
            ?.get(1)

        return creatorIdValue == clientId.toString()
    }

    private fun metadataIsPrivate(metadataLines: List<String>) =
        metadataLines.firstOrNull{ it.contains(PRIVATE) }
            ?.split(SEPARATOR)
            ?.get(1)
            ?.toBooleanStrictOrNull() == true

    companion object {
        private const val LOCAL_CLOUD_BLOB_STORAGE_DIR = "LOCAL_CLOUD_BLOB_STORAGE_DIR"
        private const val METADATA = "metadata"
        private const val DATA = "data"
        private const val SEPARATOR = ": "
        private const val NEW_ENTRY_SEPARATOR = "\n"
        private const val CREATOR_ID = "creator_id"
        private const val PRIVATE = "private"

        private val logger: Logger = LoggerFactory.getLogger(LocalMediaBlobService::class.java)
    }
}
