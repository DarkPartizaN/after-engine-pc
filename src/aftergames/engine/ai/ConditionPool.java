package aftergames.engine.ai;

/**
 *
 * @author KiQDominaN
 */
public class ConditionPool {

    private Condition[] e;
    private Condition[] e_tmp;
    private int size = 0;
    private int initialSize = 10;

    public ConditionPool() {
        e = new Condition[initialSize];
        e_tmp = new Condition[initialSize];
    }

    public ConditionPool(int initial) {
        if (initial > 0) {
            e = new Condition[initial];
            e_tmp = new Condition[initial];
        } else {
            e = new Condition[initialSize];
            e_tmp = new Condition[initialSize];
        }
    }

    public void add(Condition condition) {
        if (contains(condition)) return;

        size++;

        if (size > e.length - 1) {
            Condition[] tmp = new Condition[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = condition;
    }

    public void add(Condition[] conditions) {
        for (Condition tmp : conditions)
            add(tmp);
    }

    public void put(Condition condition, int pos) {
        if (pos > size - 1) add(condition);
        else
            e[pos] = condition;
    }

    public void swap(int i, int i2) {
        Condition tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public Condition get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public Condition[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new Condition[size];

        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public Condition get_last() {
        return get(size - 1);
    }

    public Condition pop() {
        Condition t = get_last();
        Condition[] tmp = new Condition[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, e.length - 1);
        e = tmp;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        Condition[] tmp = new Condition[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, e.length - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(Condition condition) {
        int i = 0;

        for (Condition a : e) {
            if (a == condition) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new Condition[initialSize];
        e_tmp = new Condition[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Condition condition) {
        for (Condition tmp : e) if (tmp == condition) return true;
        return false;
    }
}
