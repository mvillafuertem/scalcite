package io.github.mvillafuertem.scalcite.flattener.core


import scala.collection.mutable
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class MapFlattenerTest extends AnyFlatSpecLike with Matchers {

  behavior of "Map Flattener"

  it should "flatten a map" in {
    // g i v e n
    val tree = mutable.Map[String, Any]()
    tree.put("_id", "5c5f1f313fcc6e3084fbe65e")
    tree.put("index", 0)
    tree.put("guid", "f3b5960b-f3e1-4556-9a5d-f552afe204e7")
    tree.put("isActive", true)
    tree.put("balance", "$2,809.92")
    tree.put("picture", "http://placehold.it/32x32")
    tree.put("age", 28)
    tree.put("eyeColor", "blue")
    tree.put("about", "Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n")
    tree.put("registered", "2015-03-28T06:35:33 -01:00")
    tree.put("greeting", "Hello, Elliott Kaufman! You have 5 unread messages.")
    tree.put("favoriteFruit", "strawberry")
    val personalinfo = mutable.Map[String, Any]()
    personalinfo.put("name", "Elliott Kaufman")
    personalinfo.put("gender", "male")
    personalinfo.put("phone", "+1 (858) 421-2925")
    personalinfo.put("email", "elliottkaufman@spacewax.com")
    personalinfo.put("address", "952 Cropsey Avenue, Tyro, Guam, 1787")
    val company = Map[String, Any](("name", "SPACEWAX"))
    personalinfo.put("company", company)
    val immutablepersonalinfo = Map(personalinfo.toSeq: _*)
    tree.put("personalinfo", immutablepersonalinfo)
    val location = Map[String, Any](("latitude", 78.370719), ("longitude", -137.117139))
    tree.put("location", location)

    // w h e n
    val immutableTree = Map(tree.toSeq: _*)
    val actual: Map[String, Any] = MapFlattener(immutableTree).flatten

    // t h e n
    val expected = mutable.Map[String, Any]()
    expected.put("_id", "5c5f1f313fcc6e3084fbe65e")
    expected.put("index", 0)
    expected.put("guid", "f3b5960b-f3e1-4556-9a5d-f552afe204e7")
    expected.put("isActive", true)
    expected.put("balance", "$2,809.92")
    expected.put("picture", "http://placehold.it/32x32")
    expected.put("age", 28)
    expected.put("eyeColor", "blue")
    expected.put("about", "Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n")
    expected.put("registered", "2015-03-28T06:35:33 -01:00")
    expected.put("greeting", "Hello, Elliott Kaufman! You have 5 unread messages.")
    expected.put("favoriteFruit", "strawberry")
    expected.put("personalinfo.name", "Elliott Kaufman")
    expected.put("personalinfo.gender", "male")
    expected.put("personalinfo.phone", "+1 (858) 421-2925")
    expected.put("personalinfo.email", "elliottkaufman@spacewax.com")
    expected.put("personalinfo.address", "952 Cropsey Avenue, Tyro, Guam, 1787")
    expected.put("personalinfo.company.name", "SPACEWAX")
    expected.put("location.latitude", 78.370719)
    expected.put("location.longitude", -137.117139)

    val immutableExpected = Map(expected.toSeq: _*)
    actual shouldBe immutableExpected
  }

}
