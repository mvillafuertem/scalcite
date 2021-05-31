package io.circe.scalcite.flattener

import com.github.plokhotnyuk.jsoniter_scala.core._
import io.circe.Json.{ JArray, JBoolean, JNull, JNumber, JObject, JString }
import io.circe.{ Json, JsonDouble, JsonLong, JsonObject }

import java.util

object ScalciteFlattener {

  implicit val codec: JsonValueCodec[Json] = new JsonValueCodec[Json] {

    def decodeValueRecursive(k: String, in: JsonReader, default: Json): Json =
      in.nextToken() match {
        case 'n'                                                                   => in.readNullOrError(default, "expected `null` value")
        case '"'                                                                   => decodeString(in)
        case 'f' | 't'                                                             => decodeBoolean(in)
        case n @ ('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '-') => decodeNumber(in, n)
        case '['                                                                   => if (k.isEmpty) decodeArray(k, in, default) else decodeArray(k.concat("."), in, default)
        case '{'                                                                   => if (k.isEmpty) decodeObject(k, in, default) else decodeObject(k.concat("."), in, default)
        case _                                                                     => in.decodeError("expected JSON value")
      }

    override def decodeValue(in: JsonReader, default: Json): Json = decodeValueRecursive("", in, default)

    private def decodeObject(k: String, in: JsonReader, default: Json): JObject =
      JObject(
        if (in.isNextToken('}')) JsonObject.empty
        else {
          val x = new util.LinkedHashMap[String, Json]
          in.rollbackToken()
          do {
            val key    = in.readKeyAsString()
            val str    = k.concat(key)
            val result = decodeValueRecursive(str, in, default)
            result match {
              case JObject(value) =>
                //x.putAll(result.asObject.get.toMap.asJava) // TODO intentar no usar converters .asJava
                value.toIterable.foreach { case (str, json) => x.put(str, json) }
              case _              => x.put(str, result)
            }
          } while (in.isNextToken(','))
          if (!in.isCurrentToken('}')) in.objectEndOrCommaError()
          JsonObject.fromLinkedHashMap(x)
        }
      )

    private def decodeArray(k: String, in: JsonReader, default: Json): JObject =
      JObject(
        if (in.isNextToken(']')) JsonObject.empty
        else {
          val m = new util.LinkedHashMap[String, Json]
          in.rollbackToken()
          var i = 0
          do {
            val str    = k.concat(s"[$i]")
            val result = decodeValueRecursive(str, in, default)
            result match {
              case JObject(value) =>
                //m.putAll(result.asObject.get.toMap.asJava) // TODO intentar no usar converters .asJava
                value.toIterable.foreach { case (str, json) => m.put(str, json) }
              case _              => m.put(str, result)
            }
            i += 1
          } while (in.isNextToken(','))
          if (!in.isCurrentToken(']')) in.arrayEndOrCommaError()
          JsonObject.fromLinkedHashMap(m)
        }
      )

    override def encodeValue(x: Json, out: JsonWriter): Unit =
      x match {
        case JNull       => out.writeNull()
        case JString(s)  => out.writeVal(s)
        case JBoolean(b) => out.writeVal(b)
        case JNumber(n)  =>
          n match {
            case JsonLong(l) => out.writeVal(l)
            case _           => out.writeVal(n.toDouble)
          }
        case JArray(a)   =>
          out.writeArrayStart()
          val l = a.size
          var i = 0
          while (i < l) {
            val json: Json = a(i)
            encodeValue(json, out)
            i += 1
          }
          out.writeArrayEnd()
        case JObject(o)  =>
          out.writeObjectStart()
          val it = o.toIterable.iterator
          while (it.hasNext) {
            val (k, v) = it.next()
            out.writeKey(k)
            encodeValue(v, out)
          }
          out.writeObjectEnd()
      }

    override def nullValue: Json = Json.Null
  }

  private def decodeNumber(in: JsonReader, n: Byte) =
    JNumber({
      in.rollbackToken()
      val d = in.readDouble()
      val i = d.toInt
      if (i.toDouble == d) JsonLong(i)
      else JsonDouble(d)
    })

  private def decodeBoolean(in: JsonReader) = {
    in.rollbackToken()
    if (in.readBoolean()) Json.True
    else Json.False
  }

  private def decodeString(in: JsonReader) = {
    in.rollbackToken()
    JString(in.readString(null))
  }
}
