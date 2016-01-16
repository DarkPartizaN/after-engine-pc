package aftergames.engine.ui;

/**
 *
 * @author KiQDominaN
 */
public class UIPool {

    private UI[] e;
    private UI[] e_tmp;
    private int size = 0;
    private int initialSize = 10;

    public UIPool() {
        e = new UI[initialSize];
        e_tmp = new UI[initialSize];
    }

    public UIPool(int initial) {
        if (initial > 0) {
            e = new UI[initial];
            e_tmp = new UI[initial];
        } else {
            e = new UI[initialSize];
            e_tmp = new UI[initialSize];
        }
    }

    public void add(UI ui) {
        if (contains(ui)) return;

        size++;

        if (size > e.length - 1) {
            UI[] tmp = new UI[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = ui;
    }

    public void add(UI[] ui) {
        for (UI tmp : ui)
            add(tmp);
    }

    public void put(UI ui, int pos) {
        if (pos > size - 1) add(ui);
        else
            e[pos] = ui;
    }

    public void swap(int i, int i2) {
        UI tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public UI get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public UI[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new UI[size];

        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public UI get_last() {
        return get(size - 1);
    }

    public UI pop() {
        UI t = get_last();
        UI[] tmp = new UI[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, e.length - 1);
        e = tmp;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        UI[] tmp = new UI[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, e.length - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(UI ui) {
        if (is_empty()) return;
        if (ui == null) return;

        int i = 0;

        for (UI a : e) {
            if (a == ui) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new UI[initialSize];
        e_tmp = new UI[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(UI ui) {
        for (UI tmp : e) if (tmp == ui) return true;
        return false;
    }

    public void invert() {
        for (int i = 0; i < e.length; i++) {
            UI temp = e[i];
            e[i] = e[e.length - i - 1];
            e[e.length - i - 1] = temp;
        }
    }
}
