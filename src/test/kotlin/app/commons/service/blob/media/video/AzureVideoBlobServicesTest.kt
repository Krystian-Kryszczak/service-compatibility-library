package app.commons.service.blob.media.video

import app.commons.service.blob.media.MediaBlobServiceTestCase
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import jakarta.inject.Named

@MicronautTest
class AzureVideoBlobServicesTest(@Named("azure") private val imageBlobService: VideoBlobService): MediaBlobServiceTestCase(imageBlobService) // not tested
