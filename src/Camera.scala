class Camera(var x: Int, var y: Int, val width: Int, val height: Int) {

  def move(newX: Int, newY: Int) = {
    this.x = newX;
    this.y = newY;
  }

  def update() = {
    import org.lwjgl._
    import opengl.{ Display, GL11, DisplayMode }
    import GL11._;

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity
    glOrtho(0, width, height, 0, -.5, .5);

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glViewport(-x, y, width, height)
  }
}