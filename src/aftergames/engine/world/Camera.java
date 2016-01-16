package aftergames.engine.world;

import aftergames.engine.utils.MathUtils;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.EngineRuntime;

/**
 *
 * @author KiQDominaN
 */
public class Camera extends Entity {

    //Class represents standart floating camera
    public float aspect = EngineRuntime.screen_aspect;
    public float fov = 90f;
    public float znear = 1f, zfar = 1000f;
    protected boolean freeze;

    public Camera(Rect viewport) {
        movetype = MOVETYPE_DYNAMIC;

        solid = false;
        visible = false;
        freeze = false;

        bbox = viewport;
        cbox = viewport;
        vbox = viewport;
    }

    public void freeze(boolean on) {
        freeze = on;
    }

    public void update() {
        if (freeze) return;

        if (hasTarget()) {
            float delta_x = (target.getWorldX() - getWidth() / 2) - getWorldX();
            position.x += (delta_x / 12f + target.current_speed.x * 1.2f + MathUtils.cos(target.angle) * 3.5) * EngineRuntime.frametime;

            float delta_y = (target.getWorldY() - getHeight() / 2) - getWorldY();
            position.y += (delta_y / 12f + target.current_speed.y * 1.2f + MathUtils.sin(target.angle) * 3.5) * EngineRuntime.frametime;
        }
    }

}
