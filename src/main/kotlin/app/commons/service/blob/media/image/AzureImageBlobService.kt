package app.commons.service.blob.media.image

import app.commons.service.blob.media.AzureMediaBlobService
import com.azure.storage.blob.BlobServiceClient
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "azure")
class AzureImageBlobService(blobServiceClient: BlobServiceClient): AzureMediaBlobService(blobServiceClient, "image"),
    ImageBlobService
