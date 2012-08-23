class Camera(var x: Int, var y: Int, val width: Int, val height: Int) {
  var boundX = 0;
  var boundY = 0;
  var boundWidth = width;
  var boundHeight = height;
  
  this.move(x, y);

  def move(newX: Int, newY: Int) = {
    this.x = newX;
    
    if (this.x < this.boundX) this.x = this.boundX;
    if (this.x + this.width > this.boundX + this.boundWidth) this.x = this.boundX + this.boundWidth - this.width;
    
    this.y = newY;
    
    if (this.y < this.boundY) this.y = this.boundY;
    if (this.y + this.height > this.boundY + this.boundHeight) this.y = this.boundY + this.boundHeight - this.height;
  }

  def setBounds(boundX: Int, boundY: Int, boundWidth: Int, boundHeight: Int) = {
	  this.boundX = boundX;
	  this.boundY = boundY;
	  this.boundWidth = boundWidth;
	  this.boundHeight = boundHeight;
  }

  def update() = {
    import org.lwjgl._
    import opengl.{ Display, GL11, DisplayMode }
    import GL11._;

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity
    glOrtho(x, width + x, height + y, y, -.5, .5);

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glViewport(0, 0, width, height)
  }
}