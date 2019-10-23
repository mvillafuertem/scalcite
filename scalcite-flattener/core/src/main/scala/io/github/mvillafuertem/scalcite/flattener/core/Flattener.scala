package io.github.mvillafuertem.scalcite.flattener.core

trait Flattener[A, B] {

  def flatten(a: A): B

}

object Flattener {

  def apply[A, B](implicit blower: Flattener[A, B]): Flattener[A, B] = blower

  implicit class FlattenerOps[A, B](a: A)(implicit blower: Flattener[A, B]) {

    def flatten: B = Flattener[A, B].flatten(a)

  }

}

