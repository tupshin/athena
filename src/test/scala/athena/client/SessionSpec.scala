package athena.client

import org.scalatest.{WordSpec, Matchers}
import play.api.libs.iteratee.Iteratee
import scala.concurrent.{Await, Future}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import athena.AthenaTest
import athena.data.CValue
import athena.Athena.AthenaException

class SessionSpec extends WordSpec with AthenaTest with Matchers {

  "A Session" should {
    "execute a query" in {
      val session = Session(hosts, port, "testks")

      try {
        val aggregateRows = Await.result(session.execute("select * from users"), Duration(10, TimeUnit.SECONDS))
        assert(!aggregateRows.isEmpty)

        val rows = Await.result(session.executeStream("select * from users") |>>> Iteratee.getChunks, Duration(10, TimeUnit.SECONDS))
        assert(!rows.isEmpty)
      } finally {
        session.close()
      }
    }

    "fail with an invalid keyspace" in {
      val session = Session(hosts, port, "testks1")
      intercept[AthenaException] {
        await(session.execute("select * from users"))
      }
      session.close()
    }

  }

}