package app.commons.service.blob.media.audio

import app.commons.service.blob.media.MediaBlobServiceTestCase
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import jakarta.inject.Named

@MicronautTest
class AzureAudioBlobServicesTest(@Named("azure") private val audioBlobService: AudioBlobService): MediaBlobServiceTestCase(audioBlobService) // not tested
