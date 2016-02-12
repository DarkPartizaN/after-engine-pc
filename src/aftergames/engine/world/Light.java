package aftergames.engine.world;

import static org.lwjgl.opengl.GL11.*;

import aftergames.engine.EngineRuntime;
import aftergames.engine.render.Color;
import aftergames.engine.render.RenderAPI;
import aftergames.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public class Light extends Entity {

    public static int AMBIENT = 0, POINT = 1, CONE = 2;
    public int type = POINT;

    protected float distance, radius;
    public boolean cast_shadows = false;

    public Color color = Color.white;

    public Light(int type) {
        this.type = type;
    }

    public void init() {
        checkworld = false;
        solid = false;
        nomodel = true;
        visible = true;
    }

    public void setDistance(float distance) {
        this.distance = distance;

        setBounds(distance * 2.5f, distance * 2.5f);
        setCollisionCircle(0, 0, (int) distance, MathUtils.bound(10, (int) (distance / 10f), 48));
        setViewCircle(0, 0, (int) distance, 20);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDistance() {
        return distance;
    }

    public float getRadius() {
        return radius;
    }

    public void addTouch(Entity e) {
        if (e == this || (e instanceof Light)) return; //Prevent selfcollision :)

        touched.add(e);
    }

    public void update() {
        if (hasTarget()) {
            rotateObject(target.angle - radius / 2);
            setWorldPosition(target.getWorldX(), target.getWorldY());
        }
    }

    public void render() {
        if (!visible) return;

        if (type == Light.AMBIENT) {
            glBegin(GL_QUADS);
            {
                glColor4f(color.r, color.g, color.b, color.a);

                glVertex2d(getWorldX(), getWorldY());
                glVertex2d(getWorldX() + EngineRuntime.screen_width, getWorldY());
                glVertex2d(getWorldX() + EngineRuntime.screen_width, getWorldY() + EngineRuntime.screen_height);
                glVertex2d(getWorldX(), getWorldY() + EngineRuntime.screen_height);
            }
            glEnd();
        }

        if (type == Light.POINT) {
            float precize = cbox.getPoints().length;
            float delta = 360f / precize;

            RenderAPI.begin(RenderAPI.R_TRI_FAN);
            {
                RenderAPI.setColor(color);
                RenderAPI.setVertexCoords(position);

                RenderAPI.setColor(Color.black);

                for (float a = 0; a <= 360; a += delta)
                    RenderAPI.setVertexCoords(getWorldX() + distance * MathUtils.cos(a), getWorldY() + distance * MathUtils.sin(a));

                RenderAPI.setVertexCoords(getWorldX() + distance, getWorldY());
            }
            RenderAPI.end();
        }

        if (type == Light.CONE) {
            float precize = 4;
            float delta = radius / precize;

            glBegin(GL_TRIANGLE_FAN);
            {
                RenderAPI.setColor(color);

                glVertex2d(getWorldX() + 5, getWorldY());

                RenderAPI.setColor(Color.black);

                for (float a = angle; a <= angle + radius; a += delta)
                    glVertex2d(getWorldX() + distance * MathUtils.cos(a), getWorldY() + distance * MathUtils.sin(a));
            }
            glEnd();
        }

        RenderAPI.setColor(Color.white);
    }

    public void setColor(Color c) {
        this.color = c;
    }
}
