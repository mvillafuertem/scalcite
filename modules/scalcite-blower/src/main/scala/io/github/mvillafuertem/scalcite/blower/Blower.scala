package io.github.mvillafuertem.scalcite.blower

trait Blower[A, B] {

  def apply(a: A): B

}

object Blower {

  def apply[A, B](implicit blower: Blower[A, B]): Blower[A, B] = blower

  implicit class BlowerOps[A, B](a: A)(implicit blower: Blower[A, B]) {

    def blow: B = Blower[A, B].apply(a)

  }

}