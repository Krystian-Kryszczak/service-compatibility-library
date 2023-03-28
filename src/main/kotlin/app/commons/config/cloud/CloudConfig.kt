package app.commons.config.cloud

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("cloud")
class CloudConfig {
    var name: String? = null
}
