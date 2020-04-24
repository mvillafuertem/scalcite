package io.github.mvillafuertem.scalcite.flattener

trait Flattener[A, B] {

  def apply(a: A): B

}

object Flattener {

  def apply[A, B](implicit blower: Flattener[A, B]): Flattener[A, B] = blower

  implicit class FlattenerOps[A, B](a: A)(implicit blower: Flattener[A, B]) {

    def flatten: B = Flattener[A, B].apply(a)

  }

}
