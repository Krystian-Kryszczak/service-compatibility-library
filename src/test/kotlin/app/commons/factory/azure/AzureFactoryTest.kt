package app.commons.factory.azure

import com.azure.storage.blob.BlobServiceClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.micronaut.test.extensions.kotest.annotation.MicronautTest

@MicronautTest
class AzureFactoryTest(private val blobServiceClient: BlobServiceClient): StringSpec({

    "blob service client inject test" {
        blobServiceClient shouldNotBe null
    }
})
