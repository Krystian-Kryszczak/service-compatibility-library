package app.commons.service.blob.media.image

import app.commons.service.blob.media.LocalMediaBlobService
import app.commons.service.blob.media.image.ImageBlobService
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "local")
class LocalImageBlobService: LocalMediaBlobService("image"), ImageBlobService
