package aftergames.engine.ui;

import aftergames.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UIPanel extends UIItem {

    protected UIImage background;
    protected boolean resize_background;
    private UICursor cursor;
    protected UIFont font;

    public UIPanel() {
        font = UIFont.getDefault();
    }

    public final void setCursor(UICursor cursor) {
        this.cursor = cursor;
    }

    public final void setCursorRecursive(UICursor cursor) {
        this.cursor = cursor;

        for (UI ui : getElements()) ((UIPanel) ui).setCursor(cursor);
    }

    public final void resetCursor() {
        cursor = null;
    }

    public final UICursor getCursor() {
        return cursor;
    }

    public final void setBackground(UIImage background) {
        this.background = background;
    }

    public final UIImage getBackground() {
        return background;
    }

    public final void resizeBackground(boolean resize) {
        resize_background = resize;
    }

    public final void draw() {
        if (!isVisible()) return;

        sort();

        Renderer.clipRect(x, y, width, height);
        Renderer.fillRect(x, y, width, height, background_color);

        //Draw background
        if (background != null) {
            //Tiled background
            if (!resize_background) {
                for (int tmp_y = y; tmp_y < height; tmp_y += background.getHeight())
                    for (int tmp_x = x; tmp_x < width; tmp_x += background.getWidth())
                        Renderer.drawImage(background.getImage(), tmp_x, tmp_y);
            } else
                //or fit background
                Renderer.drawImage(background.getImage(), x, y, width, height);
        }

        for (UI e : elements.get_all())
            if (e != null)
                if (e.isVisible()) e.draw();

        onDraw();

        Renderer.resetClip();

        if (cursor != null) {
            if (cursor.isEnabled()) cursor.update(); //HACKHACK
            if (cursor.isVisible()) cursor.draw();
        }
    }
}
