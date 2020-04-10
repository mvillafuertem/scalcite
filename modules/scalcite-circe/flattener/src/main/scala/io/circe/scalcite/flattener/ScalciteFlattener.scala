package io.circe.scalcite.flattener

import java.nio.charset.StandardCharsets
import java.util

import com.github.plokhotnyuk.jsoniter_scala.core._
import io.circe.{Json, JsonDouble, JsonLong, JsonObject}
import io.circe.Json.{JArray, JBoolean, JNull, JNumber, JObject, JString}
import io.github.mvillafuertem.scalcite.flattener.Flattener

object ScalciteFlattener {

  implicit val flatten: Flattener[Json, Either[Throwable, Json]] =
    (blownUp: Json) => Right(_flatten(blownUp))

  //implicit val flatten: Flattener[String, Either[Throwable, Json]] =
  //  (blownUp: String) => Right(_flatten(readFromArray(blownUp.toString.getBytes(StandardCharsets.UTF_8))))

  def flatten(blownUp: String): Either[Throwable, Json] = {
    val json = readFromArray(blownUp.getBytes(StandardCharsets.UTF_8))
    flatten.apply(json)
  }

  private def _flatten(json: Json, path: String = ""): Json = {

    json match {
      case JObject(value) => value
        .toIterable
        .map { case (k, v) => _flatten(v, buildPath(path, k)) }
        .fold(JObject(JsonObject.empty))(_ deepMerge _)

      case JArray(value) => value
        .zipWithIndex
        .map { case (j, index) => _flatten(j, buildPath(path, s"[$index]")) }
        .fold(JObject(JsonObject.empty))(_ deepMerge _)

      case _ => JObject(JsonObject((path, json)))
    }

  }

  private def buildPath(path: String, key: String): String =
    if (path.isEmpty) key else s"$path.$key"

  implicit val codec: JsonValueCodec[Json] = new JsonValueCodec[Json] {

    override def decodeValue(in: JsonReader, default: Json): Json = in.nextToken() match {
      case 'n' => in.readNullOrError(default, "expected `null` value")
      case '"' => decodeString(in)
      case 'f' | 't' => decodeBoolean(in)
      case n@('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '-') => decodeNumber(in, n)
      case '[' => decodeArray(in, default)
      case '{' => decodeObject(in, default)
      case _ => in.decodeError("expected JSON value")
    }

    private def decodeObject(in: JsonReader, default: Json) = {
      JObject(if (in.isNextToken('}')) JsonObject.empty
      else {
        val x = new util.LinkedHashMap[String, Json]
        in.rollbackToken()
        do x.put(in.readKeyAsString(), decodeValue(in, default))
        while (in.isNextToken(','))
        if (!in.isCurrentToken('}')) in.objectEndOrCommaError()
        JsonObject.fromLinkedHashMap(x)
      })
    }

    private def decodeArray(in: JsonReader, default: Json) = {
      JArray(if (in.isNextToken(']')) Vector.empty
      else {
        in.rollbackToken()
        var x = new Array[Json](4)
        var i = 0
        do {
          if (i == x.length) x = java.util.Arrays.copyOf(x, i << 1)
          x(i) = decodeValue(in, default)
          i += 1
        } while (in.isNextToken(','))
        val jsons: Array[Json] = if (in.isCurrentToken(']'))
          if (i == x.length) x
          else java.util.Arrays.copyOf(x, i)
        else in.arrayEndOrCommaError()
        jsons.toVector
      })
    }

    override def encodeValue(x: Json, out: JsonWriter): Unit = x match {
      case JNull => out.writeNull()
      case JString(s) => out.writeVal(s)
      case JBoolean(b) => out.writeVal(b)
      case JNumber(n) => n match {
        case JsonLong(l) => out.writeVal(l)
        case _ => out.writeVal(n.toDouble)
      }
      case JArray(a) =>
        out.writeArrayStart()
        a.foreach(v => encodeValue(v, out))
        out.writeArrayEnd()
      case JObject(o) =>
        out.writeObjectStart()
        o.toIterable.foreach { case (k, v) =>
          out.writeKey(k)
          encodeValue(v, out)
        }
        out.writeObjectEnd()
    }

    override def nullValue: Json = Json.Null
  }


  private def decodeString(in: JsonReader) = {
    in.rollbackToken()
    JString(in.readString(null))
  }

  private def decodeNumber(in: JsonReader, n: Byte) = {
    JNumber({
      in.rollbackToken()
      in.setMark() // TODO: add in.readNumberAsString() to Core API of jsoniter-scala
      var b = n
      try {
        do b = in.nextByte()
        while (b >= '0' && b <= '9')
      } catch {
        case _: JsonReaderException => /* ignore end of input error */
      } finally in.rollbackToMark()
      if (b == '.' || b == 'e' || b == 'E') JsonDouble(in.readDouble())
      else JsonLong(in.readLong())
    })
  }

  private def decodeBoolean(in: JsonReader) = {
    in.rollbackToken()
    if (in.readBoolean()) Json.True
    else Json.False
  }
}
