package aftergames.engine.world;

/**
 *
 * @author KiQDominaN
 */
public class LightPool {

    private Light[] e;
    private Light[] e_tmp;
    private int size = 0;
    private int initialSize = 10;

    public LightPool() {
        e = new Light[initialSize];
        e_tmp = new Light[initialSize];
    }

    public LightPool(int initial) {
        if (initial > 0) {
            e = new Light[initial];
            e_tmp = new Light[initial];
        } else {
            e = new Light[initialSize];
            e_tmp = new Light[initialSize];
        }
    }

    public void add(Light light) {
        if (contains(light)) return;

        size++;

        if (size > e.length - 1) {
            Light[] tmp = new Light[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = light;
    }

    public void add(Light[] light) {
        for (Light l : light)
            add(l);
    }

    public void put(Light light, int pos) {
        if (pos > size - 1) add(light);
        else
            e[pos] = light;
    }

    public void swap(int i, int i2) {
        Light tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public Light get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public Light[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new Light[size];

        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public Light get_last() {
        return get(size - 1);
    }

    public Light pop() {
        Light t = get_last();

        Light[] tmp = new Light[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, size - 1);
        e = tmp;

        size--;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        Light[] tmp = new Light[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, size - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(Light obj) {
        int i = 0;

        for (Light a : e) {
            if (a == obj) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new Light[initialSize];
        e_tmp = new Light[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Light light) {
        for (Light tmp : e) if (tmp == light) return true;
        return false;
    }

}
