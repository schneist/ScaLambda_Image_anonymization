package shared
import org.scalajs.dom.console
import slinky.core._
import slinky.core.annotations._
import slinky.web.html._

import java.util.UUID

case class AnswerOption(
                         label:String,
                         value:Int
                       )

case class Question(
                     id:String = UUID.randomUUID().toString,
                     text:String,
                     answer:Option[Int],
                     answerOptions : Seq[AnswerOption]
                   )


object components {

  val dummy = Seq(
    Question(text = "Q1", answer = Option.empty, answerOptions = Seq(AnswerOption("l1", 1), AnswerOption("l2", 2), AnswerOption("l3", 3))),
    Question(text = "Q2", answer = Option.empty, answerOptions = Seq(AnswerOption("l1", 1), AnswerOption("l2", 2), AnswerOption("l3", 3)))
  )

  @react
  object Page {

    val scriptloc = """https://d7yx4gnxnwmp1.cloudfront.net/scalambda-poll-fastopt-bundle.js"""

    case class Props(questions:scala.collection.immutable.Seq[Question])

    val component: FunctionalComponent[Props] = FunctionalComponent[Props] {
      props =>


        html(
          head(
          ),
          body(

            div(id :="root")(
              SurveyApp( props.questions)
            ),
            script(src := scriptloc),
          ),
        )
    }
  }

  @react class SurveyApp extends Component {

    def answer(question: Question): Unit = {
      setState(state => {
        State(state.items.updated(question.id, question))
      })
    }

    case class Props(items: scala.collection.immutable.Seq[Question])

    case class State(items: scala.collection.immutable.Map[String,Question])

    override def initialState = State(props.items.map(q => (q.id,q)).toMap)

    override def render() = {
      console.log("redner")
      val filtered = state.items.filter(_._2.answer.isEmpty)
      console.log(filtered)
      div(
        h3(filtered.head._2.id),
        Answerer(filtered.head._2,answer(_))
      )
    }

  }

  @react class Answerer extends Component {
    case class Props(item:Question,parent:Question => Unit)

    case class State(item:Question,parent:Question => Unit)

    override def initialState =State(props.item,props.parent)

    override def componentWillReceiveProps(nextProps: Props): Unit = setState(State(nextProps.item,nextProps.parent))


    def handleSubmit(e: SyntheticEvent[org.scalajs.dom.html.Form, org.scalajs.dom.Event]): Unit = {
      e.preventDefault()
      val l = state.item.copy(answer = Some(5))
      console.log(state.parent)
      state.parent(l)
    }

    override def render() = {

      val q = state.item
      form(onSubmit := (handleSubmit(_)))(
        h3(q.text),
        q.answerOptions.map(opt => button(name := opt.value.toString,opt.label))
      )
    }

  }

}
