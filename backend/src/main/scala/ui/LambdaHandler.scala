package ui
import net.exoego.facade.aws_lambda._
import slinky.web.ReactDOMServer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._
import scala.scalajs.js._


object LambdaHandler {

  @JSExportTopLevel(name = "LambdaHandler")
  def handle(event: APIGatewayProxyEvent, context: Context): Promise[APIGatewayProxyResult] = {
    val headers :scala.scalajs.js.Dictionary[Boolean | Double | String] = Dictionary.apply(( "Content-Type", "text/html"))
    val o = for{
      out <- Future.successful(
        ReactDOMServer.renderToString(
          shared.components.Page()
        )
      )
    } yield out
    o.map(s => APIGatewayProxyResult.apply(body = s,statusCode = 200, headers = headers, isBase64Encoded = false, multiValueHeaders = ())).toJSPromise
  }

}


/**
 *  val dynamo_SDK = new DynamoDB
    val tableName: String = "Questionaire"
    val o = for{
      a <- dynamo_SDK.getItemFuture(GetItemInput(TableName = tableName,Key =
        Dictionary{"UUID" -> AttributeValue("e1aa3978-b76d-4763-aaa4-79a3372efef6")}
        ))
 */