package app.service

import app.model.Item
import app.storage.cassandra.dao.ItemDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import java.util.UUID

abstract class AbstractItemService<T: Item>(private val itemDao: ItemDao<T>): ItemService<T> {
    override fun save(item: T): Completable = Completable.fromPublisher(itemDao.save(item))
    override fun findById(id: UUID): Maybe<T> = Maybe.fromPublisher(itemDao.findById(id))
    override fun update(item: T): Completable = Completable.fromPublisher(itemDao.update(item))
    override fun delete(item: T): Completable = Completable.fromPublisher(itemDao.delete(item))
}
