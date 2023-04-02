package app.commons.service.blob.media.image

import app.commons.service.blob.media.MediaBlobServiceTestCase
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import jakarta.inject.Named

@MicronautTest
class LocalImageBlobServicesTest(@Named("local") private val imageBlobService: ImageBlobService): MediaBlobServiceTestCase(imageBlobService)
