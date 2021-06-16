package lambda

import cats.effect._
import cats.implicits._
import facade.amazonaws.services.dynamodb._
import facade.amazonaws.services.rekognition._
import facade.amazonaws.services.s3._
import net.exoego.facade.aws_lambda._
import typings.sharp.mod._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichFutureNonThenable, _}
import scala.scalajs.js.annotation._
import scala.scalajs.js.{Dictionary, Promise}
import scala.util.Try
import cats.effect.IO
import colog.{HasLogger, LogRecord, Logger}
import cats.data.ReaderT
import colog.Logging
import cats.mtl.implicits._


case class Env(logger: Logger[IO, LogRecord])

object Env {

  implicit val envHasLogger: HasLogger[IO, Env, LogRecord] = new HasLogger[IO, Env, LogRecord] {
    override def getLogger(env: Env): Logger[IO, LogRecord] = env.logger

    override def setLogger(env: Env)(newLogger: Logger[IO, LogRecord]): Env =
      env.copy(logger = newLogger)
  }

}

object LambdaHandler {
  type AppIO[A] = ReaderT[IO, Env, A]
  val s3_SDK = new S3
  val rekognition_SDK = new Rekognition
  val dynamo_SDK = new DynamoDB
  val logging = Logging.structured[AppIO, Env]

  val log :AppIO[Unit] = logging.info("Hello")


  def removeSubImages(sharp: Sharp,bounds:Seq[BoundingBox]) : Future[Sharp] ={
    val blur = js.Dynamic.global.process.env.BLUR.asInstanceOf[js.UndefOr[String]].toOption.exists(_.equalsIgnoreCase("true"))
    val radius = js.Dynamic.global.process.env.BLUR_RADIUS.asInstanceOf[js.UndefOr[Double]].toOption.getOrElse(25.0)
    for {
      m <- sharp.metadata().toFuture
      h <- Future.fromTry(Try{m.height.get match {
        case belowZero: Double  if belowZero <= 0  => 0
        case good: Double => good
      }})
      w <-  Future.fromTry(Try{m.width.get match {
        case belowZero: Double  if belowZero <= 0  => 0
        case good: Double => good
      }})
      extracted <- bounds.map (b => {
        val boxWidth = (b.Width.get * w) match {
          case belowZero: Double  if belowZero <= 0  => 0
          case good: Double => good
        }
        val boxHeight = (b.Height.get * h) match {
          case belowZero: Double  if belowZero <= 0  => 0
          case good: Double => good
        }
        val boxLeft = (b.Left.get * w) match {
          case belowZero: Double  if belowZero <= 0  => 0
          case good: Double => good
        }
        val boxTop = (b.Top.get * h) match {
          case belowZero: Double  if belowZero <= 0  => 0
          case good: Double => good
        }
        val filler: Future[typings.node.Buffer] = if (blur) {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region.apply(boxLeft, boxTop, boxWidth, boxHeight))
            .blur(radius).toBuffer().toFuture)
        } else {
          sharp.toBuffer().toFuture.flatMap(b => typings.sharp.mod.apply(b)
            .extract(Region(boxLeft, boxTop, boxWidth, boxHeight))
            .threshold(255).toBuffer().toFuture)
        }
        filler.map( x =>  OverlayOptions.apply().setInput(x).setLeft(boxLeft).setTop(boxTop))
      }).toList.traverse(identity)
      out <- Future.successful{
        sharp.withMetadata().composite(extracted.toJSArray)
      }
    } yield out
  }

  def traverse(v: ujson.Value,Key:String): Iterable[String] = v match{
    case a: ujson.Arr => a.arr.flatMap(traverse(_,Key))
    case o: ujson.Obj if o.obj.contains(Key) => Seq(o.obj(Key).toString()+"#")
    case o: ujson.Obj => o.obj.values.flatMap(traverse(_,Key))
    case _ => Nil
  }

  @JSExportTopLevel(name = "LambdaHandler")
  def handle(event: S3Event, context: Context): Promise[Unit] = {
    val e = event.Records.toList.head.s3
    
    val filename = js.URIUtils.decodeURIComponent(e.`object`.key.replace("+"," "))
    val o = for{
      r_M <- rekognition_SDK.detectLabels(DetectLabelsRequest(Image(js.undefined,S3Object( e.bucket.name,filename)))).promise().toFuture
      labels <- Future.successful(traverse(ujson.read(js.JSON.stringify(r_M.Labels)),"Name"))
      confidences <- Future.successful(traverse(ujson.read(js.JSON.stringify(r_M.Labels)),"Confidence"))
      _ <- dynamo_SDK.putItem(PutItemInput(Dictionary("Filename" -> AttributeValue(S=filename),"RekognitionLabels"-> AttributeValue.S(labels.mkString),"Confidences" -> AttributeValue.S(confidences.mkString),"raw" -> AttributeValue.S(js.JSON.stringify(r_M.Labels))),e.bucket.name)).promise().toFuture
      s3 <- s3_SDK.getObject(GetObjectRequest( e.bucket.name,filename)).promise().toFuture
      faceList <- Future.successful(r_M.Labels.get.filter(_.Name.equals("Person")).flatMap(_.Instances.getOrElse(Array.empty[Instance].toJSArray)))
      sharp <-(s3.Body.get:Any) match {
        case s:String =>  Future.successful(typings.sharp.mod.apply(s))
        case a:scala.scalajs.js.Array[scala.Byte] =>  Future.successful(typings.sharp.mod.apply(a.map(_.toInt).join("")))
        case _:Any => {
          Future.successful(typings.sharp.mod.apply(""))
        }
      }
      removed <- removeSubImages(sharp,faceList.map(_.BoundingBox.getOrElse(BoundingBox(0.0F,0.0F,0.0F,0.0F))).toSeq) //.filter(_.Height.getOrElse(0.0F)>=0))
      buffer <- removed.toBuffer().toFuture
      out <-s3_SDK.putObjectFuture(PutObjectRequest(e.bucket.name,filename.replace("input","output"),js.undefined,buffer.toString()))
    } yield out
    o.map[Unit](_ => ()).toJSPromise
  }
}

