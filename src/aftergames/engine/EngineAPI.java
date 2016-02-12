package aftergames.engine;

import static org.lwjgl.glfw.GLFW.*;

import aftergames.engine.ai.Actor;
import aftergames.engine.render.Renderer;
import aftergames.engine.world.World;

/**
 *
 * @author KiQDominaN
 */
public class EngineAPI {
    //======================================
    //This class represents engine functions
    //with safe access from game library
    //======================================

    private static Renderer render;
    private static Engine engine;

    //================================
    //Engine
    //================================
    public static void createEngine() {
        engine = new Engine();
        engine.create();
    }

    public static void startEngine(World world) {
        if (!engineCreated()) return;
        engine.start(world);
    }

    public static void frame() {
        if (!engineCreated()) return;
        engine.frame();
    }

    public static boolean engineCreated() {
        return !(engine == null);
    }

    public static int getState() {
        if (!engineCreated()) return Engine.IN_DEAD;
        return engine.getState();
    }

    public static void setState(int state) {
        if (!engineCreated()) return;
        engine.setState(state);
    }

    public static void restoreState() {
        if (!engineCreated()) return;
        engine.restoreState();
    }

    public static void stopEngine() {
        if (!engineCreated()) return;
        setState(Engine.IN_STOPING);
    }

    //================================
    //World
    //================================
    public static World getWorld() {
        if (!engineCreated()) return null;
        return engine.getWorld();
    }

    public static Actor getActor() {
        if (!engineCreated()) return null;
        return engine.getWorld().getActor();
    }

    //================================
    //UI
    //================================
    public static void showSystemCursor(boolean show) {
        if (show)
            glfwSetInputMode(EngineRuntime.window_handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        else
            glfwSetInputMode(EngineRuntime.window_handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    //================================
    //Renderer
    //================================
    public static void setRenderer(Renderer render) {
        EngineAPI.render = render;
    }

    public static Renderer getRenderer() {
        return render;
    }
}