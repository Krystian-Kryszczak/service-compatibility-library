package app.commons.service.blob.media.audio

import app.commons.service.blob.media.LocalMediaBlobService
import app.commons.service.blob.media.audio.AudioBlobService
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "local")
class LocalAudioBlobService: LocalMediaBlobService("audio"), AudioBlobService
