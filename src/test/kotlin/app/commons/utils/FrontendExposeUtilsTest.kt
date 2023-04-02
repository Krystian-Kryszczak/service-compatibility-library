package app.commons.utils

import app.commons.model.being.user.User
import com.datastax.oss.driver.api.core.uuid.Uuids
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FrontendExposeUtilsTest : StringSpec({

    "extract avatar url should not return null" {
        val user = User(Uuids.timeBased())
        FrontendExposeUtils.extractAvatarUrl(user) shouldNotBe null
    }

    "extract avatar url should return null" {
        val user = User()
        FrontendExposeUtils.extractAvatarUrl(user) shouldBe null
    }

    "format avatar data should return `John Smith`" {
        val user = User(
            Uuids.timeBased(),
            "John",
            "Smith"
        )
        FrontendExposeUtils.formatAuthorData(user) shouldBe "John Smith"
    }

    "format avatar data should return ``" {
        val user = User()
        FrontendExposeUtils.formatAuthorData(user) shouldBe ""
    }
})
