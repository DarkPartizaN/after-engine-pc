package aftergames.engine.utils;

import aftergames.engine.render.Shader;
import aftergames.engine.render.Texture;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Scanner;
import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 *
 * @author KiQDominaN
 */
public final class ResourceUtils {

    //Resource root directories
    public static String dir_root = StringUtils.make_string(new File(".").getAbsoluteFile().getParentFile().getAbsolutePath().concat("/"));
    public static String dir_images = StringUtils.make_string("res/gfx/");
    public static String dir_maps = StringUtils.make_string("res/maps/");
    public static String dir_fonts = StringUtils.make_string("res/fonts/");
    public static String dir_shaders = StringUtils.make_string("res/shaders/");

    private static final HashMap<String, BufferedImage> cached_images = new HashMap<>(); //Image cache
    private static final HashMap<String, Texture> cached_textures = new HashMap<>(); //Textures cache
    private static final HashMap<String, Shader> cached_shaders = new HashMap<>(); //Shaders cache

    private static final BufferedImage null_image = create_null_image(); //Emo-texture for missed images :)
    private static final BufferedImage watermark_image = load_awt_image("launcher/watermark.png"); //For screenshots

    public static final ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    public static final ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

    private static BufferedImage create_null_image() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 32, 32);

        g.setColor(Color.PINK);
        for (int y = 0; y < 32; y += 16) {
            for (int x = 0; x < 32; x += 16) {
                g.fillRect(x, y, 8, 8);
                g.fillRect(x + 8, y + 8, 8, 8);
            }
        }

        return img;
    }

    public static boolean file_exists(String path) {
        return new File(path).exists();
    }

    public static BufferedImage load_awt_image(String imageID) {
        if (!cached_images.containsKey(imageID)) {
            try {
                cached_images.put(imageID, ImageIO.read(new File(dir_root.concat(dir_images).concat(imageID))));
            } catch (IOException ex) {
                return null_image;
            }
        }

        return cached_images.get(imageID);
    }

    public static Texture load_image(String imageID) {
        if (!cached_textures.containsKey(imageID)) {
            BufferedImage source_image = load_awt_image(imageID), target_image;
            WritableRaster raster;

            if (source_image.getColorModel().hasAlpha()) {
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, source_image.getWidth(), source_image.getHeight(), 4, null);
                target_image = new BufferedImage(glAlphaColorModel, raster, false, null);
            } else {
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, source_image.getWidth(), source_image.getHeight(), 3, null);
                target_image = new BufferedImage(glColorModel, raster, false, null);
            }

            // copy the source image into the produced image
            Graphics2D g = target_image.createGraphics();
            g.setColor(new Color(0f, 0f, 0f, 0f));
            g.fillRect(0, 0, source_image.getWidth(), source_image.getHeight());
            g.drawImage(source_image, 0, 0, null);

            byte[] data = ((DataBufferByte) target_image.getRaster().getDataBuffer()).getData();

            ByteBuffer buffer = BufferUtils.create_byte_buffer(data.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(data, 0, data.length);
            buffer.flip();

            Texture tmp = new Texture();
            tmp.width = target_image.getWidth();
            tmp.height = target_image.getHeight();

            tmp.raw = new int[tmp.width * tmp.height];
            target_image.getRGB(0, 0, tmp.width, tmp.height, tmp.raw, 0, tmp.width);

            glBindTexture(GL_TEXTURE_2D, tmp.id); //Bind texture ID

            //Setup wrap mode
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            //Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            //Send texel data to OpenGL
            if (source_image.getColorModel().hasAlpha())
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, target_image.getWidth(), target_image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            else
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, target_image.getWidth(), target_image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);

            cached_textures.put(imageID, tmp);
        }

        return cached_textures.get(imageID);
    }

    public static Font load_ttf(String font_name, int font_size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(dir_root.concat(dir_fonts).concat(font_name))).deriveFont((float) font_size);
        } catch (FontFormatException | IOException ex) {
            //Get default font >_<
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            return gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics().getFont().deriveFont((int) font_size);
        }
    }

    public static void take_screenshot(String name, int width, int height) {
        glReadBuffer(GL_FRONT); //Sloooooow

        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.create_byte_buffer(width * height * bpp);

        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        try {
            File file = new File(StringUtils.concat(dir_root, "screenshots/")); // The file to save to.
            if (!file.exists()) file.mkdir();

            file = new File(StringUtils.concat(dir_root, "screenshots/", name));
            if (!file.exists()) file.createNewFile();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int i = (x + (width * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;

                    image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }

            Graphics2D g = image.createGraphics();
            g.drawImage(watermark_image, 2, image.getHeight() - watermark_image.getHeight() - 2, null);
            g.dispose();

            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            //TODO: Engine error handling
        }
    }

    //Load any file from game folder
    public static String load_file(String file) {
        InputStream is;
        String tmp = StringUtils.make_string();
        int c;

        try {
            is = new FileInputStream(dir_root.concat(file));

            while ((c = is.read()) != -1) tmp += ((char) c);

            is.close();
        } catch (IOException e) {
            System.err.println("Could not find a file: " + dir_root.concat(file));
            return null;
        }

        return tmp;
    }

    //Load text file
    public static String load_lines(String file) {
        InputStream is;
        Scanner scan;
        String tmp = new String();

        try {
            is = new FileInputStream(dir_root.concat(file));
            scan = new Scanner(is);

            while (scan.hasNextLine()) tmp = tmp.concat(scan.nextLine()).concat("\n");

            is.close();
        } catch (IOException e) {
        }

        return tmp;
    }

    public static String[] load_lines_array(String file) {
        return StringUtils.splitString(load_file(file), "\n");
    }

    public static Shader load_shader(String name) {
        if (!cached_shaders.containsKey(name)) {
            try {
                Shader tmp = new Shader();
                String vp = load_file(dir_shaders.concat(name).concat("/").concat(name).concat(".vp"));
                if (vp == null)
                    vp = load_file(dir_shaders.concat("default/default.vp"));
                tmp.setVertexShader(vp);
                tmp.setFragmentShader(load_file(dir_shaders.concat(name).concat("/").concat(name).concat(".fp")));
                tmp.compile();

                cached_shaders.put(name, tmp);
            } catch (Throwable t) {
                System.err.println("Cannot load a shader: " + name);
                t.printStackTrace();
            }
        }

        return cached_shaders.get(name);
    }
}