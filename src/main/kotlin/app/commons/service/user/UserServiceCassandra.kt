package app.commons.service.user

import app.commons.model.being.user.User
import app.commons.service.AbstractItemService
import app.commons.storage.cassandra.dao.being.user.UserDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class UserServiceCassandra(private val userDao: UserDao): UserService, AbstractItemService<User>(userDao) {
    override fun save(item: User): Completable = Completable.fromPublisher(userDao.save(item))
    override fun findById(id: UUID): Maybe<User> = Maybe.fromPublisher(userDao.findById(id))
    override fun findByEmail(email: String): Maybe<User> = Flowable.fromPublisher(userDao.findByEmail(email)).firstElement()
    override fun deleteById(id: UUID): Completable = Completable.fromPublisher(userDao.deleteById(id))
    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = Single.fromPublisher(userDao.deleteByIdIfExists(id))
}
