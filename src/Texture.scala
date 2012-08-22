import org.lwjgl.opengl.GL11;

/**
 * A texture to be bound within JOGL. This object is responsible for 
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 * 
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigged that the source image and hence the texture
 * mapping coordinates need to be adjusted to matchup drawing the
 * sprite against the texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
class Texture(target:Int, textureID:Int) {
    /** The height of the image */
    var height:Int;
    /** The width of the image */
    var width:Int;
    /** The width of the texture */
    var texWidth:Int;
    /** The height of the texture */
    var texHeight:Int;
    /** The ratio of the width of the image to the texture */
    var widthRatio:Float;
    /** The ratio of the height of the image to the texture */
    var heightRatio:Float;

    /**
     * Bind the specified GL context to a texture
     *
     * @param gl The GL context to bind to
     */
    def bind() {
      GL11.glBindTexture(target, textureID); 
    }

    /**
     * Set the height of the image
     *
     * @param height The height of the image
     */
    def setHeight(height:Int) = {
        this.height = height;
        updateRatio();
    }

    /**
     * Set the width of the image
     *
     * @param width The width of the image
     */
    def setWidth(width:Int) = {
        this.width = width;
        updateRatio();
    }
    
    /**
     * Get the height of the original image
     *
     * @return The height of the original image
     */
    def getImageHeight():Int = {
        return height;
    }
    
    /** 
     * Get the width of the original image
     *
     * @return The width of the original image
     */
    def getImageWidth():Int = {
        return width;
    }
    
    /**
     * Get the height of the physical texture
     *
     * @return The height of physical texture
     */
    def getHeight():Float = {
        return heightRatio;
    }
    
    /**
     * Get the width of the physical texture
     *
     * @return The width of physical texture
     */
    def getWidth():Float = {
        return widthRatio;
    }
    
    /**
     * Set the height of this texture 
     *
     * @param texHeight The height of the texture
     */
    def setTextureHeight(texHeight:Int) = {
        this.texHeight = texHeight;
        updateRatio();
    }
    
    /**
     * Set the width of this texture 
     *
     * @param texWidth The width of the texture
     */
    def setTextureWidth(texWidth:Int) = {
        this.texWidth = texWidth;
        updateRatio();
    }
    
    private def updateRatio() = {
        if (texHeight != 0) {
            heightRatio = (height.asInstanceOf[Float])/texHeight;
        }
        
        if (texWidth != 0) {
            widthRatio = (width.asInstanceOf[Float])/texWidth;
        }
    }
}
