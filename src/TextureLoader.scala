import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.io.File;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.utils.PNGDecoder;

class TextureLoader {
	import scala.collection.mutable.Map;
	

    /** 
     * Create a new texture loader based on the game panel
     *
     * @param gl The GL content in which the textures should be loaded
     */
    
    var glAlphaColorModel:ColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                        Array(8, 8, 8, 8),
                                        true,
                                        false,
                                        java.awt.Transparency.TRANSLUCENT,
                                        DataBuffer.TYPE_BYTE);
    
    var glColorModel:ColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                        Array(8, 8, 8, 8),
                                        false,
                                        false,
                                        java.awt.Transparency.OPAQUE,
                                        DataBuffer.TYPE_BYTE);
    
    /**
     * Create a new texture ID 
     *
     * @return A new texture ID
     */
    def createTextureID():Int = { 
       var tmp:IntBuffer = createIntBuffer(1)
       GL11.glGenTextures(tmp); 
       return tmp.get(0);
    } 
    
    /**
     * Load a texture
     *
     * @param resourceName The location of the resource to load
     * @param x
     * @param y
     * @param width
     * @param height If x, y, width and height are -1, then take the entire image. Otherwise, take a subimage. 
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    def getTexture(resourceName:String):Texture = {
        return loadTexture(resourceName);
    }
    
    /**
     * Load a texture into OpenGL from a image reference on
     * disk.
     *
     * @param resourceName The location of the resource to load
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    private def loadTexture(resourceName:String):Texture = { 
        import java.io.{FileInputStream, InputStream};
        
        var srcPixelFormat:Int = 0;
        
        // create the texture ID for this texture 

        var textureID:Int = createTextureID(); 
        var texture:Texture = new Texture(GL11.GL_TEXTURE_2D, textureID); 
        
        // bind this texture 

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); 
 
        var bufferedImage:BufferedImage = loadImage(resourceName); 
        
        texture.setWidth(bufferedImage.getWidth());
        texture.setHeight(bufferedImage.getHeight());
        
        if (bufferedImage.getColorModel().hasAlpha()) {
            srcPixelFormat = GL11.GL_RGBA;
        } else {
	        assert(false);
            srcPixelFormat = GL11.GL_RGB;
        }

        // convert that image into a byte buffer of texture data 

        var textureBuffer:ByteBuffer = convertImageData(bufferedImage, texture); 
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
 
        // produce a texture from the byte buffer

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 
                      0, 
                      GL11.GL_RGBA, 
                      get2Fold(bufferedImage.getWidth()), 
                      get2Fold(bufferedImage.getHeight()), 
                      0, 
                      srcPixelFormat, 
                      GL11.GL_UNSIGNED_BYTE, 
                      textureBuffer ); 
        
        return texture; 

      
		/*val in:InputStream = new FileInputStream(resourceName);
		val decoder:PNGDecoder = new PNGDecoder(in);
		
		val textureBuffer:ByteBuffer = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
		decoder.decode(textureBuffer, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
		textureBuffer.flip();
		
        // create the texture ID for this texture 

        var textureID:Int = createTextureID(); 
        var texture:Texture = new Texture(GL11.GL_TEXTURE_2D, textureID); 
        
        // bind this texture 

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); 
 
        texture.setWidth(decoder.getWidth());
        texture.setHeight(decoder.getHeight());
        
        // convert that image into a byte buffer of texture data 

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
 
        // produce a texture from the byte buffer

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
        
        return texture;
        */
    } 
    
    /**
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    private def get2Fold(fold:Int):Int = {
        var ret:Int = 2;
        while (ret < fold) {
            ret *= 2;
        }
        
        return ret;
    } 
    
    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    private def convertImageData(bufferedImage:BufferedImage ,texture:Texture):ByteBuffer = { 
        var imageBuffer:ByteBuffer = null; 
        var raster:WritableRaster = null;
        var texImage:BufferedImage = null;
        
        var texWidth:Int = 2;
        var texHeight:Int = 2;
        
        // find the closest power of 2 for the width and height

        // of the produced texture

        while (texWidth < bufferedImage.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < bufferedImage.getHeight()) {
            texHeight *= 2;
        }
        
        texture.setTextureHeight(texHeight);
        texture.setTextureWidth(texWidth);
        
        // create a raster that can be used by OpenGL as a source

        // for a texture

        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
            texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
            texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
        }

        // copy the source image into the produced image

        var g:Graphics = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,texWidth,texHeight);
        g.drawImage(bufferedImage,0,0,null);
        
        // build a byte buffer from the temporary image 

        // that be used by OpenGL to produce a texture.

        var data:Array[Byte] = (texImage.getRaster().getDataBuffer()).asInstanceOf[DataBufferByte].getData(); 

        imageBuffer = ByteBuffer.allocateDirect(data.length); 
        imageBuffer.order(ByteOrder.nativeOrder()); 
        imageBuffer.put(data, 0, data.length); 
        imageBuffer.flip();
        
        return imageBuffer; 
    } 
    
    /** 
     * Load a given resource as a buffered image
     * 
     * @param ref The location of the resource to load
     * @return The loaded buffered image
     * @throws IOException Indicates a failure to find a resource
     */
    private def loadImage(ref:String):BufferedImage = { 
        return ImageIO.read(new File(ref)); 
    }
    
    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size how many int to contain
     * @return created IntBuffer
     */
    protected def createIntBuffer(size:Int): IntBuffer = {
      var temp:ByteBuffer = ByteBuffer.allocateDirect(4 * size);
      temp.order(ByteOrder.nativeOrder());

      return temp.asIntBuffer();
    }    
}
