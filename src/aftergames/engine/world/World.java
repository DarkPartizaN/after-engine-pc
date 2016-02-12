package aftergames.engine.world;

import aftergames.engine.EngineRuntime;
import aftergames.engine.ai.Actor;
import aftergames.engine.utils.geom.Rect;
import aftergames.engine.render.Texture;
import aftergames.engine.utils.MathUtils;
import aftergames.engine.utils.ResourceUtils;
import aftergames.engine.utils.geom.Shape;

/**
 *
 * @author KiQDominaN
 */
public class World {

    private float x, y;
    private int world_width, world_height;
    private final LayerManager layer_manager;
    private Camera camera;
    private Actor actor;

    public World() {
        layer_manager = new LayerManager();
        camera = new Camera(new Rect(0, 0, EngineRuntime.screen_width, EngineRuntime.screen_height));
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

        Layer defaultLayer = new Layer();
        defaultLayer.setName("default");
        defaultLayer.addTerrain(t);

        layer_manager.add(defaultLayer);
    }

    public void createDefaultLocation(int width, int height, Tiles tiles) {
        System.out.println("Create default location");
        int tx = width, ty = height, tw = tiles.tile_width, th = tiles.tile_height;

        x = y = 0;
        world_width = tx * tw;
        world_height = ty * th;

        Layer defaultLayer = new Layer();
        defaultLayer.setName("default");
        defaultLayer.addTerrain(tiles);

        layer_manager.add(defaultLayer);
    }

    public void update() {
        //Camera offset
        camera.update();

        if (camera.checkworld) {
            if (camera.getWidth() < world_width) {
                if (camera.getWorldX() < 0)
                    camera.setWorldPosition(0, camera.getWorldY());
                if (camera.getWorldX() + camera.getWidth() > world_width)
                    camera.setWorldPosition(world_width - camera.getWidth(), camera.getWorldY());
            }
            if (camera.getHeight() < world_height) {
                if (camera.getWorldY() < 0)
                    camera.setWorldPosition(camera.getWorldX(), 0);
                if (camera.getWorldY() + camera.getHeight() > world_height)
                    camera.setWorldPosition(camera.getWorldX(), world_height - camera.getHeight());
            }
        }

        for (Layer layer : layer_manager.get_all()) layer.update();
        checkCollisions();

        //UNDONE: Update weather
    }

    public LayerManager getLayerManager() {
        return layer_manager;
    }

    public Entity getEntityByName(String name) {
        for (Layer layer : layer_manager.get_all()) {
            for (Entity obj : layer.getEntities())
                if (obj.name.equals(name))
                    return obj;
        }

        return null;
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

    //Physics bleat!
    private final EntityPool objects = new EntityPool(128);

    private void checkCollisions() {
        Entity obj1, obj2;

        objects.clear();
        for (Layer layer : layer_manager.get_all()) objects.add(layer.getEntities());

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

}