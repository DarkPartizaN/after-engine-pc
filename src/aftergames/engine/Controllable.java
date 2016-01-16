package aftergames.engine;

import aftergames.engine.utils.StringUtils;
import static org.lwjgl.glfw.GLFW.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * @author KiQDominaN
 */
public final class Controllable {

    private static final HashMap<Integer, String> key_names = new HashMap<>();
    private static final HashMap<String, Integer> key_codes = new HashMap<>();

    static {
        // Use reflection to find out key names
        Field[] fields = org.lwjgl.glfw.GLFW.class.getFields();
        try {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && (field.getName().startsWith("GLFW_KEY_") || field.getName().startsWith("GLFW_MOUSE_"))
                        && !field.getName().endsWith("WIN")) { /* Don't use deprecated names */

                    int key = field.getInt(null);
                    int trim;

                    if (field.getName().startsWith("GLFW_KEY_"))
                        trim = field.getName().indexOf("_", field.getName().indexOf("_") + 1);
                    else
                        trim = field.getName().indexOf("_", 0);

                    String name = field.getName().substring(trim + 1, field.getName().length());

                    if (name.startsWith("MOUSE_"))
                        name = StringUtils.replace(name, "BUTTON_", "");

                    key_names.put(key, name);
                    key_codes.put(name, key);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }

    //Developer stuff
    public static final int KEY_ANY = -999;
    public static final int TAKE_SCREENSHOT = GLFW_KEY_F5;
    //Input
    public static final int ESC = GLFW_KEY_ESCAPE;
    public static final int CONSOLE = GLFW_KEY_GRAVE_ACCENT;
    public static final int SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int ENTER = GLFW_KEY_ENTER;
    public static final int BACKSPACE = GLFW_KEY_BACKSPACE;
    public static final int TAB = GLFW_KEY_TAB;
    public static final int LINEUP = GLFW_KEY_UP;
    public static final int LINEDOWN = GLFW_KEY_DOWN;

    public static String getKeyName(int key) {
        return key_names.get(key);
    }

    public static int getKeyCode(String name) {
        if (key_codes.containsKey(name))
            return key_codes.get(name);
        else return 0;
    }
}
