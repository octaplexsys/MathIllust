package dyn

import org.scalajs.dom
import org.scalajs.dom._
import scalatags.JsDom.all._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation._


@JSExportTopLevel("DynPlot")
object DynPlot{
  type Point = (Double, Double)
  def solvSeq(init: Point, dyn: Point => Point, scale: Double, length: Int = 1000) =
    (0 until length).foldRight[Vector[Point]](Vector(init)){
      case (_, seq) =>
       {
         val (x, y) = seq.last
         val (x1, y1) = dyn((x, y))
         seq :+ (x + x1 * scale, y + y1 * scale)
       }
    }

  case class Matrix(a: Double, b: Double, c: Double, d: Double) extends (Point => Point) {
      def apply(xy: Point) =  (a * xy._1 + b * xy._2, c * xy._1 + d * xy._2)
    }

  @JSExport
  def main(): Unit = {

    val jsDiv = document.getElementById("js-div")

    val height = 400
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

    def drawPath(xys: Seq[(Double, Double)], scale: Double, lw: Int = 1, col: String = "green") = {
      ctx.beginPath()
      ctx.lineWidth = lw
      ctx.strokeStyle = col
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

    def animateDyn(init: Point, dyn: Point => Point,  step: Double,  scale: Double, interval: Double) = {
      var point = init
      ctx.beginPath()
      ctx.lineWidth = 2
      ctx.strokeStyle = "red"

      val animId =
        dom.window.setInterval(
        () =>
          {
            val (x0, y0) = point
            ctx.moveTo(x0 * scale + width/2, height/2 - (y0 * scale) )
            val x1 = dyn((x0, y0))._1
            val y1 = dyn((x0, y0))._2
            val x = x0 + (x1 * step)
            val y = y0 + (y1 * step)
            console.log(x)
            console.log(y)
            point = (x, y)
            ctx.lineTo(x * scale + width/2, height/2 - (y * scale) )
            ctx.stroke()
          },
          interval
      )
      animId
    }

    def animatePath(xys: Seq[(Double, Double)], scale: Double, interval: Double) = {
      var i = 1
      val animID =
        dom.window.setInterval(
        () =>
          {
            i += 1
            // console.log(i)
            drawPath(xys.take(i), scale, 2, "blue")
          },
          interval
      )
      animID
    }

    // val id = animatePath(testPath, 150, 1)

    val id = animateDyn((1, 1), Matrix(1, 0, 0, -1), 0.05, 100, 100 )

    dom.window.onclick = {
      (event) =>
        dom.window.clearInterval(id)
        console.log(event.pageX)
        console.log(event.pageY)
        console.log(cnvs.getBoundingClientRect.top)
        console.log(cnvs.getBoundingClientRect.left)

      }
  }
}
