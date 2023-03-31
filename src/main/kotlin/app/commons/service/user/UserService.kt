package app.commons.service.user

import app.commons.model.being.user.User
import app.commons.service.ItemService
import io.reactivex.rxjava3.core.Maybe

interface UserService: ItemService<User> {
    fun findByEmail(email: String): Maybe<User>
}
