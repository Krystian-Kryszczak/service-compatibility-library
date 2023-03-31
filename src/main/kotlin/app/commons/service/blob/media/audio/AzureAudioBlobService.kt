package app.commons.service.blob.media.audio

import app.commons.service.blob.media.AzureMediaBlobService
import app.commons.service.blob.media.audio.AudioBlobService
import com.azure.storage.blob.BlobServiceClient
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Singleton
@Requires(property = "cloud.name", value = "azure")
class AzureAudioBlobService(blobServiceClient: BlobServiceClient): AzureMediaBlobService(blobServiceClient, "audio"),
    AudioBlobService
