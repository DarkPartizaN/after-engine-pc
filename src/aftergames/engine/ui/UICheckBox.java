package aftergames.engine.ui;

import aftergames.engine.render.Color;
import aftergames.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UICheckBox extends UIItem {

    private boolean checked;

    public void onCreate() {
        setSize(UIFont.getDefault().getHeight(), UIFont.getDefault().getHeight());
        setColor(Color.white);
        setBackColor(Color.gray);
        setEventDelay(70);

        checked = false;
    }

    public void onDraw() {
        Renderer.drawRect(x, y, width, height, background_color.mix(Color.white));

        if (checked) Renderer.fillRect(x + 2, y + 2, width - 4, height - 4, color);
    }

    public final void onClick() {
        checked = !checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
