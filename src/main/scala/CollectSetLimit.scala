package com.c28n.agg

import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.{Encoder}
import org.apache.spark.sql.expressions.Aggregator

import scala.collection.mutable

//TODO: make it work with Any (when I'm using HashSet[Any], I get the error "[ENCODER_NOT_FOUND] Not found an encoder of the type Any to Spark SQL internal representation"

case class CollectSetLimit(limit: Int) extends Aggregator[String, mutable.HashSet[String], mutable.HashSet[String]] {
  // A zero value for this aggregation. Should satisfy the property that any b + zero = b
  def zero: mutable.HashSet[String] = mutable.HashSet.empty[String]

  // Combine two values to produce a new value. For performance, the function may modify `buffer`
  // and return it instead of constructing a new object
  def reduce(buffer: mutable.HashSet[String], data: String): mutable.HashSet[String] = {
    if (data != null && buffer.size < limit)
      buffer += data
    else
      buffer
  }

  // Merge two intermediate values
  def merge(b1: mutable.HashSet[String], b2: mutable.HashSet[String]): mutable.HashSet[String] = {
    if (b1.size >= limit) b1.take(limit)
    else if (b2.size >= limit) b2.take(limit)
    else (b1 ++= b2).take(limit)
  }

  // Transform the output of the reduction
  def finish(reduction: mutable.HashSet[String]): mutable.HashSet[String] = reduction.take(limit)

  def bufferEncoder: Encoder[mutable.HashSet[String]] = ExpressionEncoder[mutable.HashSet[String]]

  def outputEncoder: Encoder[mutable.HashSet[String]] = ExpressionEncoder[mutable.HashSet[String]]

}

