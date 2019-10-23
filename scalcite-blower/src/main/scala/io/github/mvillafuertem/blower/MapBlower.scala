package io.github.mvillafuertem.blower

import scala.annotation.tailrec
import scala.collection.immutable.Map
import scala.collection.mutable


object MapBlower {
  def apply(map: Map[String, Any]): MapBlower = new MapBlower(map)
}

final class MapBlower(map: Map[String, Any]) {

  def blowUp: Map[String, Any] = {
    val tree = mutable.Map[String, Any]()
    map.foreach(tuple => treeFromPath(splitPath(tuple._1), tuple._2, tree))
    tree.toMap
  }

  private def splitPath(path: String): Array[String] = {
    path.split("[.]")
  }

  @tailrec
  private def treeFromPath(path: Array[String], value: Any, leaf: mutable.Map[String, Any]): mutable.Map[String, Any] = {
    if (path.length equals 1) {
      leaf.put(path.head, value)
      leaf
    } else {
      treeFromPath(path.drop(1), value, `with`(path.head, leaf))
    }
  }

  private def `with`(propertyName: String, leaf: mutable.Map[String, Any]): mutable.Map[String, Any] = {
    val option = leaf.get(propertyName)

    option match {
      case Some(value) => value.asInstanceOf[mutable.Map[String, Any]]
      case None => val result = new mutable.LinkedHashMap[String, Any]
        leaf.put(propertyName, result)
        result
    }

  }

}
