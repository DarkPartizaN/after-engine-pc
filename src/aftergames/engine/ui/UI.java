package aftergames.engine.ui;

import aftergames.engine.Controllable;
import aftergames.engine.EngineRuntime;

/**
 *
 * @author KiQDominaN
 */
public abstract class UI {

    //Visible flags
    public boolean visible;
    //Enable flags
    public boolean enabled;
    //Event flags
    public boolean active;
    public boolean focused;
    //Shared variables
    protected int x, y, width, height;
    protected int layer; //Draw order
    //Parent
    private UI parent;
    //Elements
    protected UIItemPool elements;
    //Event system
    protected long last_event;
    protected long event_delay;

    public UI() {
        visible = true;
        active = true;
        enabled = true;

        elements = new UIItemPool();
        last_event = System.currentTimeMillis();
    }

    //Abstract methods
    public abstract void draw();

    //Events
    public abstract void onCreate();

    public abstract void onShow();

    public abstract void onEnable();

    public abstract void onIdle();

    public abstract void onFocus();

    public abstract void onFocusLost();

    public abstract void onMouseMove();

    public abstract void onClick();

    public abstract void onKeyPressed();

    public abstract void onKeyReleased();

    public abstract void onDraw();

    public abstract void onDisable();

    public abstract void onHide();

    public abstract void onDestroy();

    //Element management
    public final void add(UI e) {
        if (elements.contains(e)) return;

        e.parent = this;
        e.setPosition(x + e.x, y + e.y);

        elements.add(e);
    }

    public final void remove(UI e) {
        if (elements.is_empty()) return;
        if (!elements.contains(e)) return;

        elements.remove(e);

        if (e.parent == this) e.parent = null;
        e.setPosition(e.x - x, e.y - y);
    }

    public final void setPosition(int x, int y) {
        int delta_x = x - getX();
        int delta_y = y - getY();

        this.x += delta_x;
        this.y += delta_y;

        for (UI e : elements.get_all())
            e.setPosition(e.getX() + delta_x, e.getY() + delta_y);
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public final void setWidth(int width) {
        this.width = width;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean hasParent() {
        return (parent != null);
    }

    public final UI getParent() {
        return parent;
    }

    public UI[] getElements() {
        return elements.get_all();
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;

        if (visible)
            onShow();
        else
            onHide();
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setEnabled(boolean enable) {
        this.enabled = enable;

        if (enable)
            onEnable();
        else
            onDisable();
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public final boolean isActive() {
        return active;
    }

    public final boolean canEvent() {
        if (hasParent()) if (!parent.isActive() || !parent.isVisible()) return false;
        if (!isActive() || !isVisible()) return false;

        if (System.currentTimeMillis() - last_event > event_delay) {
            last_event = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public final void setEventDelay(int delay) {
        event_delay = delay;
    }

    private boolean was_focused; //HACKHACK

    //Processing
    public final void update() {
        onIdle();

        focused = EngineRuntime.getMouseX() > x && EngineRuntime.getMouseX() < x + width && EngineRuntime.getMouseY() > y && EngineRuntime.getMouseY() < y + height;

        //UI events
        if (canEvent()) {
            if (focused) {
                if (!was_focused) {
                    was_focused = true;
                    onFocus();
                }

                if (EngineRuntime.keyPressed(0)) onClick();
                if (EngineRuntime.keyPressed(Controllable.KEY_ANY)) onKeyPressed();
            } else {
                if (was_focused) {
                    was_focused = false;
                    onFocusLost();
                }
            }

            if (EngineRuntime.mouseMoved()) onMouseMove();
        }

        for (UI e : elements.get_all()) e.update();
    }
}
