package io.github.mvillafuertem.blower

import io.circe.Json
import org.scalatest.{FlatSpecLike, Matchers}

/**
 * @author Miguel Villafuerte
 */
class CirceBlowerSpec extends FlatSpecLike with Matchers {

  behavior of "Circe Blower"

  it should "blow a circe json" in {
    // g i v e n
    import io.github.mvillafuertem.blower.Blower._
    import io.github.mvillafuertem.blower.CirceBlower._

    val json: Json = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts", Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("b"))
    )

    // w h e n
    val actual = json.blow

    // t h e n
    val expected = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts", Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))),
      ("values",
        Json.obj(
          ("bar", Json.fromBoolean(true)),
          ("baz", Json.fromDoubleOrNull(100.001)),
          ("qux", Json.fromString("b")),
        )
      )
    )

    actual.map(a => a shouldBe expected)

  }

}
