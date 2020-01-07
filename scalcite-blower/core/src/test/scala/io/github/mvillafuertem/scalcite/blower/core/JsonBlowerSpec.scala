package io.github.mvillafuertem.scalcite.blower.core

import java.net.URL


import scala.io.Source
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class JsonBlowerSpec extends AnyFlatSpecLike with Matchers {

  it should "blowUp a json string" in {

    // g i v e n
    val jsonString = jsonFrom(getClass.getResource("/flattened.json"))

    // w h e n
    val actual = new JsonBlower toJsonString jsonString

    // t h e n
    println(actual)
    val expected = jsonFrom(getClass.getResource("/blowed.json"))
    actual shouldBe expected
  }

  private def jsonFrom(url: URL) =
    Source.fromURL(url).getLines().mkString.replaceAll("[\\n\\s]", "")
}
