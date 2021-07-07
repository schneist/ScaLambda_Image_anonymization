package ui
import org.scalajs.dom
import org.scalajs.dom.console
import slinky.web.ReactDOM
import typings.xlsx.mod.ParsingOptions

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import scala.scalajs.js.typedarray.Uint8Array

object Main extends App {


  console.log("hydrating ... " )
  hydrate()


  @JSExportTopLevel("hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")
    ReactDOM.hydrate(
      shared.components.Page.content(shared.components.Page.Props("",read)),
      container
    )
  }

  def read = (dyn:Uint8Array) => {
    val workbook = typings.xlsx.mod.read(dyn,ParsingOptions().set("type","array"));
    val worksheet = workbook.Sheets.head._2
    typings.jsYaml.mod.dump(typings.xlsx.mod.utils.sheet_to_json(worksheet))
  }

}

