package aftergames.engine.world;

import aftergames.engine.EngineRuntime;
import aftergames.engine.utils.geom.*;
import aftergames.engine.sprite.Sprite;
import aftergames.engine.render.Renderer;
import aftergames.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public abstract class Entity {

    //Movetype
    public static final int MOVETYPE_STATIC = 0, MOVETYPE_DYNAMIC = 1;
    public int movetype;
    //Types of damage
    public static final byte DMG_GENERIC = -1, DMG_BULLET = 0, DMG_BURN = 1, DMG_ACID = 2, DMG_FROST = 3, DMG_MELEE = 4;
    //Remove flag
    public boolean killme;
    //Visible flag
    protected boolean visible;
    //No model?
    public boolean nomodel = true;
    //Model
    protected Sprite sprite;
    //World parameters
    public int layer; //Draw order
    public float angle;
    public float speed;
    protected Vector2 position = new Vector2(0, 0); //World position
    protected Vector2 old_position = new Vector2(0, 0);
    public Vector2 current_speed = new Vector2(0, 0);
    //Size & collision
    protected Matrix trans = new Matrix();
    protected Rect bbox;
    protected Shape cbox;
    protected Shape vbox;
    //Needs collision?
    public boolean solid;
    public boolean trigger = false;
    public boolean checkworld = true; //Check out of world bounds
    protected EntityPool collides = new EntityPool();
    protected EntityPool touched = new EntityPool();
    //Objects in view field
    protected EntityPool visible_objects = new EntityPool();
    //Name for triggers
    public String name = new String();
    //Parent system
    protected EntityPool childs = new EntityPool();
    protected Entity parent, target;
    //Render system
    public LightPool lights = new LightPool();
    public boolean rendered = false;

    public Entity() {
        bbox = new Rect(0, 0, 0, 0);
        cbox = new Rect(0, 0, 0, 0);
        vbox = new Rect(0, 0, 0, 0);

        checkCollisions();
    }

    public void init() {
    }

    public void update() {
    }

    public final Sprite getSprite() {
        return sprite;
    }

    public void render() {
        if (nomodel || sprite == Sprite.empty_sprite || rendered) return;

        Rect region = sprite.frames[sprite.getCurrentAnimation().getCurrentFrame()];

        Renderer.setTexture(sprite.texture);

        Renderer.begin(Renderer.R_QUAD);
        {
            Renderer.setTexCoords(region.x, region.y);
            Renderer.setVertexCoords(bbox.getPoints()[0].x, bbox.getPoints()[0].y);
            Renderer.setTexCoords(region.w, region.y);
            Renderer.setVertexCoords(bbox.getPoints()[1].x, bbox.getPoints()[1].y);
            Renderer.setTexCoords(region.w, region.h);
            Renderer.setVertexCoords(bbox.getPoints()[2].x, bbox.getPoints()[2].y);
            Renderer.setTexCoords(region.x, region.h);
            Renderer.setVertexCoords(bbox.getPoints()[3].x, bbox.getPoints()[3].y);
        }
        Renderer.end();

        Renderer.setTexture(null);
    }

    public Shape shadowbox = new Rect(0, 0, 0, 0);

    public void renderShadow() {
        if (nomodel || sprite == Sprite.empty_sprite || !hasTouch() || rendered) return;

        lights.clear();
        for (Entity e : touched.get_all()) if (e instanceof Light) lights.add((Light) e);

        Rect region = sprite.frames[sprite.getCurrentAnimation().getCurrentFrame()];

        for (Light l : lights.get_all()) {
            Vector2 diff = position.sub(l.position);
            Vector2 n;
            float distance = diff.length();
            float transparency = MathUtils.max(0, 0.8f - (distance / 255f));

            Rect shadow = new Rect(0, 0, getWidth(), getHeight());

            trans = Matrix.translate(-getWidth() / 2, -getHeight() / 2);
            trans.transform(shadow);

            trans = Matrix.rotate(angle);
            trans.transform(shadow);

            trans = Matrix.translate(getWorldX(), getWorldY());
            trans.transform(shadow);

            for (Point p : shadow.getPoints()) {
                diff.x = p.x - l.getWorldX();
                diff.y = p.y - l.getWorldY();

                n = diff.normalize();
                distance = diff.length() * 0.15f;

                p.x += distance * n.x;
                p.y += distance * n.y;
            }

            float y = 0.21f * l.color.r + 0.72f * l.color.g + 0.07f * l.color.b; //Handle light brihgtness

            Renderer.current_shader.setUniform("u_color", 0, 0, 0, transparency * y);
            Renderer.current_shader.setUniform("u_coloronly", true);

            Renderer.setTexture(sprite.texture);

            Renderer.begin(Renderer.R_QUAD);
            {
                Renderer.setTexCoords(region.x, region.y);
                Renderer.setVertexCoords(shadow.getPoints()[0].x, shadow.getPoints()[0].y);
                Renderer.setTexCoords(region.w, region.y);
                Renderer.setVertexCoords(shadow.getPoints()[1].x, shadow.getPoints()[1].y);
                Renderer.setTexCoords(region.w, region.h);
                Renderer.setVertexCoords(shadow.getPoints()[2].x, shadow.getPoints()[2].y);
                Renderer.setTexCoords(region.x, region.h);
                Renderer.setVertexCoords(shadow.getPoints()[3].x, shadow.getPoints()[3].y);
            }
            Renderer.end();

            shadowbox = shadow;
        }

        Renderer.current_shader.setUniform("u_color", 1f, 1f, 1f, 1f);
        Renderer.current_shader.setUniform("u_coloronly", false);

        Renderer.setTexture(null);
    }

    public void touch(Entity e) {
    }

    public final void addChild(Entity obj) {
        obj.parent = this;
        childs.add(obj);
    }

    public final Entity[] getChilds() {
        return childs.get_all();
    }

    public void die() {
    }

    public final void setTarget(Entity target) {
        this.target = target;
    }

    public final boolean hasTarget() {
        return (target != null);
    }

    public final void setBounds(float w, float h) {
        bbox = new Rect(0, 0, w, h);
    }

    public final void setCollisionRect(float x, float y, float w, float h) {
        cbox = new Rect(x - getWidth() / 2, y - getHeight() / 2, w, h);
    }

    public final void setCollisionCircle(float centerx, float centery, int radius, int precize) {
        cbox = new Circle(centerx, centery, radius, precize);
    }

    public final void setViewRect(float x, float y, float w, float h) {
        vbox = new Rect(x - getWidth() / 2, y - getHeight() / 2, w, h);
    }

    public final void setViewCircle(float centerx, float centery, int radius, int precize) {
        vbox = new Circle(centerx, centery, radius, precize);
    }

    public final void setViewArc(float centerx, float centery, int radius, int precize) {
        vbox = new Circle(centerx, centery, radius, precize);
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final EntityPool getVisibleObjects() {
        return visible_objects;
    }

    public final void setSprite(Sprite model) {
        this.sprite = model;
        nomodel = false;

        setBounds(model.frame_width, model.frame_height);
    }

    public final boolean hasModel() {
        return !nomodel && sprite != null && sprite != Sprite.empty_sprite;
    }

    public final void checkCollisions() {
        if (!hasCollisions() || !canCollide()) return;

        float tmp_angle;
        Vector2 tmp_speed;

        for (Entity c : collides.get_all()) {
            if (!c.canCollide()) continue;

            setWorldPosition(old_position.x, old_position.y);

            tmp_angle = MathUtils.angle(getWorldPosition(), c.getWorldPosition()); //Direction to object
            tmp_speed = new Vector2(MathUtils.cos(tmp_angle), MathUtils.sin(tmp_angle)).mul(current_speed.length());

            moveObject(tmp_speed);

            refreshAll();
        }
    }

    public final boolean canCollide() {
        return (solid || trigger);
    }

    public final void addCollision(Entity e) {
        if (e.trigger || e == this) return; //Prevent selfcollision :)

        collides.add(e);
    }

    public final void removeCollision(Entity e) {
        collides.remove(e);
    }

    public final boolean hasCollisions() {
        return !collides.is_empty();
    }

    public void addTouch(Entity e) {
        if (e == this) return; //Prevent selfcollision :)

        touched.add(e);
    }

    public final void removeTouch(Entity e) {
        touched.remove(e);
    }

    public final boolean hasTouch() {
        return !touched.is_empty();
    }

    public final void setWorldPosition(float x, float y) {
        old_position = position;

        position.x = x;
        position.y = y;

        refreshAll();
    }

    public final void moveObject(Vector2 speed) {
        if (movetype == MOVETYPE_STATIC) return;

        old_position = position;

        speed = speed.mul(EngineRuntime.frametime);

        position = position.add(speed);

        refreshAll();
    }

    public final void setSpeed(Vector2 speed) {
        current_speed = speed;
    }

    public boolean isMoving() {
        return (current_speed.length() > 0);
    }

    public final void rotateObject(float angle) {
        if (angle < -180) angle = 180;
        if (angle > 180) angle = -180;

        this.angle = angle;

        refreshAll();
    }

    public final void refreshBBOX() {
        bbox.reset();

        trans = Matrix.translate(-getWidth() / 2, -getHeight() / 2);
        trans.transform(bbox);

        trans = Matrix.rotate(angle);
        trans.transform(bbox);

        trans = Matrix.translate(getWorldX(), getWorldY());
        trans.transform(bbox);
    }

    public final void refreshCBOX() {
        cbox.reset();

        trans = Matrix.rotate(angle);
        trans.transform(cbox);

        trans = Matrix.translate(getWorldX(), getWorldY());
        trans.transform(cbox);
    }

    public final void refreshVBOX() {
        vbox.reset();

        trans = Matrix.rotate(angle);
        trans.transform(vbox);

        trans = Matrix.translate(getWorldX(), getWorldY());
        trans.transform(vbox);
    }

    public final void refreshAll() {
        refreshBBOX();
        refreshCBOX();
        refreshVBOX();
    }

    public final Shape getBBOX() {
        return bbox;
    }

    public final Shape getCBOX() {
        return cbox;
    }

    public final Shape getVBOX() {
        return vbox;
    }

    public final float getWorldX() {
        return position.x;
    }

    public final float getWorldY() {
        return position.y;
    }

    public final Vector2 getWorldPosition() {
        return position;
    }

    public final float getWidth() {
        return getBBOX().getWidth();
    }

    public final float getHeight() {
        return getBBOX().getHeight();
    }

    public final Matrix getMatrix() {
        return trans;
    }

    public final Entity[] getCollides() {
        return collides.get_all();
    }
}
