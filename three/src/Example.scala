package example

import org.denigma.threejs._


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

import scala.math._

import scala.scalajs.js.annotation._

import scala.scalajs.js

@JSExportTopLevel("ThreeExample")
object Example{


  @JSExport
  val  cylinder: js.Function2[Double, Double, Vector3] = (u, v) =>
    new Vector3(sin(2 * Pi * u), cos(2 * Pi *u), v)

    val scene = new Scene()
    var camera = new PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 )

    val renderer = new WebGLRenderer();

    @JSExport
    val geometry = new Geometry()

    val verts = Vector(
      new Vector3( 1,  0, 0 ),
  	  new Vector3( 0, 1, 0 ),
  	  new Vector3( 0, -1, 0 ),
      new Vector3( 0,  0,  1)
    )


  @JSExport
  def main(): Unit = {

    val jsDiv = document.getElementById("js-div")

    renderer.setSize( window.innerWidth, window.innerHeight )
    jsDiv.appendChild( renderer.domElement )

    geometry.vertices.push(verts: _*)
    val face1 = new Face3(0, 1, 2)
    val face2 = new Face3(1, 2, 3)
    face1.normal = new Vector3(0, 0, 1)
    face2.normal = new Vector3(1, 0, 0)
    geometry.faces.push(face1)
    geometry.faces.push(face2)

    // geometry.computeVertexNormals();

    val material = new MeshBasicMaterial()
    material.color = new Color(Integer.parseInt("00ff00", 16))
    var pic = new Mesh( geometry, material );
    scene.add( pic );

    camera.position.z = 5;

    def animate(ts: Double) : Unit =  {
	      window.requestAnimationFrame( animate )
        pic.rotation.x += 0.01;
        pic.rotation.y += 0.01
	      renderer.render( scene, camera )
      }
      window.requestAnimationFrame(animate)
  }
}
