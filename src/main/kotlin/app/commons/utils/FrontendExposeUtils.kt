package app.commons.utils

import app.commons.model.being.user.User

object FrontendExposeUtils {
    fun formatAuthorData(user: User): String {
        var result = ""

        val userName = user.name
        if (!userName.isNullOrBlank()) result += userName

        val userLastname = user.lastname
        if (!userLastname.isNullOrBlank()) result += " $userLastname"

        return result
    }

    fun extractAvatarUrl(user: User): String? {
        val id = user.id ?: return null
        return "/images/$id"
    }
}
