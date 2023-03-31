package app.commons.service.blob.media.video

import app.commons.service.blob.media.AzureMediaBlobService
import com.azure.storage.blob.BlobServiceClient
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "azure")
class AzureVideoBlobService(blobServiceClient: BlobServiceClient): AzureMediaBlobService(blobServiceClient, "video"),
    VideoBlobService
