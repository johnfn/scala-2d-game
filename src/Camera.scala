class Camera(x:Int, y:Int, width:Int, height:Int) {
  
  def update() = {
    import org.lwjgl._
    import opengl.{Display,GL11,DisplayMode}
    import GL11._;
    
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity
    glOrtho(0, width, height, 0, -.5, .5);

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glViewport(-x, y, width, height)
  }
}