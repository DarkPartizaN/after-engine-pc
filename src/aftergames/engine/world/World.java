package aftergames.engine.world;

import aftergames.engine.EngineRuntime;
import aftergames.engine.ai.Actor;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.utils.geom.Shape;
import aftergames.engine.render.BlendingMode;
import aftergames.engine.render.Color;
import aftergames.engine.render.Framebuffer;
import aftergames.engine.render.Renderer;
import aftergames.engine.render.Shader;
import aftergames.engine.render.Texture;
import aftergames.engine.utils.MathUtils;
import aftergames.engine.utils.ResourceUtils;

/**
 *
 * @author KiQDominaN
 */
public class World {

    private float x, y;
    private int world_width, world_height;
    private EntityPool objects;
    private EntityPool visible_objects;
    private LightPool lights;
    private TilesPool terrains;
    private Camera camera;
    private Actor actor;

    public World() {
        int tx = 50, ty = 50, tw = 64, th = 64;

        x = y = 0;
        world_width = tx * tw;
        world_height = ty * th;

        objects = new EntityPool();
        visible_objects = new EntityPool();
        lights = new LightPool();
        terrains = new TilesPool();

        camera = new Camera(new Rect(0, 0, EngineRuntime.screen_width, EngineRuntime.screen_height));

        initRender();
    }

    public void createDefaultLocation(int width, int height, int tile_width, int tile_height, int tile_num) {
        System.out.println("Create default location");
        int tx = width, ty = height, tw = tile_width, th = tile_height, tr = tile_num;

        x = y = 0;
        world_width = tx * tw;
        world_height = ty * th;

        int[] tmp = new int[tx * ty];
        for (int i = 0; i < tx * ty; i++) tmp[i] = MathUtils.random_int(1, tr);

        Texture tex = ResourceUtils.load_image("tiles/tiles.png");

        Tiles t = new Tiles(tex, tx, ty, tw, th);
        t.setMap(tmp);

        addTerrain(t);
    }

    public void createDefaultLocation(int width, int height, Tiles tiles) {
        System.out.println("Create default location");
        int tx = width, ty = height, tw = tiles.tile_width, th = tiles.tile_height;

        x = y = 0;
        world_width = tx * tw;
        world_height = ty * th;

        addTerrain(tiles);
    }

    public void update() {
        //Camera offset
        camera.update();

        if (camera.checkworld) {
            if (camera.getWidth() < world_width) {
                if (camera.getWorldX() < 0) camera.setWorldPosition(0, camera.getWorldY());
                if (camera.getWorldX() + camera.getWidth() > world_width) camera.setWorldPosition(world_width - camera.getWidth(), camera.getWorldY());
            }
            if (camera.getHeight() < world_height) {
                if (camera.getWorldY() < 0) camera.setWorldPosition(camera.getWorldX(), 0);
                if (camera.getWorldY() + camera.getHeight() > world_height) camera.setWorldPosition(camera.getWorldX(), world_height - camera.getHeight());
            }
        }

        //Update objects
        for (Entity obj : objects.get_all()) {
            obj.update();

            //Check out of world
            if (obj.checkworld) {
                if (obj.getBBOX().getMinX() < 0) obj.setWorldPosition(obj.getBBOX().getSize().x / 2, obj.getWorldY());
                if (obj.getBBOX().getMinY() < 0) obj.setWorldPosition(obj.getWorldX(), obj.getBBOX().getSize().y / 2);
                if (obj.getBBOX().getMinX() + obj.getBBOX().getSize().x >= world_width) obj.setWorldPosition(world_width - obj.getBBOX().getSize().x / 2, obj.getWorldY());
                if (obj.getBBOX().getMinY() + obj.getBBOX().getSize().y >= world_height) obj.setWorldPosition(obj.getWorldX(), world_height - obj.getBBOX().getSize().y / 2);
            }
        }

        sortObjects();
        checkCollisions();

        //Update weather
    }

    public void addTerrain(Tiles t) {
        terrains.add(t);
    }

    public void addObject(Entity obj) {
        objects.add(obj);

        for (Entity p : obj.getChilds()) addObject(p);
    }

    public void removeObject(Entity obj) {
        for (Entity c : obj.collides.get_all())
            c.collides.remove(obj); //Delete physic body

        objects.remove(obj); //Delete object
    }

    public Entity[] getVisible() {
        return visible_objects.get_all();
    }

    public Entity[] getObjects() {
        return objects.get_all();
    }

    public Entity getObjectByName(String name) {
        for (Entity obj : objects.get_all())
            if (obj.name.equals(name))
                return obj;

        return null;
    }

    public Light[] getLights() {
        lights.clear();

        for (Entity e : getVisible())
            if (e instanceof Light)
                lights.add((Light) e);

        return lights.get_all();
    }

    public Tiles[] getTiles() {
        return terrains.get_all();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return world_width;
    }

    public int getHeight() {
        return world_height;
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    //Some old :(
    //but nice code :)
    public void sortObjects() {
        visible_objects.clear();

        for (Entity e : objects.get_all()) {
            if (e.killme) removeObject(e); //Remove dead objects
            if (e.visible) {
                if (e.getBBOX().getMinX() + e.getBBOX().getSize().x > camera.getWorldX()
                        && e.getBBOX().getMinX() < camera.getWorldX() + camera.getWidth()
                        && e.getBBOX().getMinY() + e.getBBOX().getSize().y > camera.getWorldY()
                        && e.getBBOX().getMinY() < camera.getWorldY() + camera.getHeight()) {

                    visible_objects.add(e);
                }
            }
        }

        int num_ents = visible_objects.size();

        Entity obj1, obj2;
        for (int i = 0; i < num_ents; i++) {
            for (int j = num_ents - 2; j >= i; j--) {
                obj1 = visible_objects.get(j);
                obj2 = visible_objects.get(j + 1);

                if (obj1.layer < obj2.layer)
                    visible_objects.swap(j, j + 1);
            }
        }

// UNDONE: visibility field
//        for (Entity e : objects.get_all()) {
//            for (Entity e2 : visible_objects) {
//                if (Shape.intersects(e.getVBOX(), e2.getBBOX()))
//                    e.visible_objects.add(e2);
//                else
//                    e.visible_objects.remove(e2);
//            }
//        }
    }

    //Physics bleat!
    public void checkCollisions() {
        Entity obj1, obj2;

        for (int i = 0; i < objects.size(); i++) {
            obj1 = objects.get(i);

            for (int j = i + 1; j < objects.size(); j++) {
                obj2 = objects.get(j);
                if (Shape.intersects(obj1.getBBOX(), obj2.getBBOX())) {
                    obj1.addTouch(obj2);
                    obj2.addTouch(obj1);

                    obj1.touch(obj2);
                    obj2.touch(obj1);

                    if (Shape.intersects(obj1.getCBOX(), obj2.getCBOX())) {
                        obj1.addCollision(obj2);
                        obj2.addCollision(obj1);
                    } else {
                        obj1.removeCollision(obj2);
                        obj2.removeCollision(obj1);
                    }
                } else {
                    obj1.removeTouch(obj2);
                    obj2.removeTouch(obj1);
                }
            }

            obj1.checkCollisions();
        }
    }

    //RENDER SYSTEM
    private Framebuffer fb_world, fb_light, fb_tmp;
    private Shader default_shader, blur_shader, scene_shader;

    private void initRender() {
        fb_world = new Framebuffer(EngineRuntime.screen_width, EngineRuntime.screen_height);
        fb_light = new Framebuffer(EngineRuntime.screen_width, EngineRuntime.screen_height);
        fb_tmp = new Framebuffer(EngineRuntime.screen_width, EngineRuntime.screen_height);

        default_shader = ResourceUtils.load_shader("default");
        blur_shader = ResourceUtils.load_shader("gauss_blur");
        scene_shader = ResourceUtils.load_shader("scene");
    }

    private Color ambient = Color.dark_gray;
    public static boolean clip_world = true;

    public void render() {
        //We need clear framebuffers to correct layers drawing
        Renderer.clear();
        Renderer.fillRect(0, 0, EngineRuntime.screen_width, EngineRuntime.screen_height, Color.black);

        //Clip render frame if world smaller, then screen
        //DANGER: operations order is magic, do not change that!!!
        if (clip_world) {
            int clip_x = (camera.getWorldX() < 0) ? (int) -camera.getWorldX() : 0;
            int clip_y = (camera.getWorldY() < 0) ? (int) -camera.getWorldY() : 0;
            int clip_w = EngineRuntime.screen_width;
            int clip_h = EngineRuntime.screen_height;

            if (camera.getWorldX() + camera.getWidth() > world_width) clip_w = (int)(world_width - camera.getWorldX());
            if (camera.getWidth() > world_width) clip_w = world_width;
            if (camera.getWorldY() + camera.getHeight() > world_height) clip_h = (int)(world_height - camera.getWorldY());
            if (camera.getHeight() > world_height) clip_h = world_height;

            Renderer.clipRect(clip_x, clip_y, clip_w, clip_h);
        }

        //Render frame
        Renderer.setCamera(getCamera());

        //Render light
        Renderer.setTarget(fb_light);
        fb_light.clear(ambient);

        if (Renderer.r_lightning) {
            for (Light l : getLights()) {
                Renderer.setBlending(BlendingMode.BLEND_ADDITIVE);
                Renderer.setShader(null);

                l.render();

                if (!l.touched.is_empty()) {
                    Renderer.setBlending(BlendingMode.BLEND_ALPHA);
                    Renderer.setShader(default_shader);

                    default_shader.setUniform("u_color", ambient.r, ambient.g, ambient.b, 1f);
                    default_shader.setUniform("u_coloronly", true);

                    for (Entity e : l.touched.get_all())
                        if (e.layer < l.layer) e.render();
                }
            }
        }

        //Render world & shadows
        Renderer.setTarget(fb_world);
        fb_world.clear(Color.black); //Black color is important!

        Renderer.setBlending(BlendingMode.BLEND_ALPHA);
        Renderer.setShader(default_shader);

        default_shader.setUniform("u_coloronly", false);

        if (Renderer.r_diffuse) for (Tiles t : getTiles()) t.render();

        for (Entity e : getVisible()) {
            if (!(e instanceof Light)) {
                if (Renderer.r_shadows) e.renderShadow();
                if (Renderer.r_diffuse) e.render();
            }
        }

        //Merge it all together
        Renderer.setOrtho(EngineRuntime.screen_width, EngineRuntime.screen_height);

        //Soft-edged lightmap ( we need ortho mode for screenspace blur :) )
        Renderer.setBlending(null);
        Renderer.setShader(blur_shader);

        blur_shader.setUniform("radius", 0.5f);
        blur_shader.setUniform("resolution", (float) EngineRuntime.screen_width);
        blur_shader.setUniform("dir", 0);

        Renderer.setTarget(fb_tmp);
        fb_tmp.clear();

        fb_light.draw();
        blur_shader.setUniform("resolution", (float) EngineRuntime.screen_height);
        blur_shader.setUniform("dir", 1);

        Renderer.setTarget(fb_light);
        fb_tmp.draw();

        Renderer.setTarget(null);

        //Draw framebuffers
        Renderer.setMultiTexture(0, fb_world.getTexture());
        Renderer.setMultiTexture(1, fb_light.getTexture());

        Renderer.setShader(scene_shader);

        scene_shader.setUniform("u_scene", 0);
        scene_shader.setUniform("u_lightmap", 1);
        scene_shader.setUniform("u_grayscale", Renderer.r_grayscale);

        Renderer.begin(Renderer.R_QUAD);
        {
            Renderer.setTexCoords(0, 1);
            Renderer.setVertexCoords(0, 0);
            Renderer.setTexCoords(1, 1);
            Renderer.setVertexCoords(EngineRuntime.screen_width, 0);
            Renderer.setTexCoords(1, 0);
            Renderer.setVertexCoords(EngineRuntime.screen_width, EngineRuntime.screen_height);
            Renderer.setTexCoords(0, 0);
            Renderer.setVertexCoords(0, EngineRuntime.screen_height);
        }
        Renderer.end();

        Renderer.setMultiTexture(1, null);
        Renderer.setMultiTexture(0, null);

        Renderer.setShader(null);

        //For testing
        if (Renderer.r_debug) {
            Renderer.setCamera(camera);

            for (Entity e : getVisible()) {
                Renderer.drawShape(e.getBBOX(), Color.green);
                Renderer.drawShape(e.getCBOX(), Color.yellow);
                Renderer.drawShape(e.shadowbox, Color.blue);
                Renderer.drawPoint(e.position, 3, Color.red);
            }

            float mx = EngineRuntime.getWorldMouseX();
            float my = EngineRuntime.getWorldMouseY();

            Renderer.fillRect((int) mx - 4, (int) my - 4, 8, 8, Color.blue);
            Renderer.fillRect((int) (camera.getWorldX() + camera.getWidth() / 2) - 4, (int) (camera.getWorldY() + camera.getHeight() / 2) - 4, 8, 8, Color.violet);
        }
    }
}
