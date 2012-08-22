class Spritesheet(file:String, val tileSize:Int, texLoader:TextureLoader) {
  // The only reason we load in tex is so we can grab it's width/height. Slightly inefficient, but saves us trouble.
  val tex:Texture = texLoader.getTexture(file);
  
  val widthInTiles:Int  = (tex.getWidth() / tileSize).asInstanceOf[Int];
  val heightInTiles:Int = (tex.getHeight() / tileSize).asInstanceOf[Int];
  
  println(widthInTiles);
  println(heightInTiles);
  
  // Load every texture.
  val textures = Array.tabulate(widthInTiles, heightInTiles)((x, y) => texLoader.getTexture(file, x * tileSize, y * tileSize, tileSize, tileSize));
  
  /* bind the image at (x, y) on the spritesheet. */
  def bind(x:Int, y:Int) = {
    textures(x)(y).bind();
  }
}