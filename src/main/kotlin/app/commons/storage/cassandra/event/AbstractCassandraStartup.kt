package app.commons.storage.cassandra.event

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.Logger

abstract class AbstractCassandraStartup(private val cqlSession: CqlSession, private val logger: Logger) {
    @EventListener
    protected abstract fun onStartupEvent(event: StartupEvent)

    protected fun getExhibitTableBase(name: String): CreateTable =
        getNamedItemTableBase(name)
            .withColumn("creator_id", DataTypes.TIMEUUID)
            .withColumn("views", DataTypes.INT)
            .withColumn("rating", DataTypes.INT)
            .withColumn("private", DataTypes.BOOLEAN)

    private fun getNamedItemTableBase(name: String): CreateTable =
        getItemTableBase(name)
            .withColumn("name", DataTypes.TEXT)

    private fun getItemTableBase(name: String): CreateTable =
        SchemaBuilder
            .createTable(name).ifNotExists()
            .withPartitionKey("id", DataTypes.TIMEUUID)

    protected fun execute(statement: SimpleStatement) {
        cqlSession.execute(statement)
        logger.info("Query \"${statement.query}\" was executed.")
    }
}
