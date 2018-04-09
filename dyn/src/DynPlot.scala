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

    val cnvs = canvas(style := "border:1px solid #00ffff;").render
    cnvs.height = height;
    cnvs.width = width;
    implicit val ctx = cnvs.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    var a = 1
    var b = 0
    var c = 0
    var d = -1

    var scale = 100
    var step = 0.01
    var interval = 10

    val aI = input(size := "5",value := a).render
    val bI = input(size:= "5", value := b).render
    val cI = input(size:= "5", value := c).render
    val dI = input(size:= "5", value := d).render


    val tab =
      div(`class` := "col-md-3")(
        h3("Matrix:"),
        table(`class` := "table")(
      tr(td(strong("a = "), aI), td(strong("b = "), bI)),
      tr(td(strong("c = "), cI), td(strong("d = "), dI))
    )
  )


    jsDiv.appendChild(
      div(cnvs, tab).render
    )

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

    // drawPath(testPath, 200)

    var point = (0.0, 0.0)

    var dyn = Matrix(a, b, c, d)

    def animateDyn(init: Point) = {
      point = init
      ctx.beginPath()
      ctx.lineWidth = 2
      ctx.strokeStyle = "blue"

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
            // console.log(x)
            // console.log(y)
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

    var id = animateDyn((1, 1))

    def ctop = cnvs.getBoundingClientRect.top
    def cleft = cnvs.getBoundingClientRect.left

    def stop() = dom.window.clearInterval(id)

    aI.onchange = (event: dom.Event) => {
      stop()
      a = aI.value.toInt
      init()
      dyn = Matrix(a, b, c, d)
    }

    bI.onchange = (event: dom.Event) => {
      stop()
      b = bI.value.toInt
      init()
      dyn = Matrix(a, b, c, d)
    }


    cI.onchange = (event: dom.Event) => {
      stop()
      c = cI.value.toInt
      init()
      dyn = Matrix(a, b, c, d)
    }

    dI.onchange = (event: dom.Event) => {
      stop()
      d = dI.value.toInt
      init()
      dyn = Matrix(a, b, c, d)
    }


    dyn = Matrix(a, b, c, d)

    cnvs.onclick = {
      (event) =>
        stop()
        val xpos = event.pageX - cleft
        val ypos = event.pageY - ctop
        val x = (xpos - (width/2))/scale
        val y = -(ypos - (height/2))/scale
        // console.log(x)
        // console.log(y)
        id = animateDyn((x, y))
        // console.log(event.pageX)
        // console.log(event.pageY)
        // console.log(cleft)
        // console.log(ctop)
        // console.log(y)

      }
  }
}
