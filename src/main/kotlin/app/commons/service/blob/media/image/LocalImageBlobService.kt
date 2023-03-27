package app.service.blob.media.image

import app.service.blob.media.LocalMediaBlobService
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "local")
class LocalImageBlobService: LocalMediaBlobService("image"), ImageBlobService
