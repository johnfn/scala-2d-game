class Map(x:Int, y:Int, widthInTiles:Int, heightInTiles:Int, ss:Spritesheet) extends Entity(x, y, widthInTiles, heightInTiles) {
	import java.awt.image.BufferedImage;
	import javax.imageio.ImageIO;
	import java.io.File;

    traits = List("update", "draw", "map")

	val data:BufferedImage = ImageIO.read(new File("assets/map.png"));
    
	def toRGBTuple(num:Int):(Int, Int, Int) = {
	  val colorStr:String = num.toString();
	  return (java.awt.Color.decode(colorStr).getRed(), java.awt.Color.decode(colorStr).getRed(), java.awt.Color.decode(colorStr).getRed());
	} 
	
    var t:Array[Array[Tile]] = Array.tabulate(widthInTiles, heightInTiles)((x, y) => toRGBTuple(data.getRGB(x, y)) match {
      case (0, 0, 0) => new Tile(x * 20, y * 20, 20, 20, 0, ss);
      case (255, 255, 255) => new Tile(x * 20, y * 20, 20, 20, 1, ss);
      case _ => throw new Error("aderp!");
    })

    override def draw = {
      t.flatten.map(e => e.draw)
    }

    override def render() = {}
    override def update(m:Manager) = {}
    override def depth:Int = 5;

    override def collidesWith(e:Entity):Boolean = t.flatten.count(_.collidesWith(e)) > 0
    override def touchesPoint(p:(Int, Int)):Boolean = {
      t.flatten.count((t) => t.touchesPoint(p) && t.isWall) > 0
    }
}


  class Tile(x:Int, y:Int, width:Int, height:Int, t:Int, ss:Spritesheet) extends Entity(x, y, width, height) {
    override def update(m:Manager) = {}

    override def render = {
      t match {
        case 0 => ss.render(1, 0);
        case 1 => ss.render(0, 0);
        case _ => throw new Error("No value for Tile.");
      }
    }

    def isWall:Boolean = t == 0

    override def depth:Int = 5;

    override def collidesWith(other:Entity):Boolean = {
      isWall && touchesEntity(other)
    }
  }
  
