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
  
  initDisplay(); // This needs to come first for any texture or graphical things to work.
  
  var texLoader:TextureLoader = new TextureLoader();

  val player = new Player(100, 100, 20, 20)
  var cam:Camera = new Camera(0, 0, width, height);
  var map:Map = null; // needs to be initialized after window is created. can't load textures until that point.
  val ss:Spritesheet = new Spritesheet("assets/derp.png", 20, texLoader);
  val t:Text = new Text();
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
    override def update(m:Manager) = {}

    override def render = {
      glColor3f(1.0f, 1.0f, 1.0f)
      
      t match {
        case 0 => ss.render(1, 0);
        case 1 => ss.render(0, 0);
        case _ => throw new Error("No value for Tile.");
      }
    }

    def isWall:Boolean = t == 0

    override def depth:Int = 5;

    override def collidesWith(other:Entity):Boolean = {
      isWall && touchesEntity(other)
    }
  }
  

  def toRGBTuple(num:Int):(Int, Int, Int) = {
    val colorStr:String = num.toString();
    return (java.awt.Color.decode(colorStr).getRed(), java.awt.Color.decode(colorStr).getRed(), java.awt.Color.decode(colorStr).getRed());
  }

  class Map(x:Int, y:Int, width:Int, height:Int) extends Entity(x, y, width, height) {
	import java.awt.image.BufferedImage;
	import javax.imageio.ImageIO;
	import java.io.File;

    traits = List("update", "draw", "map")

	val data:BufferedImage = ImageIO.read(new File("assets/map.png"));
    
    var t:Array[Array[Tile]] = Array.tabulate(20, 20)((x, y) => toRGBTuple(data.getRGB(x, y)) match {
      case (0, 0, 0) => new Tile(x * 20, y * 20, 20, 20, 0);
      case (255, 255, 255) => new Tile(x * 20, y * 20, 20, 20, 1);
      case _ => throw new Error("aderp!");
    })

    override def draw = {
      t.flatten.map(e => e.draw)
    }

    override def render() = {}
    override def update(m:Manager) = {}
    override def depth:Int = 5;

    override def collidesWith(e:Entity):Boolean = t.flatten.count(_.collidesWith(e)) > 0
    override def touchesPoint(p:Point):Boolean = {
      t.flatten.count((t) => t.touchesPoint(p) && t.isWall) > 0
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
  
  def initDisplay() = {
    Display.setTitle(GAME_TITLE)
    Display.setFullscreen(false)
    Display.setVSyncEnabled(true)
    Display.setDisplayMode(new DisplayMode(width,height))
    Display.create()
    
    glEnable(GL_TEXTURE_2D);
  }

  def init(fullscreen:Boolean){
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

      //glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      
      cam.update()

      manager.update_all()
      manager.draw_all()

      Display.sync(FRAMERATE)
      
      t.render();
    }
  }
}
