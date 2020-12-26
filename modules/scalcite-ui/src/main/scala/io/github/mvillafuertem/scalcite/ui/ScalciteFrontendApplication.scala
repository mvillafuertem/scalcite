package io.github.mvillafuertem.scalcite.ui

import org.scalajs.dom
import slinky.hot
import slinky.web.ReactDOM

import scala.scalajs.js.annotation.{ JSExportTopLevel, JSImport }
import scala.scalajs.{ js, LinkingInfo }

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object ScalciteFrontendApplication {
  val css = IndexCSS

  @JSExportTopLevel("main")
  def main(): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    ReactDOM.render(App(), container)
  }
}
