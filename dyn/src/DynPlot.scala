package dyn

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

//import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation._


@JSExportTopLevel("DynPlot")
object DynPlot{
  type Point = (Double, Double)
  def solvSeq(init: Point, dyn: Point => Point, scale: Double, length: Int = 1000): Vector[(Double, Double)] =
    (0 until length).foldRight[Vector[Point]](Vector(init)){
      case (_, seq) =>
       val (x, y) = seq.last
        val (x1, y1) = dyn((x, y))
        seq :+ (x + x1 * scale, y + y1 * scale)
    }

  case class Matrix(a: Double, b: Double, c: Double, d: Double) extends (Point => Point) {
      def apply(xy: Point): (Double, Double) =  (a * xy._1 + b * xy._2, c * xy._1 + d * xy._2)
    }

  @JSExport
  def main(): Unit = {

    val jsDiv = document.getElementById("js-div")

    val height = 500
    val width = 500

    val cnvs = canvas(style := "border:1px solid #00ffff;").render
    cnvs.height = height
    cnvs.width = width
    implicit val ctx: CanvasRenderingContext2D = cnvs.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    def ctop: Double = cnvs.getBoundingClientRect.top
    def cleft: Double = cnvs.getBoundingClientRect.left

    def init(): Unit = {
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


    def drawPath(xys: Seq[(Double, Double)], scale: Double, lw: Int = 1, col: String = "green"): Unit = {
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


    var a = 1.0
    var b = 0.0
    var c = 0.0
    var d = -1.0

    val scale = 100
    val step = 0.002
    val interval = 1

    var point : Point = (0.0, 0.0)

    var dyn: Point => Point = Matrix(a, b, c, d)

    val aI: Input = input(size := "5",value := a).render
    val bI: Input = input(size:= "5", value := b).render
    val cI: Input = input(size:= "5", value := c).render
    val dI: Input = input(size:= "5", value := d).render


    val tab =
      div(`class` := "col-md-3")(
        h3("Matrix:"),
        table(`class` := "table")(
      tr(td(strong("a = "), aI), td(strong("b = "), bI)),
      tr(td(strong("c = "), cI), td(strong("d = "), dI))
    )
  )

    val pause =
        button(`class` := "btn btn-danger")("Stop").render

    val clear =
        button(`class` := "btn btn-warning")("clear").render

    val resume =
        button(`class` := "btn btn-success")("continue").render

    val paths =
        button(`class` := "btn btn-primary")("random paths").render


    jsDiv.appendChild(
      div(cnvs, tab,
        div(`class` := "row")(pause, span(" "), clear, span(" "), resume, span(" "), paths)
      ).render
    )




    def animateDyn(init: Point): Int = {
      point = init
      ctx.beginPath()
      ctx.lineWidth = 2
      ctx.strokeStyle = "blue"

      val animId =
        dom.window.setInterval(
        handler = () => {
          val (x0, y0) = point
          ctx.moveTo(x0 * scale + width / 2, height / 2 - (y0 * scale))
          val x1 = dyn((x0, y0))._1
          val y1 = dyn((x0, y0))._2
          val x = x0 + (x1 * step)
          val y = y0 + (y1 * step)
          point = (x, y)
          ctx.lineTo(x * scale + width / 2, height / 2 - (y * scale))
          ctx.stroke()
        },
          timeout = interval
      )
      animId
    }

    init()

    var id = animateDyn((1, 1))

    def stop(): Unit = dom.window.clearInterval(id)
    


    aI.onchange = (_: dom.Event) => {
      stop()
      a = aI.value.toDouble
      init()
      dyn = Matrix(a, b, c, d)
    }

    bI.onchange = (_: dom.Event) => {
      stop()
      b = bI.value.toDouble
      init()
      dyn = Matrix(a, b, c, d)
    }


    cI.onchange = (_: dom.Event) => {
      stop()
      c = cI.value.toDouble
      init()
      dyn = Matrix(a, b, c, d)
    }

    dI.onchange = (_: dom.Event) => {
      stop()
      d = dI.value.toDouble
      init()
      dyn = Matrix(a, b, c, d)
    }

    pause.onclick = (_) => stop()

    resume.onclick = (_) => {id = animateDyn(point)}

    clear.onclick = (_) => init()

    dyn = Matrix(a, b, c, d)

    lazy val rnd = new scala.util.Random

    def showRandom(n: Int = 500, length: Int = 1000): Unit = {
      (1 to n).foreach{(_) =>
        val x0 = rnd.nextDouble() * 10 - 5
        val y0 = rnd.nextDouble() * 10 - 5
        // console.log(x0)
        // console.log(y0)
        drawPath(solvSeq((x0, y0), dyn, step, length), scale)
      }
    }

    paths.onclick = (_) => showRandom()

    cnvs.onclick = {
      (event) =>
        stop()
        val xpos = event.pageX - cleft
        val ypos = event.pageY - ctop
        val x = (xpos - (width/2))/scale
        val y = -(ypos - (height/2))/scale
        id = animateDyn((x, y))
      }
  }
}
