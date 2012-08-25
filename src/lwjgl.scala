import org.lwjgl._
import opengl.{ Display, GL11, DisplayMode }
import GL11._
import input._
import math._
import Keyboard._
import scala.util.control.Breaks._

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

  val t: Text = new Text("FPS", 0, 0);
  var cam: Camera = new Camera(0, 0, width, height);
  val ss: Spritesheet = new Spritesheet("assets/derp.png", TILE_SIZE, texLoader);
  var map: Map = new Map(0, 0, MAP_WIDTH, MAP_HEIGHT, ss, TILE_SIZE);
  val player = new Player(100, 100, TILE_SIZE, TILE_SIZE, map, ss);
  val manager = new Manager()

  var finished = false;

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

  def updateFPS() = {
    if (getTime() - lastTime > 1000000000) {
      t.setText(tix.toString());
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

    glEnable(GL_TEXTURE_2D);
    
    while (!(isKeyDown(KEY_ESCAPE) || Display.isCloseRequested)) {
      updateFPS();
      
      glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

      manager.update_all()
      manager.draw_all()

      t.render();
      
      glDisable(GL_BLEND);

      cam.move(player.x - cam.width / 2, player.y - cam.height / 2);
      cam.update()

      Display.update()

      Display.sync(FRAMERATE)
    }
  }
}
