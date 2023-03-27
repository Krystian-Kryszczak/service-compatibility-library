package app.service.blob.media.audio

import app.service.blob.media.LocalMediaBlobService
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "local")
class LocalAudioBlobService: LocalMediaBlobService("audio"), AudioBlobService
