package ui
import com.comcast.ip4s.{IpAddress, Ipv4Address, Ipv6Address}
import org.scalajs.dom
import org.scalajs.dom.console
import slinky.web.ReactDOM
import typings.xlsx.mod.{ParsingOptions, WorkSheet}
import ujson._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.Try

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




  case class entry(ip:Option[IpAddress],comment:String)


  def read = (dyn:Uint8Array) => {
    val workbook = typings.xlsx.mod.read(dyn,ParsingOptions().set("type","array"));
    val worksheet = workbook.Sheets.head._2
    typings.xlsx.mod.utils.sheet_to_html(worksheet)
    val json :js.Array[js.Array[js.Any]] = typings.xlsx.mod.utils.sheet_to_json(worksheet)
    val jj = ujson.read(JSON.stringify(json))
    val collectionE = scala.collection.mutable.ArrayBuffer[entry]()
    jj match {
      case a :ujson.Arr => {
        a.value.foreach(_ match {
          case o: Obj => {
            val obj = o.value
            if (obj.contains("__EMPTY_4")) {
              collectionE.append(entry(
                obj.get("__EMPTY_4").map(_.toString.replaceAll("[^\\d.]", "").replace('"'.toString,"")).flatMap(Ipv4Address.fromString),
                obj.map(_._2).mkString
              ))
            }
            if (obj.contains("__EMPTY_5")) {
              collectionE.append(entry(
                obj.get("__EMPTY_5").map(_.toString.replace('"'.toString,"")).flatMap(Ipv6Address.fromString),
                obj.map(_._2).mkString
              ))
            }
          }
          case _ =>
        })
      }
      case _ =>
    }
    val collectionS = collectionE.filter(_.ip.isDefined).map(ent => "\t- " + ent.ip.get + " # " + ent.comment + "\r\n")
    "aws:SourceIP:\r\n" +  collectionS.mkString
    //
  }

}

