package app.service.blob.media

import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.specialized.AppendBlobClient
import com.azure.storage.blob.specialized.BlobInputStream
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream
import java.util.*

abstract class AzureMediaBlobService(azureBlobServiceClient: BlobServiceClient, containerName: String): MediaBlobService {
    private val blobContainerClient: BlobContainerClient = azureBlobServiceClient.createBlobContainer(containerName.lowercase(Locale.getDefault()))

    override fun save(inputStream: InputStream, creatorId: UUID, private: Boolean): Single<UUID> = Single.create<UUID> {
        val itemId = UUID.randomUUID()
        val blobClient = blobContainerClient.getBlobClient(itemId.toString())
        setTags(blobClient, creatorId, private)
        blobClient.upload(inputStream, false)
        it.onSuccess(itemId)
    }.observeOn(Schedulers.io())
    override fun save(id: UUID, inputStream: InputStream, creatorId: UUID, private: Boolean): Completable = Completable.create {
        val blobClient = blobContainerClient.getBlobClient(id.toString())
        setTags(blobClient, creatorId, private)
        blobClient.upload(inputStream, false)
        it.onComplete()
    }.observeOn(Schedulers.io())
    override fun save(id: UUID, fileUpload: StreamingFileUpload, creatorId: UUID, private: Boolean): Single<Boolean> = Single.create<AppendBlobClient> {
        val blobClient = blobContainerClient.getBlobClient(id.toString())
        setTags(blobClient, creatorId, private)
        it.onSuccess(blobClient.appendBlobClient)
    }.flatMap {
        Single.fromPublisher(fileUpload.transferTo(it.blobOutputStream))
    }

    override fun update(id: UUID, clientId: UUID, private: Boolean): Single<Boolean> = Single.create {
        val blobClient = blobContainerClient.getBlobClient(id.toString())
        it.onSuccess(isOwner(blobClient, clientId) && changePrivate(blobClient, private))
    }
    override fun update(id: UUID, clientId: UUID, inputStream: InputStream, private: Boolean?): Single<Boolean> = Single.create<Boolean> {
        val blobClient = blobContainerClient.getBlobClient(id.toString())
        if (private != null) {
            changePrivate(blobClient, private)
        }
        if (blobClient.exists()) {
            blobClient.upload(inputStream, true)
            it.onSuccess(true)
        } else {
            it.onSuccess(false)
        }
    }.observeOn(Schedulers.io())
    override fun downloadById(id: UUID): Maybe<out InputStream> = Maybe.create<BlobInputStream> {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        if (containerClient.exists()) {
            it.onSuccess(containerClient.openInputStream())
        }
        it.onComplete()
    }.observeOn(Schedulers.io())
    override fun deleteById(id: UUID): Completable = Completable.create {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        containerClient.delete()
        it.onComplete()
    }
    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = Single.create {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        it.onSuccess(containerClient.deleteIfExists())
    }
    override fun downloadById(id: UUID, clientId: UUID?): Maybe<out InputStream> = Maybe.create<BlobInputStream> {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        if (containerClient.exists() && canDownload(containerClient, clientId)) {
            it.onSuccess(containerClient.openInputStream())
        }
        it.onComplete()
    }.observeOn(Schedulers.io())
    override fun deleteById(id: UUID, clientId: UUID): Single<Boolean> = Single.create {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        val isOwner = isOwner(containerClient, clientId)
        if (isOwner) containerClient.delete()
        it.onSuccess(isOwner)
    }
    override fun deleteByIdIfExists(id: UUID, clientId: UUID): Single<Boolean> = Single.create {
        val containerClient = blobContainerClient.getBlobClient(id.toString())
        it.onSuccess(isOwner(containerClient, clientId) && containerClient.deleteIfExists())
    }
    private fun setTags(blobClient: BlobClient, creatorId: UUID, private: Boolean) {
        blobClient.tags["creatorId"] = creatorId.toString()
        if (private) blobClient.tags["private"] = "true"
    }
    private fun changePrivate(blobClient: BlobClient, private: Boolean): Boolean {
        if (private) blobClient.tags["private"] = "true"
        else return blobClient.tags.remove("private") != null
        return true
    }
    private fun isOwner(blobClient: BlobClient, clientId: UUID): Boolean = blobClient.tags["creatorId"] == clientId.toString()
    private fun canDownload(blobClient: BlobClient, clientId: UUID?): Boolean = blobClient.tags["private"] != "true" || blobClient.tags["creatorId"] == clientId.toString()
}
