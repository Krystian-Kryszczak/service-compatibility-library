package app.commons.config.cloud.azure

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.kotest.annotation.MicronautTest

@MicronautTest
class AzureBlobConfigTest(private val azureBlobConfig: AzureBlobConfig): StringSpec({

    "azure blob configuration values should not contain null" {
        listOf(
                azureBlobConfig.endpoint
        ) shouldNotContain null
    }

    "azure blob configuration values should be the same as specified in the application context" {
        val prefix = "cloud.azure.blob"

        val items: MutableMap<String, Any> = HashMap()
        items["${prefix}.endpoint"] = "localhost"

        val ctx = ApplicationContext.run(items)
        val ctxAzureBlobConfig = ctx.getBean(AzureBlobConfig::class.java)

        ctxAzureBlobConfig.endpoint shouldBe "localhost"

        ctx.close()
    }
})
