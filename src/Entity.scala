import org.lwjgl._
import opengl.{ Display, GL11, DisplayMode }
import GL11._
import input._
import math._
import Keyboard._
import scala.util.control.Breaks._

object Trait extends Enumeration {
  type Trait = Value
  val Update, Draw, Transparent, IgnoresCamera = Value;
}

abstract class Entity(var x: Int, var y: Int, var width: Int, var height: Int) {
  import Trait._;

  var vx: Int = 0
  var vy: Int = 0

  var traits: List[Trait] = List(Trait.Update, Trait.Draw)

  def touchesPoint(p: (Int, Int)): Boolean = {
    val (px, py) = p;

    x <= px && px <= x + width && y <= py && py <= y + height
  }

  def touchesEntity(other: Entity): Boolean = {
    (touchesPoint((other.x, other.y))
      || touchesPoint((other.x, other.y + other.height))
      || touchesPoint((other.x + other.width, other.y))
      || touchesPoint((other.x + other.width, other.y + other.height)))
  }

  def render;
  def update(m: Manager);
  def depth: Int;

  /* Overridden in stuff like Map to be more accurate */
  def collidesWith(other: Entity): Boolean = {
    assert(other.width == width)
    assert(other.height == height)

    touchesEntity(other)
  }

  def move(x: Int, y: Int) = {
    this.x = x;
    this.y = y;
  }

  def draw = {
    glPushMatrix()
    glTranslatef(x, y, 0)
    render
    glPopMatrix()
  }
}
