package io.circe.scalcite.flattener

import com.github.plokhotnyuk.jsoniter_scala.core.{ readFromArray, JsonValueCodec }
import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets

final class ScalciteFlattenerSpec extends AnyFlatSpecLike with Matchers {

  behavior of "Scalcite Flattener"

  it should "flatten a json string of circe" in {
    // g i v e n
    val json: String =
      """
        |{
        |  "id": "c730433b-082c-4984-9d66-855c243266f0",
        |  "name": "Foo",
        |  "counts": [
        |    1,
        |    2,
        |    3
        |  ],
        |  "values": {
        |    "bar": true,
        |    "baz": 100.001,
        |    "qux": "a"
        |  }
        |}
        |""".stripMargin

    // w h e n
    implicit val codec: JsonValueCodec[Json] = ScalciteFlattener.codec
    val actual: Json                         = readFromArray(json.getBytes(StandardCharsets.UTF_8))

    // t h e n
    val expected = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts.[0]", Json.fromInt(1)),
      ("counts.[1]", Json.fromInt(2)),
      ("counts.[2]", Json.fromInt(3)),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("a"))
    )

    actual shouldBe expected
  }

  it should "flatten a json of circe" in {
    // g i v e n
    val json                                 = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts", Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))),
      (
        "values",
        Json.obj(
          ("bar", Json.fromBoolean(true)),
          ("baz", Json.fromDoubleOrNull(100.001)),
          ("qux", Json.fromString("a"))
        )
      )
    )
    // w h e n
    implicit val codec: JsonValueCodec[Json] = ScalciteFlattener.codec
    val actual: Json                         = readFromArray(json.noSpaces.getBytes(StandardCharsets.UTF_8))

    // t h e n
    val expected = Json.obj(
      ("id", Json.fromString("c730433b-082c-4984-9d66-855c243266f0")),
      ("name", Json.fromString("Foo")),
      ("counts.[0]", Json.fromInt(1)),
      ("counts.[1]", Json.fromInt(2)),
      ("counts.[2]", Json.fromInt(3)),
      ("values.bar", Json.fromBoolean(true)),
      ("values.baz", Json.fromDoubleOrNull(100.001)),
      ("values.qux", Json.fromString("a"))
    )

    actual shouldBe expected
  }

}
