package io.github.mvillafuertem.scalcite.flattener.core

import java.net.URL

import org.scalatest.{FlatSpecLike, Matchers}

import scala.io.Source

class JsonFlattenerSpec extends FlatSpecLike with Matchers {

  it should "flatten a json string" in {

    // Given
    val jsonString = jsonFrom(getClass.getResource("/blowed.json"))

    // When
    val actual = new JsonFlattener toJsonString jsonString

    // Then
    println(actual)
    val expected = jsonFrom(getClass.getResource("/flattened.json"))

    actual shouldBe expected

  }

  private def jsonFrom(url: URL) =
    Source.fromURL(url).getLines().mkString.replaceAll("[\\n\\s]", "")

}
