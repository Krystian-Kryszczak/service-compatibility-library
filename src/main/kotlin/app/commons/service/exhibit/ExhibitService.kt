package app.commons.service.exhibit

import app.commons.model.exhibit.Exhibit
import app.commons.service.ItemService
import io.micronaut.http.HttpResponse
import io.micronaut.security.authentication.Authentication
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.UUID

interface ExhibitService<T: Exhibit>: ItemService<T> {
    fun propose(authentication: Authentication?): Flowable<T>
    fun findById(id: UUID, authentication: Authentication?): Single<HttpResponse<T>>
    fun getCreatorId(itemId: UUID): Maybe<UUID>
}
