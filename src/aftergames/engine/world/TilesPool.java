package aftergames.engine.world;

/**
 *
 * @author KiQDominaN
 */
public class TilesPool {

    private Tiles[] e;
    private Tiles[] e_tmp;
    private int size = 0;
    private int initialSize = 10;

    public TilesPool() {
        e = new Tiles[initialSize];
        e_tmp = new Tiles[initialSize];
    }

    public TilesPool(int initial) {
        if (initial > 0) {
            e = new Tiles[initial];
            e_tmp = new Tiles[initial];
        } else {
            e = new Tiles[initialSize];
            e_tmp = new Tiles[initialSize];
        }
    }

    public void add(Tiles i) {
        if (contains(i)) return;

        size++;

        if (size > e.length - 1) {
            Tiles[] tmp = new Tiles[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = i;
    }

    public void put(Tiles t, int pos) {
        if (pos > size - 1) add(t);
        else
            e[pos] = t;
    }

    public void swap(int i, int i2) {
        Tiles tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public Tiles get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public Tiles[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new Tiles[size];

        System.arraycopy(e, 0, e_tmp, 0, size);
        return e_tmp;
    }

    public Tiles get_last() {
        return get(size - 1);
    }

    public Tiles pop() {
        Tiles t = get_last();

        Tiles[] tmp = new Tiles[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, size - 1);
        e = tmp;

        size--;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        Tiles[] tmp = new Tiles[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, size - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(Tiles t) {
        if (is_empty()) return;

        int i = 0;

        for (Tiles a : e) {
            if (a == t) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new Tiles[initialSize];
        e_tmp = new Tiles[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Tiles i) {
        for (Tiles tmp : e) if (tmp == i) return true;
        return false;
    }

}
