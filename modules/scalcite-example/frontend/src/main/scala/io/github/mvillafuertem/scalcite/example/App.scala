package io.github.mvillafuertem.scalcite.example

import slinky.core._
import slinky.core.annotations.react
import slinky.web.html.select
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("resources/App.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

@JSImport("resources/logo.svg", JSImport.Default)
@js.native
object ReactLogo extends js.Object

@react class App extends StatelessComponent {
  type Props = Unit

  private val css = AppCSS

  def render() = {
    div(className := "App")(
      header(className := "App-header")(
        img(src := ReactLogo.asInstanceOf[String], className := "App-logo", alt := "logo"),
        h1(className := "App-title")("Welcome to React (with Scala.js!)")
      ),
      p(className := "App-intro")(
        "To get started, edit ", code("App.scala"), " and save to reload."
      ),
      div(className := "App-body")(
        ul()(
          li()(a(className := "active", href := "")("Queries")),
          li()(a(href := "")("Simulate"))
        ),
        div(className := "App-content")(
          select(className := "App-select", size := "3", multiple)(
            option(value := "field.boolean")("field.boolean"),
            option(value := "field.string")("field.string")
          )
        )
      )
    )
  }
}
