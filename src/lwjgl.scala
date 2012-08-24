import org.lwjgl._
import opengl.{ Display, GL11, DisplayMode }
import GL11._
import input._
import math._
import Keyboard._
import scala.util.control.Breaks._

//TODO: FPS Counter
//TODO: FPS Limiter

object Main {
  val GAME_TITLE = "My Game"
  val FRAMERATE = 60
  val MAP_WIDTH: Int = 40;
  val MAP_HEIGHT: Int = 40;

  val width = 440
  val height = 480

  val TILE_SIZE: Int = 20;

  initDisplay(); // This needs to come first for any texture or graphical things to work.

  var texLoader: TextureLoader = new TextureLoader();

  val player = new Player(100, 100, TILE_SIZE, TILE_SIZE)
  var cam: Camera = new Camera(0, 0, width, height);
  val ss: Spritesheet = new Spritesheet("assets/derp.png", TILE_SIZE, texLoader);
  var map: Map = new Map(0, 0, MAP_WIDTH, MAP_HEIGHT, ss, TILE_SIZE);
  val manager = new Manager()

  var finished = false;

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
      ss.render(0, 0);
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

    init()
    run()
    gameOver()
  }

  def initDisplay() = {
    Display.setTitle(GAME_TITLE)
    Display.setFullscreen(false)
    Display.setVSyncEnabled(true)
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.create()
  }

  def getTime(): Long = {
    System.nanoTime();
  }

  var lastTime: Long = getTime();
  var tix: Long = 0;

  def getFPS() = {
    if (getTime() - lastTime > 1000000000) {
      println(tix);
      tix = 0;
      lastTime = getTime();
    } else {
      tix += 1;
    }
  }

  def init() {
    //glDisable(GL_DEPTH_TEST) //This may annoy me in the future.
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
    cam.setBounds(0, 0, map.width, map.width);
    val t: Text = new Text("FPS", 0, 0);

    glEnable(GL_TEXTURE_2D);
    
    while (!(isKeyDown(KEY_ESCAPE) || Display.isCloseRequested)) {
      getFPS();

      glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

      manager.update_all()
      manager.draw_all()

      //t.render();
      
      glDisable(GL_BLEND);

      cam.move(player.x - cam.width / 2, player.y - cam.height / 2);
      cam.update()

      Display.update()

      Display.sync(FRAMERATE)
    }
  }
}
