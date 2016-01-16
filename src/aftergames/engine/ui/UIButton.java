package aftergames.engine.ui;

import aftergames.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public class UIButton extends UIPanel {

    protected UIText label;

    public UIButton() {
        setBackColor(Color.transparent);
        resizeBackground(true);

        label = new UIText();

        add(label);
    }

    public void setLabel(String s) {
        label.setText(s);

        setLabelPosition(0, 0);
        setSize(label.getWidth(), label.getHeight());
    }

    public void setLabelColor(Color c) {
        label.setColor(c);
    }

    public void setLabelPosition(int x, int y) {
        label.setPosition(x, y);
    }
}
