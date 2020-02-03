package io.circe.scalcite.flattener

import java.nio.charset.StandardCharsets

import com.github.plokhotnyuk.jsoniter_scala.core.readFromArray
import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

final class ScalciteFlattenerSpec extends AnyFlatSpecLike with Matchers {

  behavior of "Scalcite Flattener"

  it should "flatten a json string of circe" in {
    // g i v e n
    import io.circe.scalcite.flattener.ScalciteFlattener._
    val json: String = """{"id":"c730433b-082c-4984-9d66-855c243266f0","name":"Foo","values":{"bar":true,"baz":100.001,"qux":"a"}}"""

    // w h e n
    val actual = readFromArray(json.getBytes(StandardCharsets.UTF_8))

    // t h e n
    val expected = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("a"))
    )

    actual shouldBe expected
  }

  it should "flatten a json of circe" in {
    // g i v e n
    import io.circe.scalcite.flattener.ScalciteFlattener._
    import io.github.mvillafuertem.scalcite.flattener.core.Flattener._
    val json = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("values",
        Json.obj(
          ("bar", Json.fromBoolean(true)),
          ("baz", Json.fromDoubleOrNull(100.001)),
          ("qux",Json.fromString("a"))
        )
      )
    )
    // w h e n
    val actual = json.flatten

    // t h e n
    val expected = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("a"))
    )

    actual.map(a => a shouldBe expected)
  }

}
