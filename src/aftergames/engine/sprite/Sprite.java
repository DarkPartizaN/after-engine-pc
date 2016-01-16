package aftergames.engine.sprite;

import aftergames.engine.render.Texture;
import aftergames.engine.utils.geom.Rect;

import java.util.HashMap;

/**
 *
 * @author KiQDominaN
 */
public class Sprite {

    public Texture texture;
    public Rect[] frames;
    public int frame_width, frame_height;

    private final HashMap<String, Animation> animations = new HashMap<>();
    private Animation current_animation;
    private boolean flip_x = false, flip_y = false;

    //Non-textured model for triggers
    public final static Sprite empty_sprite = new Sprite(0, 0);

    public Sprite(int width, int height) {
        frame_width = width;
        frame_height = height;
    }

    public Sprite(Texture texture, int width, int height) {
        this(width, height);

        this.texture = texture;

        int frames_num = (texture.width / frame_width) * (texture.height / frame_height);
        frames = new Rect[frames_num];

        int tile = 0;
        float u, v, u2, v2;
        for (float locY = 0; locY < texture.height; locY += frame_height) {
            for (float locX = 0; locX < texture.width; locX += frame_width) {
                u = locX / texture.width;
                v = locY / texture.height;
                u2 = u + (float) frame_width / texture.width;
                v2 = v + (float) frame_height / texture.height;

                frames[tile++] = texture.getRegion(u, v, u2, v2);
            }
        }

        current_animation = new Animation();
        current_animation.setSequence(new Animation.AnimationSequence());
    }

    public void addAnimation(Animation anim) {
        if (!animations.containsKey(anim.getID())) animations.put(anim.getID(), anim);
    }

    public Animation animByID(String id) {
        return animations.get(id);
    }

    public Animation getCurrentAnimation() {
        return current_animation;
    }

    public void setAnimation(Animation anim) {
        if (!current_animation.equals(anim)) {
            current_animation = anim;
            current_animation.resetSequence();
        }
    }

    public void setAnimation(Animation anim, float fps) {
        setAnimation(anim);
        current_animation.setFps(fps);
    }

    public void flip(boolean x, boolean y) {
        float tmp;

        if (flip_x != x) {
            for (Rect t : frames) {
                tmp = t.x;
                t.x = t.w;
                t.w = tmp;
            }

            flip_x = x;
        }

        if (flip_y != y) {
            for (Rect t : frames) {
                tmp = t.y;
                t.y = t.h;
                t.h = tmp;
            }

            flip_y = y;
        }
    }

}
