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

class Text(var content: String, x: Int, y: Int) extends Entity(x, y, 0, 0) {
  import java.util.ArrayList;
  
  // This will cause all sorts of crazy problems.
  assert(!GL11.glIsEnabled(GL11.GL_TEXTURE_2D));

  val awtFont: Font = new Font("Verdana", Font.PLAIN, 24);
  val cf: org.newdawn.slick.font.effects.Effect = new ColorEffect(java.awt.Color.white);
  var f: UnicodeFont = new UnicodeFont(awtFont.deriveFont(0, 20.0f)); //new UnicodeFont(awtFont, 24, false, false);

  f.getEffects().asInstanceOf[ArrayList[Effect]].add(cf);
  f.addGlyphs(32, 127); //addAsciiGlyphs();
  f.loadGlyphs();
  
  def setText(t: String) = {
    this.content = t;
    
    println(this.content);
  }
  
  def update(m:Manager) = {}
  def depth:Int = 100;

  def render() = {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    f.drawString(x, y, content, Color.white);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
}