package aftergames.engine.ui;

import aftergames.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public class UIMenu extends UIItem {

    public static final int HORIZONTAL = 0, VERTICAL = 1;
    protected int orientation;

    public UIMenu(int orientation) {
        this.orientation = orientation;
        setBackColor(Color.gray);
    }

    public void onIdle() {
        //Resize
        if (orientation == VERTICAL) {
            int max_width = elements.get(0).getWidth();

            for (UI e : getElements())
                if (max_width < e.getWidth()) max_width = e.getWidth();
            for (UI e : getElements())
                e.setWidth(max_width);

            setWidth(max_width);
        }
    }

    public void add(UIMenuElement e) {
        if (orientation == HORIZONTAL) {
            if (!elements.is_empty()) {
                UI last = elements.get_last();
                e.setPosition(last.getX() + last.getWidth() + 2, 0);
            }
        } else if (orientation == VERTICAL) {
            if (!elements.is_empty()) {
                UI last = elements.get_last();
                e.setPosition(0, last.getY());

                setHeight(e.getY() + e.getHeight());
            }
        }

        super.add(e);
    }

}