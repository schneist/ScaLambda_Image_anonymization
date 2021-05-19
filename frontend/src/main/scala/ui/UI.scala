package ui
import org.scalajs.dom
import org.scalajs.dom.console
import slinky.web.ReactDOM

import scala.scalajs.js.annotation._

object Main extends App {

  console.log("hydrate " )
  hydrate()

  @JSExportTopLevel("hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")
    ReactDOM.hydrate(
      shared.components.SurveyApp(shared.components.dummy),
      container
    )
  }

}

