package app.service.blob.media.video

import app.service.blob.media.LocalMediaBlobService
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "local")
class LocalVideoBlobService: LocalMediaBlobService("video"), VideoBlobService
