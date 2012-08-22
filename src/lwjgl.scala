import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import input._
import math._
import Keyboard._
import scala.util.control.Breaks._

object Main{
  val GAME_TITLE = "My Game"
  val FRAMERATE = 60
  val width = 640
  val height = 480
  
  var texLoader:TextureLoader = new TextureLoader();

  val player = new Player(100, 100, 20, 20)
  var cam:Camera = new Camera(0, 0, width, height);
  var map:Map = null; // needs to be initialized after window is created.
  val manager = new Manager()

  var finished = false
  
  def initializeMap() = {
	  map = new Map(0, 0, width / 20, height / 20)
  }

  trait controllable extends Entity{
    def control() = {
      var rx = 0
      var ry = 0

      if(isKeyDown(KEY_I)) ry += 1
      if(isKeyDown(KEY_K)) ry -= 1
      if(isKeyDown(KEY_J)) rx -= 1
      if(isKeyDown(KEY_L)) rx += 1

    }
  }

  // I'll probably never use these  >_> <_< <_>
  def any(l:List[Boolean]) = l contains true
  def all(l:List[Boolean]) = !(l contains false)

  class Point(val x: Int, val y: Int) {
    override def toString() = {
      "Point (x : " + x + ", y : " + y + ")"
    }
  }

  class Manager() {
    var entities:List[Entity] = List()

    def get(entity_type:String):List[Entity] = {
      entities.filter(e => e.traits.contains(entity_type))
    }

    def one(entity_type:String):Entity = {
      val list:List[Entity] = get(entity_type)
      assert(list.length == 1)

      list(0)
    }

    def add(entity:Entity):Unit = {
      entities = entities :+ entity
    }

    def update_all():Unit = {
      get("update").map(_.update(this))
    }

    def draw_all():Unit = {
      get("draw").sortBy(_.depth).foreach(_.draw)
    }
  }

  abstract class Entity(var x:Int, var y:Int, var width:Int, var height:Int) {
    var vx:Int = 0
    var vy:Int = 0
    var traits:List[String] = List("update", "draw")

    def touchesPoint(p:Point):Boolean = {
      x <= p.x && p.x <= x + width && y <= p.y && p.y <= y + height
    }

    def touchesEntity(other:Entity):Boolean = {
       ( touchesPoint(new Point(other.x, other.y))
      || touchesPoint(new Point(other.x, other.y + other.height))
      || touchesPoint(new Point(other.x + other.width, other.y))
      || touchesPoint(new Point(other.x + other.width, other.y + other.height)))
    }

    def render;
    def update(m:Manager);
    def depth:Int;

    /* Overridden in stuff like Map to be more accurate */
    def collidesWith(other:Entity):Boolean = {
      assert(other.width == width)
      assert(other.height == height)

      touchesEntity(other)
    }

    def draw = {
      glPushMatrix()
      glTranslatef(x, y, 0)
      render
      glPopMatrix()
    }
  }

  class Tile(x:Int, y:Int, width:Int, height:Int, t:Int) extends Entity(x, y, width, height) {
    val texture:Texture = texLoader.getTexture("/Users/grantm/eclipse/scala-game/assets/derp.png");
    
    override def update(m:Manager) = {}

    override def render = {
      glColor3f(0.0f, 1.0f, 1.0f)
      glEnable(GL_TEXTURE_2D);
      texture.bind();
      
      glBegin(GL_QUADS)
  	      glTexCoord2f(0, 0);
	      glVertex2f(0, 0);
	      glTexCoord2f(0, texture.getHeight());
	      glVertex2f(0, height);
	      glTexCoord2f(texture.getWidth(), texture.getHeight());
	      glVertex2f(width,height);
	      glTexCoord2f(texture.getWidth(), 0);
	      glVertex2f(width,0);
      glEnd()
    }

    def isWall:Boolean = t == 0

    override def depth:Int = 5;

    override def collidesWith(other:Entity):Boolean = {
      isWall && touchesEntity(other)
    }
  }

  class Map(x:Int, y:Int, width:Int, height:Int) extends Entity(x, y, width, height) {
    traits = List("update", "draw", "map")
    var data:Array[Array[Int]] = Array.ofDim[Int](width.toInt, height.toInt).zipWithIndex.map {case (elem, i) => elem.map((e) => 1)}
    for (j <- 0 until 5) {
      data(j)(height - 1) = 0
    }

    data(12)(14) = 0

    var tiles:Array[Tile] = data.zipWithIndex.map{ case (line, i) => line.zipWithIndex.map{ case (elem, j) => new Tile(i * 20, j * 20, 20, 20, elem)}}.reduce(_ ++ _)

    override def draw = {
      tiles.map(e => e.draw)
    }

    override def render() = {}
    override def update(m:Manager) = {}
    override def depth:Int = 5;

    override def collidesWith(e:Entity):Boolean = tiles.count(_.collidesWith(e)) > 0
    override def touchesPoint(p:Point):Boolean = {
      tiles.count((t) => t.touchesPoint(p) && t.isWall) > 0
    }
  }

  def sign(num:Int):Int = {
    if (num > 0) {
      1
    } else if (num < 0) {
      -1
    } else {
      0
    }
  }

  class Player(_x:Int, _y:Int, width:Int, height:Int) extends Entity(_x, _y, width, height){
    val speed = 5
    def render = {
      glColor3f(1.0f,1.0f,1.0f)

      glBegin(GL_QUADS)
      glVertex2f(0, 0)
      glVertex2f(0, height)
      glVertex2f(width, height)
      glVertex2f(width, 0)
      glEnd()
    }

    override def depth:Int = 99;

    def onGround(m:Manager):Boolean = {
      val gameMap = manager.one("map")
      (x to (x + width)).map(new Point(_, y + height + 3)).map(gameMap.touchesPoint(_)).reduce(_ || _)
    }

    def update(m:Manager) = {
      val map = manager.one("map")
      var ry = 5
      var rx = 0

      if (isKeyDown(KEY_W)) ry -= speed
      if (isKeyDown(KEY_S)) ry += speed
      if (isKeyDown(KEY_A)) rx -= speed
      if (isKeyDown(KEY_D)) rx += speed

      if (isKeyDown(KEY_SPACE) && onGround(m)) {
        println("gotcha")
      }

      val dx = sign(rx)
      val dy = sign(ry)

      while (rx != 0 && !map.collidesWith(this)) {
        x += dx
        rx -= dx
      }
      x -= dx

      while (ry != 0 && !map.collidesWith(this)) {
        y += dy;
        ry -= dy
      }
      y -= dy
    }
  }

  def main(args:Array[String]){
    var fullscreen = false
    for(arg <- args){
      arg match{
        case "-fullscreen" =>
          fullscreen = true
      }
    }

    init(fullscreen)
    run()
    gameOver()
  }

  def init(fullscreen:Boolean){
    Display.setTitle(GAME_TITLE)
    Display.setFullscreen(fullscreen)
    Display.setVSyncEnabled(true)
    Display.setDisplayMode(new DisplayMode(width,height))
    Display.create

    //glDisable(GL_DEPTH_TEST) //This may annoy me in the future.
    //glEnable(GL_LIGHTING)
    //glEnable(GL_LIGHT0)    

    initializeMap();
    
    manager.add(player)
    manager.add(map)
  }

  def gameOver() {
    Display.destroy()
    System.exit(0)
  }

  def cleanup(){
    Display.destroy
  }

  def run(){
    while(!(isKeyDown(KEY_ESCAPE) || Display.isCloseRequested)) {
      Display.update

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      
      cam.update()

      manager.update_all()
      manager.draw_all()

      Display.sync(FRAMERATE)
    }
  }
}
