package app.config.cloud.azure

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("cloud.azure.blob")
class AzureBlobConfig {
    var endpoint: String? = null
}
