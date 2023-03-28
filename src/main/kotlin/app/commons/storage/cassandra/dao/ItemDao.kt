package app.commons.storage.cassandra.dao

import app.commons.model.Item
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Select
import java.util.UUID

interface ItemDao<T: Item>: BaseDao<T> {
    @Select
    fun findById(id: UUID): MappedReactiveResultSet<T>
}
