package aftergames.engine.utils.pools;

/**
 *
 * @author KiQDominaN
 */
public class IntPool {

    private int[] e;
    private int[] e_tmp;
    private int size;
    private int initialSize = 10;

    public IntPool() {
        e = new int[initialSize];
        e_tmp = new int[initialSize];
    }

    public IntPool(int initial) {
        if (initial > 0) {
            e = new int[initial];
            e_tmp = new int[initial];
        } else {
            e = new int[initialSize];
            e_tmp = new int[initialSize];
        }
    }

    public void put(int i) {
        size++;

        if (size > e.length) {
            int[] tmp = new int[size + 10];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = i;
    }

    public void put(int i, int pos) {
        if (pos > size - 1) put(i);
        else
            e[pos] = i;
    }

    public int get(int i) {
        if (i > size - 1) return -999;
        else return e[i];
    }

    public int[] get_all() {
        e_tmp = new int[size];
        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public int get_last() {
        return get(size - 1);
    }

    public int pop() {
        int t = get_last();

        int[] tmp = new int[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, size - 1);
        e = tmp;

        size--;

        return t;
    }

    public void remove(int i) {
        int[] tmp = new int[size - 1];

        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, size - (i + 1));
        e = tmp;

        size--;
    }

    public void clear() {
        e = new int[initialSize];
        e_tmp = new int[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }
}
