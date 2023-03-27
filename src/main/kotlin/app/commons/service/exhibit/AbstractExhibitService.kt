package app.service.exhibit

import app.model.exhibit.Exhibit
import app.service.AbstractItemService
import app.storage.cassandra.dao.exhibit.ExhibitDao
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import java.util.UUID

abstract class AbstractExhibitService<T: Exhibit>(private val exhibitDao: ExhibitDao<T>): ExhibitService<T>, AbstractItemService<T>(exhibitDao) {
    override fun getCreatorId(itemId: UUID): Maybe<UUID> = Flowable.fromPublisher(exhibitDao.findById(itemId)).firstElement().flatMap { if (it.creatorId != null) Maybe.just(it.creatorId!!) else Maybe.empty() }
}
