package io.github.mvillafuertem.scalcite.blower.core


import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class MapBlowerSpec extends AnyFlatSpecLike with Matchers {

  behavior of "Map Blower"

  it should "blow a map" in {

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
    tree.put("personalinfo.name", "Elliott Kaufman")
    tree.put("personalinfo.gender", "male")
    tree.put("personalinfo.phone", "+1 (858) 421-2925")
    tree.put("personalinfo.email", "elliottkaufman@spacewax.com")
    tree.put("personalinfo.address", "952 Cropsey Avenue, Tyro, Guam, 1787")
    tree.put("personalinfo.company.name", "SPACEWAX")
    tree.put("location.latitude", 78.370719)
    tree.put("location.longitude", -137.117139)

    // w h e n
    val immutableTree = Map(tree.toSeq: _*)
    val actual = MapBlower(immutableTree).blowUp

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
    val personalinfo = mutable.Map[String, Any]()
    personalinfo.put("name", "Elliott Kaufman")
    personalinfo.put("gender", "male")
    personalinfo.put("phone", "+1 (858) 421-2925")
    personalinfo.put("email", "elliottkaufman@spacewax.com")
    personalinfo.put("address", "952 Cropsey Avenue, Tyro, Guam, 1787")
    expected.put("personalinfo", personalinfo)
    val company = mutable.Map[String, Any]()
    company.put("name", "SPACEWAX")
    personalinfo.put("company", company)
    val location = mutable.Map[String, Any]()
    location.put("latitude", 78.370719)
    location.put("longitude", -137.117139)
    expected.put("location", location)

    actual shouldBe expected

  }

}
