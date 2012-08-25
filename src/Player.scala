class Player(_x: Int, _y: Int, width: Int, height: Int, gameMap: Map, ss:Spritesheet) extends Entity(_x, _y, width, height) {
  import Util._;
  import org.lwjgl.input.Keyboard._;
  
  val speed = 5
  def render = {
    ss.render(0, 0);
  }

  override def depth: Int = 99;

  def onGround(m: Manager): Boolean = {
    (x to (x + width)).map((_, y + height + 3)).map(gameMap.touchesPoint(_)).reduce(_ || _)
  }

  def update(m: Manager) = {
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

    while (rx != 0 && !gameMap.collidesWith(this)) {
      x += dx
      rx -= dx
    }
    x -= dx

    while (ry != 0 && !gameMap.collidesWith(this)) {
      y += dy;
      ry -= dy
    }
    y -= dy
  }
}
