package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.linq4j.{Enumerator, Linq4j}


final class JsonEnumerator(array: Array[Any]) extends Enumerator[Array[Any]] {

  val enumerator: Enumerator[Array[Any]] = {
    val objs = new util.ArrayList[Array[Any]]
    objs.add(array)
    Linq4j.enumerator(objs)
  }

  override def current: Array[Any] = this.enumerator.current

  override def moveNext: Boolean = this.enumerator.moveNext

  override def reset(): Unit = {
    this.enumerator.reset()
  }

  override def close(): Unit = {
    this.enumerator.close()
  }
}

object JsonEnumerator {

  def identityList(n: Int) = {
    val integers = new Array[Int](n)
    var i = 0
    while ( {
      i < n
    }) {
      integers(i) = i

      {
        i += 1; i - 1
      }
    }
    integers
  }

}
