class Spritesheet(file:String, val tileSize:Int, texLoader:TextureLoader) {
  // The only reason we load in tex is so we can grab it's width/height. Slightly inefficient, but saves us trouble.
  val tex:Texture = texLoader.getTexture(file);
  
  val widthInTiles:Float  = (tex.getImageWidth()  / tileSize);
  val heightInTiles:Float = (tex.getImageHeight() / tileSize);
  
  // Renders the tile with top left coordinate srcX, srcY.
  def render(srcX:Int, srcY:Int) {
    import org.lwjgl.opengl.GL11._;
    
    val topRightX = (srcX.asInstanceOf[Float] / widthInTiles);
    val topRightY = (srcY.asInstanceOf[Float] / heightInTiles);
    
    val bottomLeftX = ((srcX.asInstanceOf[Float] + 1) / widthInTiles);
    val bottomLeftY = ((srcY.asInstanceOf[Float] + 1) / heightInTiles);
    
    tex.bind();
    
    glBegin(GL_QUADS)
  	    glTexCoord2f(topRightX, topRightY);
	    glVertex2f(0, 0);
	    glTexCoord2f(topRightX, bottomLeftY);
	    glVertex2f(0, tileSize);
	    glTexCoord2f(bottomLeftX, bottomLeftY);
	    glVertex2f(tileSize, tileSize);
	    glTexCoord2f(bottomLeftX, topRightY);
	    glVertex2f(tileSize, 0);
    glEnd()
 
  }
  
  /* bind the image at (x, y) on the spritesheet. */
  def bind(x:Int, y:Int) = {
    tex.bind();
  }
}