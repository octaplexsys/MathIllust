package dyn

import org.scalajs.dom
import org.scalajs.dom._
import scalatags.JsDom.all._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation._


@JSExportTopLevel("DynPlot")
object DynPlot{

  @JSExport
  def main(): Unit = {

    val jsDiv = document.getElementById("js-div")

    val height = 600
    val width = 1000

    implicit val cnvs = canvas(style := "border:1px solid #00ffff;").render
    cnvs.height = height;
    cnvs.width = width;
    implicit val ctx = cnvs.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    jsDiv.appendChild(cnvs)

    def init() = {
      ctx.fillStyle = "white"
      ctx.fillRect(0, 0, width, height)
      ctx.beginPath()
      ctx.lineWidth = 2
      ctx.strokeStyle = "black"
      ctx.moveTo(0,  height/2)
      ctx.lineTo(width, height/2)
      ctx.stroke()
      ctx.moveTo(width/2, 0)
      ctx.lineTo(width/2, height)
      ctx.stroke()
    }

    def drawPath(xys: Seq[(Double, Double)], scale: Double) = {
      ctx.beginPath()
      ctx.lineWidth = 1
      ctx.strokeStyle = "blue"
      val (x0, y0) = xys.head
      ctx.moveTo(x0 * scale + width/2, height/2 - (y0 * scale) )
      xys.tail.foreach{
        case (x, y) => ctx.lineTo(x * scale + width/2, height/2 - (y * scale) )
      }
      ctx.stroke()
    }

    init()

    val testPath = (1 to 1000).map{(n) =>
      val x = n.toDouble/1000
      val y = x * x
      (x, y)
    }

    drawPath(testPath, 200)

    def animatePath(xys: Seq[(Double, Double)], scale: Double, interval: Double = 20) = {
      var i = 1
      val animID =
        dom.window.setInterval(
        () =>
          {
            i += 1
            // console.log(i)
            drawPath(xys.take(i), scale)
          },
          interval
      )
      animID
    }

    val id = animatePath(testPath, 100, 1)

    dom.window.onclick = {
      (event) =>
        dom.window.clearInterval(id)
        console.log(event.pageX)
        console.log(event.pageY)
      }
  }
}
