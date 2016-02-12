package aftergames.engine.ui.events;

/**
 *
 * @author KiQDominaN
 */
public class UIEventsStack {

    private UIEvent[] events;
    private int size = 0;
    private final int initialSize = 10;

    public UIEventsStack() {
        events = new UIEvent[initialSize];
    }

    public void clear() {
        events = new UIEvent[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public void push(UIEvent event) {
        size++;

        if (size > events.length - 1) {
            UIEvent[] tmp = new UIEvent[size + initialSize];
            System.arraycopy(events, 0, tmp, 0, events.length);
            events = tmp;
        }

        events[size - 1] = event;
    }

    public UIEvent pop() {
        UIEvent t = get_last();

        UIEvent[] tmp = new UIEvent[events.length - 1];
        System.arraycopy(events, 0, tmp, 0, size - 1);
        events = tmp;

        size--;

        return t;
    }

    private UIEvent get(int i) {
        if (i > size - 1) {
            return null;
        } else {
            return events[i];
        }
    }

    private UIEvent get_last() {
        return get(size - 1);
    }
}