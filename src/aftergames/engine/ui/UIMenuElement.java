package aftergames.engine.ui;

import aftergames.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public class UIMenuElement extends UIButton {

    private Color old_color;

    public void onFocus() {
        old_color = background_color;

        setBackColor(background_color.mix(Color.black));
    }

    public void onFocusLost() {
        setBackColor(old_color);
    }
}
