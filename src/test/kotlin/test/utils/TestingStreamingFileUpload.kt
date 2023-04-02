package test.utils

import io.micronaut.http.MediaType
import io.micronaut.http.multipart.PartData
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import java.io.File
import java.lang.RuntimeException
import java.util.*

class TestingStreamingFileUpload(private val file: File): StreamingFileUpload {

    override fun getContentType(): Optional<MediaType> = Optional.of(MediaType.forFilename(file.name))

    override fun getName(): String = file.name

    override fun getFilename(): String = file.name

    override fun getSize(): Long = file.totalSpace

    override fun getDefinedSize(): Long = file.totalSpace

    override fun isComplete(): Boolean = file.exists()

    override fun subscribe(s: Subscriber<in PartData>?) {
        throw RuntimeException("test.utils.TestingStreamingFileUpload will relevant implementation for this method.")
    }

    override fun transferTo(location: String?): Publisher<Boolean> {
        val loc = location ?: return Flowable.just(false)
        return transferTo(File(loc))
    }

    override fun transferTo(destination: File?): Publisher<Boolean> {
        if (destination == null) return Flowable.just(false)
        return try {
            file.inputStream().use { inputStream ->
                destination.outputStream().use { outputStream ->
                    inputStream.transferTo(outputStream)
                }
            }
            Flowable.just(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Flowable.just(false)
        }
    }

    override fun delete(): Publisher<Boolean> = Flowable.just(false)
}
