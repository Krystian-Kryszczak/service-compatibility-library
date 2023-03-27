package app.factory.cassandra

import app.storage.cassandra.dao.CommonsDaoMapper
import app.storage.cassandra.dao.being.user.UserDao
import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.CqlSession
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class CassandraFactory(cqlSession: CqlSession) {
    private val keyspace: CqlIdentifier = cqlSession.keyspace.get()
    private val daoMapper: CommonsDaoMapper = CommonsDaoMapper.builder(cqlSession).withDefaultKeyspace(cqlSession.keyspace.get()).build()

    @Singleton
    fun daoMapper(): CommonsDaoMapper = daoMapper
    @Singleton
    fun userDao(): UserDao = daoMapper.userDao(keyspace)
}
