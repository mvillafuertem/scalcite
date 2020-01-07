package io.github.mvillafuertem.scalcite.flattener.circe

import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

/**
 * @author Miguel Villafuerte
 */
final class CirceFlattenerSpec extends AnyFlatSpecLike with Matchers {

  behavior of "Circe Flattener"

  it should "flatten a json of circe" in {
    // g i v e n
    import io.github.mvillafuertem.scalcite.flattener.core.Flattener._
    import io.github.mvillafuertem.scalcite.flattener.circe.CirceFlattener._

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
