import java.awt.Font;
import java.io.InputStream;
 
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
 
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.util.ResourceLoader;

class Text {
    import java.util.ArrayList;
  
    val awtFont:Font = new Font("Verdana", Font.BOLD, 24);
    val cf:org.newdawn.slick.font.effects.Effect = new ColorEffect(java.awt.Color.white);
	var f:UnicodeFont = new UnicodeFont(awtFont);
	
	f.getEffects().asInstanceOf[ArrayList[Effect]].add(cf);
	f.addAsciiGlyphs();
    f.loadGlyphs();
    
    def render() = {
      f.drawString(50, 50, "blabalblabalblabla");
    }
}