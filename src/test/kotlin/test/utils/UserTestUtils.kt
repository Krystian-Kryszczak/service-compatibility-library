package test.utils

import app.commons.model.being.user.User
import com.datastax.oss.driver.api.core.uuid.Uuids

object UserTestUtils {
    fun getRandomTestUser() = User(
        Uuids.timeBased(),
        "John",
        "Smith",
        "john.smith@example.com",
        "111 022 342",
        0,
        1
    )
}
