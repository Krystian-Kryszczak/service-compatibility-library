package app.factory.azure

import app.config.cloud.azure.AzureBlobConfig
import com.azure.core.credential.TokenCredential
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class AzureFactory(private val azureBlobConfig: AzureBlobConfig) {
    @Singleton
    fun blobServiceClient(tokenCredential: TokenCredential): BlobServiceClient {
        return BlobServiceClientBuilder()
            .credential(tokenCredential)
            .endpoint(azureBlobConfig.endpoint)
            .buildClient()
    }
}
