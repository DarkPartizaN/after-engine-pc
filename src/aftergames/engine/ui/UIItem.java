package aftergames.engine.ui;

import aftergames.engine.render.Color;
import aftergames.engine.render.RenderAPI;

/**
 *
 * @author KiQDominaN
 */
public class UIItem extends UI {

    protected Color color;
    protected Color background_color;

    public UIItem() {
        setColor(Color.white);
        setBackColor(Color.transparent);

        onCreate();
    }

    public final void setColor(Color color) {
        this.color = color;
    }

    public final Color getColor() {
        return color;
    }

    public final void setBackColor(Color color) {
        this.background_color = color;
    }

    public final Color getBackColor() {
        return background_color;
    }

    //
    //Not used events, override it if you need
    //
    public void draw() {
        if (!isVisible()) return;

        sort();

        RenderAPI.clipRect(x, y, width, height);
        RenderAPI.fillRect(x, y, width, height, background_color);

        for (UI e : elements.get_all())
            if (e != null)
                if (e.isVisible()) e.draw();

        onDraw();

        RenderAPI.resetClip();
    }

    protected void sort() {
        UI ui1, ui2;

        for (int i = 0; i < elements.size(); i++) {
            for (int j = elements.size() - 2; j >= i; j--) {
                ui1 = elements.get(j);
                ui2 = elements.get(j + 1);

                if (ui1.layer > ui2.layer) elements.swap(j, j + 1);
            }
        }
    }

    public void onIdle() {
    }

    public void onFocus() {
    }

    public void onFocusLost() {
    }

    public void onMouseMove() {
    }

    public void onClick() {
    }

    public void onKeyPressed() {
    }

    public void onKeyReleased() {
    }

    public void onShow() {
    }

    public void onHide() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onCreate() {
    }

    public void onDestroy() {
    }

    public void onDraw() {
    }
}
