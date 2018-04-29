package example

import org.denigma.threejs._


import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

import scala.scalajs.js.annotation._

@JSExportTopLevel("ThreeExample")
object Example{
  val scene = new Scene()

  val geometry = new Geometry()
  geometry.vertices.push(
    new Vector3( -10,  10, 0 ),
	  new Vector3( -10, -10, 0 ),
	  new Vector3(  10, -10, 0 )
  )

  geometry.faces.push( new Face3( 0, 1, 2 ) )

  geometry.computeBoundingSphere()

  @JSExport
  def main(): Unit = {

    val jsDiv = document.getElementById("js-div")

    val scene = new Scene()
    var camera = new PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 )

    val renderer = new WebGLRenderer();
    renderer.setSize( window.innerWidth, window.innerHeight )
    jsDiv.appendChild( renderer.domElement )

    val geometry = new BoxGeometry( 1, 1, 1 );
    val material = new MeshBasicMaterial()
    material.color = new Color(Integer.parseInt("00ff00", 16))
    var cube = new Mesh( geometry, material );
    scene.add( cube );

    camera.position.z = 5;
    cube.rotation.x += 0.3;
    cube.rotation.y += 0.3;

    renderer.render( scene, camera )
  }
}
