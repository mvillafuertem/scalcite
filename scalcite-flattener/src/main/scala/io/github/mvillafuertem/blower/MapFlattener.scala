package io.github.mvillafuertem.blower

import scala.collection.mutable

object MapFlattener {
  def apply(map: Map[String, Any]): MapFlattener = new MapFlattener(map)
}

final class MapFlattener(tree: Map[String, Any]) {

  private val out = mutable.Map[String, Any]()

  def flatten: Map[String, Any] = {
    flat("", tree)
    out.toMap
  }

  private def flat(basePath: String, tree: Map[String, Any]): Unit = {
    tree.toSet.foreach((entry: (String, Any)) => put(appendPath(basePath, entry._1), entry._2))
  }

  private def put(path: String, anyRef: Any): Unit = {

    anyRef match {
      case leaf: Map[_, _] =>
        flat(path, leaf.asInstanceOf[Map[String, Any]])

      case _ =>
        out.put(path, anyRef)
    }
  }

  private def appendPath(basePath: String, key: String): String = {
    if (basePath.nonEmpty) s"$basePath.$key"
    else key
  }
}
