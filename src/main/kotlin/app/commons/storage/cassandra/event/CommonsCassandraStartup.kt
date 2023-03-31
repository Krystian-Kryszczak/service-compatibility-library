package app.commons.storage.cassandra.event

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class CommonsCassandraStartup(cqlSession: CqlSession): AbstractCassandraStartup(cqlSession, logger) {
    @EventListener
    override fun onStartupEvent(event: StartupEvent) {
        createUserTable()
    }

    private fun createUserTable() = execute(
        SchemaBuilder
            .createTable("user").ifNotExists()
            .withPartitionKey("id", DataTypes.TIMEUUID)
            .withColumn("name", DataTypes.TEXT)
            .withColumn("lastname", DataTypes.TEXT)
            .withColumn("email", DataTypes.TEXT)
            .withColumn("phone_number", DataTypes.TEXT)
            .withColumn("date_of_birth_in_days", DataTypes.INT)
            .withColumn("sex", DataTypes.TINYINT)
            .build()
    )

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CommonsCassandraStartup::class.java)
    }
}
