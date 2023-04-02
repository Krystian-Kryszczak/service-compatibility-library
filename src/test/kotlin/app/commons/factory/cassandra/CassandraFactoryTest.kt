package app.commons.factory.cassandra

import app.commons.storage.cassandra.dao.CommonsDaoMapper
import app.commons.storage.cassandra.dao.being.user.UserDao
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.micronaut.test.extensions.kotest.annotation.MicronautTest

@MicronautTest
class CassandraFactoryTest(
        private val daoMapper: CommonsDaoMapper,
        private val userDao: UserDao
): StringSpec({

    "dao mapper inject test" {
        daoMapper shouldNotBe null
    }

     "user dao inject test" {
         userDao shouldNotBe null
    }
})
