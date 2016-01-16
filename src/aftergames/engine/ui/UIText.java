package aftergames.engine.ui;

import aftergames.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UIText extends UIItem {

    protected UIFont font;
    protected String text;

    public UIText() {
        setFont(UIFont.getDefault());
        text = new String();
    }

    public UIText(String text) {
        setFont(UIFont.getDefault());
        setSize(font.stringWidth(text), font.getHeight());
        this.text = text;
    }

    public void onDraw() {
        if (!isEmpty()) Renderer.drawString(font, text, x, y, color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setSize(font.stringWidth(text), font.getHeight());
    }

    public boolean isEmpty() {
        return (text == null || text.isEmpty());
    }

    public boolean equals(String s) {
        return s.equals(text);
    }

    public final void setFont(UIFont font) {
        this.font = font;
    }

    public final UIFont getFont() {
        return font;
    }
}
