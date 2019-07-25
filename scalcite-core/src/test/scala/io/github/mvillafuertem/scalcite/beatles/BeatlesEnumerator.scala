package io.github.mvillafuertem.scalcite.beatles

import io.github.mvillafuertem.scalcite.beatles.BeatlesEnumerator._
import org.apache.calcite.linq4j.{Enumerator, Linq4j}

import scala.collection.JavaConverters._

final class BeatlesEnumerator(buf: StringBuilder,
                              filter: Integer,
                              projects: Array[Int]) extends Enumerator[Array[Any]] {

  private val enumerator = Linq4j.iterableEnumerator(BEATLES.toList.asJava)

  override def current(): Array[Any] = enumerator.current

  override def moveNext(): Boolean = enumerator.moveNext()


  override def reset(): Unit = enumerator.reset()

  override def close(): Unit = enumerator.close()

}

object BeatlesEnumerator {

  private val BEATLES = Array(
    Array(4, "John", 1940),
    Array(4, "Paul", 1942),
    Array(6, "George", 1943),
    Array(5, "Ringo", 1940)
  )
}
