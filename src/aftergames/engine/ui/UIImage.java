package aftergames.engine.ui;

import aftergames.engine.render.Renderer;
import aftergames.engine.render.Texture;

/**
 *
 * @author KiQDominaN
 */
public final class UIImage extends UIItem {

    private Texture texture;

    public UIImage(Texture image) {
        texture = image;
        setSize(image.width, image.height);
    }

    public void onDraw() {
        if (texture != null) Renderer.drawImage(texture, x, y);
    }

    public void setImage(Texture image) {
        texture = image;
        setSize(image.width, image.height);
    }

    public Texture getImage() {
        return texture;
    }
}