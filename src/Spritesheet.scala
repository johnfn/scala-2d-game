class Spritesheet(file:String, val tileSize:Int, texLoader:TextureLoader) {
  // The only reason we load in tex is so we can grab it's width/height. Slightly inefficient, but saves us trouble.
  val tex:Texture = texLoader.getTexture(file);
  
  val widthInTiles:Float  = (tex.getImageWidth()  / tileSize);
  val heightInTiles:Float = (tex.getImageHeight() / tileSize);
  
  // Renders the tile with top left coordinate srcX, srcY.
  def render(srcX:Int, srcY:Int) {
    import org.lwjgl.opengl.GL11._;
    
    val topRightX = tex.getWidth() * (srcX.asInstanceOf[Float] / widthInTiles);
    val topRightY = tex.getHeight() * (srcY.asInstanceOf[Float] / heightInTiles);
    
    val bottomLeftX = tex.getWidth() * ((srcX.asInstanceOf[Float] + 1) / widthInTiles);
    val bottomLeftY = tex.getHeight() * ((srcY.asInstanceOf[Float] + 1) / heightInTiles);
    
    tex.bind();
    
    glBegin(GL_QUADS)
  	    glTexCoord2f(topRightX, topRightY);
	    glVertex3f(0, 0, 0);
	    glTexCoord2f(topRightX, bottomLeftY);
	    glVertex3f(0, tileSize, 0);
	    glTexCoord2f(bottomLeftX, bottomLeftY);
	    glVertex3f(tileSize, tileSize, 0);
	    glTexCoord2f(bottomLeftX, topRightY);
	    glVertex3f(tileSize, 0, 0);
    glEnd()
 
  }
  
  /* bind the image at (x, y) on the spritesheet. */
  def bind(x:Int, y:Int) = {
    tex.bind();
  }
}