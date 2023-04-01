package app.commons.config.cloud

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.kotest.annotation.MicronautTest

@MicronautTest
class CloudConfigTest(private val cloudConfig: CloudConfig): StringSpec({

    "cloud configuration values should not contain null" {
        listOf(
                cloudConfig.name
        ) shouldNotContain null
    }

    "cloud configuration values should be the same as specified in the application context" {
        val prefix = "cloud"

        val items: MutableMap<String, Any> = HashMap()
        items["${prefix}.name"] = "local"

        val ctx = ApplicationContext.run(items)
        val ctxCloudConfig = ctx.getBean(CloudConfig::class.java)

        ctxCloudConfig.name shouldBe "local"

        ctx.close()
    }
})
