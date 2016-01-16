package aftergames.engine.ui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static aftergames.engine.utils.ResourceUtils.glAlphaColorModel;
import static aftergames.engine.utils.ResourceUtils.load_ttf;

import aftergames.engine.render.Color;
import aftergames.engine.render.Texture;
import aftergames.engine.utils.BufferUtils;
import aftergames.engine.utils.StringUtils;

import java.awt.*;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

/**
 *
 * @author KiQDominaN
 */
public class UIFont {

    private static UIFont default_font;

    public static UIFont createGuiFont(String name, int font_size) {
        Font font = load_ttf(StringUtils.concat(name, ".ttf"), font_size);
        UIFont tmp = new UIFont();

        tmp.font_texture = new Texture();

        Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        graphics.setFont(font);

        tmp.fontMetrics = graphics.getFontMetrics();

        WritableRaster raster;
        BufferedImage bufferedImage;

        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, (int) tmp.getFontImageWidth(), (int) tmp.getFontImageHeight(), 4, null);
        bufferedImage = new BufferedImage(glAlphaColorModel, raster, false, null);

        //Draw the characters on our image
        Graphics2D imageGraphics = bufferedImage.createGraphics();
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        imageGraphics.setFont(font);
        imageGraphics.setColor(Color.white.getAWTColor());

        // draw every CHAR by line...
        for (int i : key_table.keySet())
            imageGraphics.drawString(key_table.get(i), 0, (int) (tmp.fontMetrics.getMaxAscent() + (tmp.getHeight() * i)));

        //Generate texture data
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        ByteBuffer imageData = BufferUtils.create_byte_buffer(data.length);
        imageData.order(ByteOrder.nativeOrder());
        imageData.put(data, 0, data.length);
        imageData.flip();

        glBindTexture(GL_TEXTURE_2D, tmp.font_texture.id);

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, (int) tmp.getFontImageWidth(), (int) tmp.getFontImageHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);

        return tmp;
    }

    protected Texture font_texture;
    protected FontMetrics fontMetrics;

    private static final HashMap<Integer, String> key_table = new HashMap<Integer, String>() {
        {
            put(0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            put(1, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase());
            put(2, "¿¡¬√ƒ≈®∆«»… ÀÃÕŒœ–—“”‘’÷◊ÿŸ⁄€‹›ﬁﬂ");
            put(3, "¿¡¬√ƒ≈®∆«»… ÀÃÕŒœ–—“”‘’÷◊ÿŸ⁄€‹›ﬁﬂ".toLowerCase());
            put(4, "0123456789");
            put(5, " $+-*/=%\"'#@&_(),.;:?!\\|<>[]ß`^~");
        }
    };

    public static UIFont getDefault() {
        if (default_font == null)
            default_font = UIFont.createGuiFont("Kasper_R", 32);

        return default_font;
    }

    public float getCharX(char c) {
        String originStr = String.valueOf(c);

        for (String s : key_table.values()) {
            if (s.contains(originStr))
                originStr = s;
        }

        return (float) fontMetrics.getStringBounds(originStr.substring(0, originStr.indexOf(c)), null).getWidth();
    }

    public float getCharY(char c) {
        float line = 0;

        for (int i : key_table.keySet()) {
            if (key_table.get(i).contains(String.valueOf(c)))
                line = i;
        }

        return getHeight() * line;
    }

    public int getHeight() {
        return (int) (fontMetrics.getHeight());
    }

    public float charWidth(int c) {
        return fontMetrics.charWidth(c);
    }

    public int stringWidth(String s) {
        return fontMetrics.stringWidth(s);
    }

    public float getFontImageWidth() {
        float a, w = 0;
        for (String s : key_table.values()) {
            a = (float) fontMetrics.getStringBounds(s, null).getWidth();
            if (a > w)
                w = a;
        }

        return w;
    }

    public float getFontImageHeight() {
        return key_table.keySet().size() * (getHeight());
    }

    public Texture getFontTexture() {
        return font_texture;
    }
}
