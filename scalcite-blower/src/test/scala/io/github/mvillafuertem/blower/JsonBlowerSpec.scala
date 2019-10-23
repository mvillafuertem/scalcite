package io.github.mvillafuertem.blower

import java.net.URL

import org.scalatest.{FlatSpecLike, Matchers}

import scala.io.Source

class JsonBlowerSpec extends FlatSpecLike with Matchers {

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
