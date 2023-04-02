package app.commons.service.blob.media.audio

import app.commons.service.blob.media.MediaBlobServiceTestCase
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import jakarta.inject.Named

@MicronautTest
class LocalAudioBlobServicesTest(@Named("local") private val audioBlobService: AudioBlobService): MediaBlobServiceTestCase(audioBlobService)
