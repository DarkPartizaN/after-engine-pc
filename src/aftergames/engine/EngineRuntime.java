package aftergames.engine;

import aftergames.engine.ui.console.Console;
import aftergames.engine.utils.geom.Matrix;
import aftergames.engine.utils.geom.Point;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.utils.geom.Shape;
import aftergames.engine.utils.geom.Vector2;
import aftergames.engine.world.Camera;
import java.util.HashMap;

/**
 *
 * @author KiQDominaN
 */
public final class EngineRuntime {

    public static long window_handle;
    public static int screen_width, screen_height;
    public static float screen_aspect;
    public static float frametime; //For correct work with different FPS

    private static Rect mouse_rect = new Rect(0, 0, 8, 8);
    private static Rect mouse_world_rect = new Rect(0, 0, 8, 8);
    private static Point mouse_pos = new Point(0, 0);
    private static Point old_mouse_pos = new Point(0, 0);
    private static Vector2 mouse_world = new Vector2(); //Mouse position in world coordinates

    private static final HashMap<Integer, Long> keychain = new HashMap<>(1); //Pressed buttons will be stored here
    private static int last_key;
    private static char current_char; //Current input

    public static final Console console = new Console();

    static {
        resetKeys();
        resetMouse();
        resetInput();
    }

    public static void setupScreen(int width, int height) {
        screen_width = width;
        screen_height = height;
        screen_aspect = (float) width / (float) height;
    }

    public static int lastKey() {
        if (keychain.isEmpty()) return -999;

        return last_key;
    }

    public static boolean keyPressed(int key) {
        if (key == Controllable.KEY_ANY)
            return !keychain.isEmpty();
        return (keychain.containsKey(key));
    }

    public static float keyTime(int key) {
        if (keyPressed(key))
            return (System.currentTimeMillis() - keychain.get(key)) / 1000f;
        else
            return 0;
    }

    public static void inputChar(char c) {
        current_char = c;
    }

    public static String getInput() {
        String c = (current_char <= 0) ? "" : String.valueOf(current_char); //WTF o_O? Char can be < 0
        resetInput();

        return c;
    }

    public static void pressKey(int key) {
        last_key = key;
        keychain.put(key, System.currentTimeMillis());
    }

    public static void resetKey(int key) {
        keychain.remove(key);
    }

    public static void resetKeys() {
        keychain.clear();
    }

    public static void resetMouse() {
        mouse_pos.x = screen_width / 2;
        mouse_pos.y = screen_height / 2;
    }

    public static void resetInput() {
        current_char = 0;
    }

    private static Matrix mouse_trans = new Matrix(), mouse_world_trans = new Matrix();

    public static void updateMousePos(float x, float y) {
        old_mouse_pos.x = mouse_pos.x;
        old_mouse_pos.y = mouse_pos.y;

        mouse_pos.x = x;
        mouse_pos.y = y;

        mouse_rect.reset();

        mouse_trans = Matrix.translate(mouse_pos.x, mouse_pos.y);
        mouse_trans.transform(mouse_rect);
    }
    
    public static boolean mouseMoved() {
        return ((old_mouse_pos.x != mouse_pos.x) || (old_mouse_pos.y != mouse_pos.y));
    }

    public static void updateMouseWorldPos() {
        Camera cam = EngineAPI.getWorld().getCamera();

        mouse_world.x = mouse_pos.x + cam.getWorldX();
        mouse_world.y = mouse_pos.y + cam.getWorldY();

        mouse_world_rect.reset();
        mouse_world_trans = Matrix.translate(mouse_world.x, mouse_world.y);
        mouse_world_trans.transform(mouse_world_rect);
    }

    public static boolean mouseInRect(Rect rect) {
        return Shape.intersects(mouse_rect, rect);
    }

    public static boolean worldMouseInRect(Rect rect) {
        return Shape.intersects(mouse_world_rect, rect);
    }

    public static int getMouseX() {
        return (int) mouse_pos.x;
    }

    public static int getMouseY() {
        return (int) mouse_pos.y;
    }

    public static float getWorldMouseX() {
        return mouse_world.x;
    }

    public static float getWorldMouseY() {
        return mouse_world.y;
    }
}
