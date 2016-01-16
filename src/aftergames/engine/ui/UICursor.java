package aftergames.engine.ui;

import aftergames.engine.EngineRuntime;
import aftergames.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UICursor extends UIItem {

    protected UIImage image;

    public void setImage(UIImage image) {
        this.image = image;
        if (image != null)
            setSize(image.width, image.height);
    }

    public void draw() {
        Renderer.resetClip();

        if (image != null)
            Renderer.drawImage(image.getImage(), x, y);
        else
            onDraw();
    }

    public void onDraw() {
        Renderer.drawPoint(x, y, color);
        Renderer.drawLine(x, y, x, y + 10, color);
        Renderer.drawLine(x, y, x + 10, y, color);
        Renderer.drawLine(x, y, x + 10, y + 10, color);
    }

    public void onMouseMove() {
        x = EngineRuntime.getMouseX();
        y = EngineRuntime.getMouseY();
    }
}
