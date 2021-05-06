package lambda

import net.exoego.facade.aws_lambda._
import org.scalajs.dom
import org.scalajs.dom.{console, window}
import slinky.core._
import slinky.core.annotations._
import slinky.web.html._
import slinky.web.{ReactDOM, ReactDOMServer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._
import scala.scalajs.js.{Dictionary, Promise, _}
import scala.util.Try

case class AnswerOption(label:String,value:Int)

case class Question(text:String,answer:Option[Int],answerOptions : Seq[AnswerOption])

object components {

  val dummy = Seq(Question("Q1", Option.empty, Seq(AnswerOption("l1", 1), AnswerOption("l2", 2), AnswerOption("l3", 3))))

  @react
  object Page {

    //ToDo:: remove hard code
    val scriptloc = """https://d7yx4gnxnwmp1.cloudfront.net/scalambda-poll-fastopt-bundle.js"""

    case class Props()

    val component: FunctionalComponent[Props] = FunctionalComponent[Props] {
      props =>

        val Props() = props

        html(
          head(

          ),
          body(

            div(id :="root")(
              SurveyApp( dummy)
            ),
            script(src := scriptloc),
          ),



        )
    }

  }


  @react class SurveyApp extends Component {

    case class Props(items: Seq[Question])

    case class State(items: Seq[Question])

    override def initialState = State(props.items)

    def handleChange(e: SyntheticEvent[org.scalajs.dom.html.Input, org.scalajs.dom.Event]): Unit = {
      val eventValue = e.target.value
      eventValue
      val mod : (State, Props) => State = (s,_) => s
      setState(mod)
    }

    def handleSubmit(e: SyntheticEvent[org.scalajs.dom.html.Form, org.scalajs.dom.Event]): Unit = {
      e.preventDefault()

    }


    override def render() = {
      div(
        h3("TODO"),
        irn(state.items.head)
      )
    }

    def irn(q:Question) ={
      form(onSubmit := (handleSubmit(_)))(
        div(h3(q.text)),
        q.answerOptions.map(opt => button(name := opt.value.toString,opt.label))
      )
    }

  }
}

object Main extends App {

  console.log("Init main:")

  if ( Try{! js.isUndefined(js.Dynamic.global.window)}.getOrElse(false)){
    console.log("hydrate " )
    hydrate()
  }

  @JSExportTopLevel("hydrate")
  def hydrate(): Unit = {
    val container = dom.document.getElementById("root")
    ReactDOM.hydrate(
      components.SurveyApp(components.dummy),
      container
    )
  }

}

object LambdaHandler {

  @JSExportTopLevel(name = "LambdaHandler")
  def handle(event: APIGatewayProxyEvent, context: Context): Promise[APIGatewayProxyResult] = {
    val headers :scala.scalajs.js.Dictionary[Boolean | Double | String] = Dictionary.apply(( "Content-Type", "text/html"))
    val o = for{
      out <- Future.successful(
        ReactDOMServer.renderToString(
          components.Page()
        )
      )
    } yield out
    o.map(s => APIGatewayProxyResult.apply(body = s,statusCode = 200, headers = headers, isBase64Encoded = false, multiValueHeaders = ())).toJSPromise
  }

}
