package aftergames.engine.world;

import aftergames.engine.EngineAPI;

/**
 *
 * @author KiQDominaN
 */
public class Layer {

    private final EntityPool objects;
    private final EntityPool visible_objects;
    private final TilesPool terrains;
    private final LightPool visible_lights;
    protected String name;

    public Layer() {
        objects = new EntityPool();
        terrains = new TilesPool();
        visible_objects = new EntityPool();
        visible_lights = new LightPool();

        name = "";
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

    public void removeTerrain(Tiles obj) {
        terrains.remove(obj); //Delete object
    }

    public Entity[] getEntities() {
        return objects.get_all();
    }

    public Tiles[] getTiles() {
        return terrains.get_all();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Entity[] getVisible() {
        Camera camera = EngineAPI.getWorld().getCamera();

        visible_objects.clear();

        for (Entity e : getEntities()) {
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

        // UNDONE: visibility field

        return visible_objects.get_all();
    }

    public Light[] getLights() {
        visible_lights.clear();

        for (Entity e : getVisible())
            if (e instanceof Light)
                visible_lights.add((Light) e);

        return visible_lights.get_all();
    }

    public void update() {
        //Update objects
        for (Entity obj : getEntities()) {
            obj.update();

            //Check out of world
            if (obj.checkworld) {
                if (obj.getBBOX().getMinX() < 0)
                    obj.setWorldPosition(obj.getBBOX().getSize().x / 2, obj.getWorldY());
                if (obj.getBBOX().getMinY() < 0)
                    obj.setWorldPosition(obj.getWorldX(), obj.getBBOX().getSize().y / 2);
                if (obj.getBBOX().getMinX() + obj.getBBOX().getSize().x >= EngineAPI.getWorld().getWidth())
                    obj.setWorldPosition(EngineAPI.getWorld().getWidth() - obj.getBBOX().getSize().x / 2, obj.getWorldY());
                if (obj.getBBOX().getMinY() + obj.getBBOX().getSize().y >= EngineAPI.getWorld().getHeight())
                    obj.setWorldPosition(obj.getWorldX(), EngineAPI.getWorld().getHeight() - obj.getBBOX().getSize().y / 2);
            }
        }
    }

}