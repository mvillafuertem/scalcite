package io.github.mvillafuertem.scalcite

import java.util

import org.apache.calcite.linq4j.{Enumerator, Linq4j}


final class JsonEnumerator(array: Array[AnyRef]) extends Enumerator[Array[AnyRef]] {

  var enumerator: Enumerator[Array[AnyRef]] = {
    val objs = new util.ArrayList[Array[AnyRef]]
    objs.add(array)
    Linq4j.enumerator(objs)
  }

  override def current: Array[AnyRef] = this.enumerator.current

  override def moveNext: Boolean = this.enumerator.moveNext

  override def reset(): Unit = {
    this.enumerator.reset()
  }

  override def close(): Unit = {
    this.enumerator.close()
  }
}
