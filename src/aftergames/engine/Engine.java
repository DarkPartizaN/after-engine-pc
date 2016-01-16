package aftergames.engine;

import static aftergames.engine.EngineRuntime.console;

import aftergames.engine.render.Color;
import aftergames.engine.render.Renderer;
import aftergames.engine.ui.UIManager;
import aftergames.engine.utils.ResourceUtils;
import aftergames.engine.utils.StringUtils;
import aftergames.engine.utils.pools.IntPool;
import aftergames.engine.world.Time;
import aftergames.engine.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author KiQDominaN
 */
public final class Engine {

    //Engine states
    public static final int IN_CREATED = -1, IN_PAUSED = 0, IN_STOPING = 1, IN_GAME = 2, IN_DEAD = 3;
    private final IntPool states = new IntPool();
    //FPS
    public static boolean show_fps = true;
    private int fps, tmp_fps;
    private long fps_update;
    private long last_frametime;
    private Color fps_color;
    //World
    private World world;

    void create() {
        console.init();
        last_frametime = System.currentTimeMillis();

        setState(IN_CREATED);
    }

    void start(World world) {
        //Time
        Time.checkTime();
        //World
        this.world = world;

        setState(IN_GAME);
    }

    void frame() {
        if (states.get_last() == IN_DEAD) return;

        long currentFrameTime = System.currentTimeMillis();
        Renderer.clear();

        switch (states.get_last()) {
            case IN_GAME:
                processGame();
                break;
            case IN_PAUSED:
                pause();
                break;
            case IN_STOPING:
                stop();
                break;
        }

        //World mouse position
        if (world != null) {
            if (world.getCamera() != null) {
                Renderer.setCamera(world.getCamera());
                EngineRuntime.updateMouseWorldPos();
            }
        }

        Renderer.setOrtho(EngineRuntime.screen_width, EngineRuntime.screen_height);

        //UI
        processUI();

        //Handle engine input
        if (EngineRuntime.keyPressed(Controllable.TAKE_SCREENSHOT)) {
            EngineRuntime.resetKey(Controllable.TAKE_SCREENSHOT); //For prevent cascade screenshots

            String date = new SimpleDateFormat("HHmmssddMMyyyy").format(new Date());
            String name = "screenshot_".concat(date).concat(".png");

            ResourceUtils.take_screenshot(name, EngineRuntime.screen_width, EngineRuntime.screen_height);
            console.info_print("Screenshot ".concat(name).concat(" saved!"));
        }

        if (EngineRuntime.keyPressed(Controllable.CONSOLE)) toggleConsole();

        //Console
        console.update();
        console.onDraw();

        //FPS
        if (show_fps) {
            if (currentFrameTime - fps_update >= 1000) {
                fps_update = currentFrameTime;
                fps = tmp_fps;
                tmp_fps = 0;
            } else
                tmp_fps++;

            if (fps < 10)
                fps_color = Color.red;
            else if (fps < 20)
                fps_color = Color.orange;
            else if (fps < 30)
                fps_color = Color.yellow;
            else
                fps_color = Color.green;

            Renderer.drawString(StringUtils.concat("FPS:", String.valueOf(fps)), 2, 2, fps_color);
        }

        //Delta time
        EngineRuntime.frametime = (currentFrameTime - last_frametime) / 10f;
        last_frametime = currentFrameTime;
    }

    private void toggleConsole() {
        EngineRuntime.resetKey(Controllable.CONSOLE);

        if (!console.isActive())
            console.open();
        else
            console.close();
    }

    private void processGame() {
        //Time
        Time.checkTime();

        //World
        world.update();
        world.render();
    }

    private void processUI() {
        if (!console.isActive()) UIManager.update();
        UIManager.draw();
    }

    private void pause() {
    }

    private void stop() {
        states.clear();

        setState(IN_DEAD);
    }

    void setState(int state) {
        states.put(state);
    }

    void restoreState() {
        states.pop();
    }

    int getState() {
        return states.get_last();
    }

    World getWorld() {
        return world;
    }
}
