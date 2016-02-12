package aftergames.engine.world;

/**
 *
 * @author KiQDominaN
 */
public class LayerManager {

    private Layer[] l;
    private Layer[] l_tmp;
    private int size = 0;
    private final int initialSize = 10;

    public LayerManager() {
        l = new Layer[initialSize];
        l_tmp = new Layer[initialSize];
    }

    public LayerManager(int initial) {
        if (initial > 0) {
            l = new Layer[initial];
            l_tmp = new Layer[initial];
        } else {
            l = new Layer[initialSize];
            l_tmp = new Layer[initialSize];
        }
    }

    public void add(Layer layer) {
        if (contains(layer)) return;

        size++;

        if (size > l.length - 1) {
            Layer[] tmp = new Layer[size + initialSize];
            System.arraycopy(l, 0, tmp, 0, l.length);
            l = tmp;
        }

        l[size - 1] = layer;
    }

    public void put(Layer layer, int pos) {
        if (pos > size - 1) add(layer);
        else
            l[pos] = layer;
    }

    public void swap(int i, int i2) {
        Layer tmp = get(i);
        l[i] = l[i2];
        l[i2] = tmp;
    }

    public Layer get(int i) {
        if (i > size - 1) return null;
        else return l[i];
    }

    public Layer get(String name) {
        for (Layer tmp : l) if (tmp.name.equals(name))
            return tmp;
        return null;
    }

    public Layer[] get_all() {
        if (l_tmp.length != size)
            l_tmp = new Layer[size];

        System.arraycopy(l, 0, l_tmp, 0, size);

        return l_tmp;
    }

    public Layer get_last() {
        return get(size - 1);
    }

    public void remove(int i) {
        l[i] = null;

        Layer[] tmp = new Layer[l.length - 1];
        System.arraycopy(l, 0, tmp, 0, i);
        System.arraycopy(l, i + 1, tmp, i, size - (i + 1));
        l = tmp;

        size--;
    }

    public void remove(Layer layer) {
        if (is_empty()) return;

        int i = 0;

        for (Layer a : l) {
            if (a == layer) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        l = new Layer[initialSize];
        l_tmp = new Layer[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Layer layer) {
        for (Layer tmp : l) if (tmp == layer) return true;
        return false;
    }

    public boolean contains(String name) {
        for (Layer tmp : l) if (tmp.name.equals(name)) return true;
        return false;
    }
}