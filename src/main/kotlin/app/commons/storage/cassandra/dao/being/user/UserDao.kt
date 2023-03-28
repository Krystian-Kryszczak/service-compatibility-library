package app.commons.storage.cassandra.dao.being.user

import app.commons.model.being.user.User
import app.commons.storage.cassandra.dao.being.BeingDao
import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Select
import java.util.UUID

@Dao
interface UserDao: BeingDao<User> {
    @Select(customWhereClause = "email = :email", limit = "1", allowFiltering = true)
    fun findByEmail(email: String): MappedReactiveResultSet<User>
    @Delete(entityClass = [User::class])
    fun deleteById(id: UUID): ReactiveResultSet
    @Delete(entityClass = [User::class], ifExists = true)
    fun deleteByIdIfExists(id: UUID): MappedReactiveResultSet<Boolean>
}
