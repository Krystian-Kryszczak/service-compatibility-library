package app.commons.storage.cassandra.dao

import app.commons.storage.cassandra.dao.being.user.UserDao
import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.MapperBuilder
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace
import com.datastax.oss.driver.api.mapper.annotations.Mapper

@Mapper
interface CommonsDaoMapper {
    @DaoFactory
    fun userDao(@DaoKeyspace keyspace: CqlIdentifier): UserDao

    companion object {
        @JvmStatic
        fun builder(session: CqlSession): MapperBuilder<CommonsDaoMapper> {
            return CommonsDaoMapperBuilder(session)
        }
    }
}
