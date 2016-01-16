package aftergames.engine;

import java.nio.ByteBuffer;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.*;


/**
 *
 * @author KiQDominaN
 */
public class Screen {

    //LWGL stuff here
    //The window handle
    private long window_handle;
    public String window_label;
    //We need to strongly reference callback instances.
    private GLFWErrorCallback error_callback;
    private GLFWKeyCallback key_callback;
    private GLFWCharCallback input_callback;
    private GLFWMouseButtonCallback mousebuttons_callback;
    private GLFWCursorPosCallback cursor_callback;
    private GLFWScrollCallback scroll_callback;

    public Screen(String label, int width, int height, boolean fullscreen) {
        create(label, width, height, fullscreen);
    }

    private void create(String label, int width, int height, boolean fullscreen) {
        if (height == 0 || height < 640) height = 640; //FIXME!!! for prevent FBO glitches

        window_label = label;

        System.out.println("LWJGL" + Sys.getVersion() + " starting!");
        System.out.println();

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(error_callback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will be resizable

        // Create the window
        window_handle = glfwCreateWindow(width, height, label, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

        if (window_handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetInputMode(window_handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window_handle, key_callback = new KeyCallback());
        glfwSetCharCallback(window_handle, input_callback = new CharCallback());
        glfwSetMouseButtonCallback(window_handle, mousebuttons_callback = new MouseButtonsCallback());
        glfwSetCursorPosCallback(window_handle, cursor_callback = new CursorCallback());

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        if (!fullscreen)
            glfwSetWindowPos(window_handle, (GLFWvidmode.width(vidmode) - width) / 2, (GLFWvidmode.height(vidmode) - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window_handle);
        // Disable v-sync
        glfwSwapInterval(0);

        // Make the window visible
        glfwShowWindow(window_handle);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        //Engine stuff here
        EngineRuntime.setupScreen(width, height);
        EngineRuntime.window_handle = window_handle;
    }

    public void init() {
    }

    public void start() {
        loop(); //POEHALI!!!

        // Release window and window callbacks
        glfwDestroyWindow(window_handle);

        key_callback.release();
        input_callback.release();
        mousebuttons_callback.release();
        cursor_callback.release();

        // Terminate GLFW and release the GLFWerrorfun
        glfwTerminate();

        error_callback.release();

        System.gc();
        System.exit(0);
    }

    private void loop() {
        while (glfwWindowShouldClose(window_handle) == GL_FALSE && EngineAPI.getState() != Engine.IN_DEAD) {
            EngineAPI.frame();

            glfwSwapBuffers(window_handle); // swap the color buffers

            // Poll for window events. The key callback above will only be invoked during this call.
            glfwPollEvents();
        }
    }

    private final class KeyCallback extends GLFWKeyCallback {

        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS) EngineRuntime.pressKey(key);
            if (action == GLFW_RELEASE) EngineRuntime.resetKey(key);
        }
    }

    private final class CharCallback extends GLFWCharCallback {

        public void invoke(long window, int codepoint) {
            EngineRuntime.inputChar((char) codepoint);
        }
    }

    private final class MouseButtonsCallback extends GLFWMouseButtonCallback {

        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) EngineRuntime.pressKey(button);
            if (action == GLFW_RELEASE) EngineRuntime.resetKey(button);
        }
    }

    private final class CursorCallback extends GLFWCursorPosCallback {

        public void invoke(long window, double xpos, double ypos) {
            EngineRuntime.updateMousePos((float) xpos, (float) ypos);
        }
    }

    private final class ScrollCallback extends GLFWScrollCallback {

        public void invoke(long window, double scroll_x, double scroll_y) {
        }
    }
}