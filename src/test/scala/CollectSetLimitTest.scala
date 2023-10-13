package com.c28n.agg

import org.apache.spark.sql.{Encoder, Encoders, SparkSession, functions}
import org.scalatest.funsuite.AnyFunSuite

case class TestRecord(identifier: String,
                      sessionId: String,
                      age: Int)
object TestRecord {
  def create(identifier: String, sessionId: String, age: Int) = new TestRecord(identifier, sessionId, age)
}

class CollectSetLimitTest extends AnyFunSuite {

  lazy implicit val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]")
      .appName("CollectSetWithLimit")
      .getOrCreate()
  }


  import spark.implicits._

  implicit val encoder: Encoder[TestRecord] = Encoders.product[TestRecord]

  // 1 email with 15 sessions, 1 email with 8 session, then one by one
  val base: List[TestRecord] = List() ++
    (for (n <- 1 to 15) yield TestRecord("bot_1", "session_" + n.toString, n)) ++
    (for (n <- 20 to 28) yield TestRecord("bot_2", "session_" + n.toString, n)) ++
    List(
      TestRecord("identifier1", "session normal", 100),
      TestRecord("identifier2", "session normal", 101)
    )

  val df = base.toDF

  spark.udf.register("collect_set_limit", functions.udaf(new CollectSetLimit(10)))

  test("CollectSetWithLimit Agg - SQL") {

    df.createOrReplaceTempView("bots")
    spark.sql(
      """
        |SELECT identifier, max(age), collect_set_limit(sessionId) as test
        |FROM bots
        |GROUP BY identifier
        |""".stripMargin).show(100, false)
  }
}