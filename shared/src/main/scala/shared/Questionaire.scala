package shared
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, UIEvent}
import org.scalajs.dom.{FileReader, console, window}
import slinky.core._
import slinky.core.annotations._
import slinky.core.facade.{React, ReactElement}
import slinky.web.html._

import java.util.UUID
import scala.scalajs.js
import scala.scalajs.js.Array.isArray
import scala.scalajs.js.JSON
import scala.scalajs.js.Object.{create, entries, keys}
import scala.scalajs.js.typedarray.Uint8Array
import slinky.core.facade.Hooks._
import typings.std.global.TextEncoder


object components {


  @react
  object Page {

    val scriptloc = """https://d7yx4gnxnwmp1.cloudfront.net/scalambda-poll-fastopt-bundle.js"""

    case class Props(raw:String,reader: Uint8Array => String)

    val component: FunctionalComponent[Props] = FunctionalComponent[Props] {
      props =>
        html(
          head(
            style("""
                  #drop { border: 10px dashed #ccc; width: 300px; height: 300px; margin: 20px auto;}
                  #drop.hover { border: 10px dashed #333; }
                  #rawtext {width: 100%; height: 40%px;}
                  """)
          ),
          body(
            div(id := "root",
              content.apply(props)
            ),
            script(src := scriptloc)
          )
        )
    }



    val content:  FunctionalComponent[Props] = FunctionalComponent[Props] {
      props => {
        val (state, updateState) = useState(props)
        div(id := "content",
          div(id := "drop",
            onMouseOver := { e =>
              e.preventDefault()
            },
            onDragEnter := { e =>
              e.preventDefault()
            },
            onDragOver := { e =>
              e.preventDefault()
            },
            onDragEnd := { e =>
              e.preventDefault()
            },
            onDrop := { e => {
              e.preventDefault()
              val file = e.asInstanceOf[js.Dynamic].dataTransfer.files.item(0)
              val reader = new FileReader()
              reader.onload = event => {
                val data = new Uint8Array(event.target.asInstanceOf[js.Dynamic].result.asInstanceOf[js.Iterable[Short]])
                updateState( Props(props.reader(data),props.reader))
              }
              reader.readAsArrayBuffer(file.asInstanceOf[Blob])
            }
            }),
            div(id := "raw",
            textarea(id := "rawtext", value := state.raw),
          )
        )
      }
    }
  }



}
