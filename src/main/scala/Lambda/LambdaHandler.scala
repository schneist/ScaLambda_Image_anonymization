package Lambda

import com.comcast.ip4s.IpAddress
import net.exoego.facade.aws_lambda.{APIGatewayProxyEvent, APIGatewayProxyResult, Context}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.annotation.JSExportTopLevel
import com.comcast.ip4s.{IpAddress, Ipv4Address, Ipv6Address}
import typings.xlsx.mod.{ParsingOptions, WorkSheet}
import ujson._

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, JSON}
import scala.scalajs.js.annotation._
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.Try
object LambdaHandler {

  case class entry(ip:Option[IpAddress],comment:String)

  @JSExportTopLevel(name = "LambdaHandler")
  def handle(event: APIGatewayProxyEvent, context: Context): Promise[APIGatewayProxyResult] = {
    val headers: scala.scalajs.js.Dictionary[Boolean | Double | String] = Dictionary.apply(("Content-Type", "text/html"))
    val FirstRow = js.Dynamic.global.process.env.FIRST_ROW.asInstanceOf[js.UndefOr[String]]

    o.map(s => APIGatewayProxyResult.apply(body = s, statusCode = 200, headers = headers, isBase64Encoded = false, multiValueHeaders = ())).toJSPromise
  }


  def read = (dyn: Uint8Array) => {
    val workbook = typings.xlsx.mod.read(dyn, ParsingOptions().set("type", "array"));
    val worksheet = workbook.Sheets.head._2
    typings.xlsx.mod.utils.sheet_to_html(worksheet)
    val json: js.Array[js.Array[js.Any]] = typings.xlsx.mod.utils.sheet_to_json(worksheet)
    val jj = ujson.read(JSON.stringify(json))
    val collectionE = scala.collection.mutable.ArrayBuffer[entry]()
    jj match {
      case a: ujson.Arr => {
        a.value.foreach(_ match {
          case o: Obj => {
            val obj = o.value
            if (obj.contains("__EMPTY_4")) {
              collectionE.append(entry(
                obj.get("__EMPTY_4").map(_.toString.replaceAll("[^\\d.]", "").replace('"'.toString, "")).flatMap(Ipv4Address.fromString),
                obj.map(_._2).mkString
              ))
            }
            if (obj.contains("__EMPTY_5")) {
              collectionE.append(entry(
                obj.get("__EMPTY_5").map(_.toString.replace('"'.toString, "")).flatMap(Ipv6Address.fromString),
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
    "aws:SourceIP:\r\n" + collectionS.mkString
    //
  }

}
