package app.storage.cassandra.dao.exhibit

import app.model.exhibit.Exhibit
import app.storage.cassandra.dao.ItemDao
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Select

interface ExhibitDao<T: Exhibit>: ItemDao<T> {
    @Select(customWhereClause = "private = true", limit = ":l", allowFiltering = true)
    fun find(@CqlName("l") l: Int): MappedReactiveResultSet<T>
}
