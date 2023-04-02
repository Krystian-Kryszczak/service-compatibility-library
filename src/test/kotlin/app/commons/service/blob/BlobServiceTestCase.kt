package app.commons.service.blob

import com.datastax.oss.driver.api.core.uuid.Uuids
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import test.utils.TestingStreamingFileUpload
import test.utils.UserTestUtils
import java.io.File
import java.io.InputStream
import java.util.*

abstract class BlobServiceTestCase(mediaBlobService: BlobService) : StringSpec({

    val testMediaFile = File("src/test/resources/media/audio/test.wav")
    val testUser = UserTestUtils.getRandomTestUser()

    val savedMediaUuidList = mutableListOf<UUID>()

    "save media using input stream should return true" {
        mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, false)
            .blockingGet() shouldNotBe null
    }

    "save media using input stream with given media uuid should not throw any exception" {
        val testMediaUuid = Uuids.timeBased()

        shouldNotThrowAny {
            mediaBlobService.save(testMediaUuid, testMediaFile.inputStream(), testUser.id!!, false)
                .blockingSubscribe()
        }

        savedMediaUuidList.add(testMediaUuid)
    }

    "save media using StreamUpload with given media uuid should return true" {
        val testMediaUuid = Uuids.timeBased()

        mediaBlobService.save(testMediaUuid, TestingStreamingFileUpload(testMediaFile), testUser.id!!, false)
            .blockingGet() shouldBe true

        savedMediaUuidList.add(testMediaUuid)
    }

    "update media private status should be successful changed" {
        val mediaUuid = savedMediaUuidList.firstOrNull()
            ?: mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, false).blockingGet()

        mediaBlobService.update(mediaUuid, testUser.id!!, true)
            .blockingGet() shouldBe true

        mediaBlobService.downloadById(mediaUuid, testUser.id!!)
            .map(InputStream::readAllBytes)
            .blockingGet() shouldBe testMediaFile.readBytes()
    }

    "update media content should be successful changed" {
        val mediaUuid = savedMediaUuidList.firstOrNull()
            ?: mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, false).blockingGet()

        val testMediaUpdateFile = File("src/test/resources/media/audio/test2.wav")

        mediaBlobService.update(mediaUuid, testUser.id!!, testMediaUpdateFile.inputStream(), null)
            .blockingGet() shouldBe true

        mediaBlobService.downloadById(mediaUuid)
            .map(InputStream::readAllBytes)
            .blockingGet() shouldBe testMediaUpdateFile.readBytes()
    }

    "download media by id should not be empty" {
        val mediaUuid = mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, false).blockingGet()

        mediaBlobService.downloadById(mediaUuid)
            .isEmpty.blockingGet() shouldBe false

        savedMediaUuidList.add(mediaUuid)
    }

    "download private media by id with non owner credentials should be empty" {
        val mediaUuid = mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, true).blockingGet()
        val nonExistsUserId = UUID.randomUUID()

        mediaBlobService.downloadById(mediaUuid, nonExistsUserId)
            .isEmpty.blockingGet() shouldBe true

        savedMediaUuidList.add(mediaUuid)
    }

    "download private media by id with credentials should not be empty" {
        val mediaUuid = mediaBlobService.save(testMediaFile.inputStream(), testUser.id!!, true).blockingGet()

        mediaBlobService.downloadById(mediaUuid, testUser.id!!)
            .isEmpty.blockingGet() shouldBe false

        savedMediaUuidList.add(mediaUuid)
    }

    "deleted media should not can be available to download" {
        val mediaUuid = Uuids.timeBased()

        mediaBlobService.save(mediaUuid, testMediaFile.inputStream(), testUser.id!!, false)
            .andThen(Single.just(mediaUuid))
            .flatMapCompletable(mediaBlobService::deleteById)
            .andThen(mediaBlobService.downloadById(mediaUuid))
            .isEmpty
            .blockingGet() shouldBe true

        savedMediaUuidList.add(mediaUuid)
    }

    "delete media if not exists should return true" {
        val mediaUuid = Uuids.timeBased()

        mediaBlobService.save(mediaUuid, testMediaFile.inputStream(), testUser.id!!, false)
            .andThen(Single.just(mediaUuid))
            .flatMap(mediaBlobService::deleteByIdIfExists)
            .blockingGet() shouldBe true

        savedMediaUuidList.add(mediaUuid)
    }

    "try delete no exist media if not exists should return false" {
        val notExistsMediaUuid = Uuids.timeBased()

        mediaBlobService.deleteByIdIfExists(notExistsMediaUuid)
            .blockingGet() shouldBe false
    }

    "deleted media with credentials should not can be available to download" {
        val mediaUuid = Uuids.timeBased()

        mediaBlobService.save(mediaUuid, testMediaFile.inputStream(), testUser.id!!, false)
            .andThen(mediaBlobService.deleteById(mediaUuid, testUser.id!!))
            .blockingGet() shouldBe true

        mediaBlobService.downloadById(mediaUuid)
            .isEmpty.blockingGet() shouldBe true
    }

    "delete media if not exists with owner credentials should return true" {
        val mediaUuid = Uuids.timeBased()
        val testUserId = testUser.id!!

        mediaBlobService.save(mediaUuid, testMediaFile.inputStream(), testUserId, true)
            .andThen(mediaBlobService.deleteByIdIfExists(mediaUuid, testUserId))
            .blockingGet() shouldBe true

        // clean
        savedMediaUuidList.add(mediaUuid)
    }

    "try delete no exist media with credentials if not exists should return false" {
        val mediaUuid = Uuids.timeBased()

        mediaBlobService.deleteByIdIfExists(mediaUuid, testUser.id!!)
            .blockingGet() shouldBe false
    }

    afterSpec {
        // clean
        Flowable.fromIterable(savedMediaUuidList)
            .flatMapCompletable(mediaBlobService::deleteById)
            .blockingSubscribe()
    }
})
