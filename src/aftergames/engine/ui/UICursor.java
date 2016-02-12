package aftergames.engine.ui;

import aftergames.engine.EngineRuntime;
import aftergames.engine.render.RenderAPI;

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
        RenderAPI.resetClip();

        if (image != null)
            RenderAPI.drawImage(image.getImage(), x, y);
        else
            onDraw();
    }

    public void onDraw() {
        RenderAPI.drawPoint(x, y, color);
        RenderAPI.drawLine(x, y, x, y + 10, color);
        RenderAPI.drawLine(x, y, x + 10, y, color);
        RenderAPI.drawLine(x, y, x + 10, y + 10, color);
    }

    public void onMouseMove() {
        x = EngineRuntime.getMouseX();
        y = EngineRuntime.getMouseY();
    }
}
