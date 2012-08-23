import org.lwjgl._
import opengl.{ Display, GL11, DisplayMode }
import GL11._
import input._
import math._
import Keyboard._
import scala.util.control.Breaks._

//TODO: FPS Counter
//TODO: FPS Limiter
//TODO: Decent camera following.
//TODO: Transparency??? O_O...

object Main {
  val GAME_TITLE = "My Game"
  val FRAMERATE = 60
  val MAP_WIDTH: Int = 40;
  val MAP_HEIGHT: Int = 40;

  val width = 440
  val height = 480
  
  val TILE_SIZE:Int = 20;

  initDisplay(); // This needs to come first for any texture or graphical things to work.

  var texLoader: TextureLoader = new TextureLoader();

  val player = new Player(100, 100, TILE_SIZE, TILE_SIZE)
  var cam: Camera = new Camera(0, 0, width, height);
  val ss: Spritesheet = new Spritesheet("assets/derp.png", TILE_SIZE, texLoader);
  var map: Map = new Map(0, 0, MAP_WIDTH, MAP_HEIGHT, ss, TILE_SIZE);
  val t: Text = new Text();
  val manager = new Manager()

  var finished = false

  trait controllable extends Entity {
    def control() = {
      var rx = 0
      var ry = 0

      if (isKeyDown(KEY_I)) ry += 1
      if (isKeyDown(KEY_K)) ry -= 1
      if (isKeyDown(KEY_J)) rx -= 1
      if (isKeyDown(KEY_L)) rx += 1

    }
  }

  // I'll probably never use these  >_> <_< <_>
  def any(l: List[Boolean]) = l contains true
  def all(l: List[Boolean]) = !(l contains false)

  class Point(val x: Int, val y: Int) {
    override def toString() = {
      "Point (x : " + x + ", y : " + y + ")"
    }
  }

  def sign(num: Int): Int = {
    if (num > 0) {
      1
    } else if (num < 0) {
      -1
    } else {
      0
    }
  }

  class Player(_x: Int, _y: Int, width: Int, height: Int) extends Entity(_x, _y, width, height) {
    val speed = 5
    def render = {
      glColor3f(1.0f, 1.0f, 1.0f)

      glBegin(GL_QUADS)
      glVertex2f(0, 0)
      glVertex2f(0, height)
      glVertex2f(width, height)
      glVertex2f(width, 0)
      glEnd()
    }

    override def depth: Int = 99;

    def onGround(m: Manager): Boolean = {
      val gameMap = manager.one("map")
      (x to (x + width)).map((_, y + height + 3)).map(gameMap.touchesPoint(_)).reduce(_ || _)
    }

    def update(m: Manager) = {
      val map = manager.one("map")
      var ry = 0
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

  def main(args: Array[String]) {
    var fullscreen = false
    for (arg <- args) {
      arg match {
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
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.create()

    glEnable(GL_TEXTURE_2D);
  }

  def init(fullscreen: Boolean) {
    //glDisable(GL_DEPTH_TEST) //This may annoy me in the future.
    //glEnable(GL_LIGHTING)
    //glEnable(GL_LIGHT0)    

    manager.add(player)
    manager.add(map)
  }

  def gameOver() {
    Display.destroy()
    System.exit(0)
  }

  def cleanup() {
    Display.destroy()
  }

  def run() {
    while (!(isKeyDown(KEY_ESCAPE) || Display.isCloseRequested)) {

      //glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      manager.update_all()
      manager.draw_all()

      t.render();
      
      cam.move(player.x - cam.width / 2, player.y - cam.height / 2);
      cam.update()

      Display.update()
      
      Display.sync(FRAMERATE)
    }
  }
}
