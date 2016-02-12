package aftergames.engine.world;

/**
 *
 * @author KiQDominaN
 */
public class EntityPool {

    private Entity[] e;
    private Entity[] e_tmp;
    private int size = 0;
    private final int initialSize = 10;

    public EntityPool() {
        e = new Entity[initialSize];
        e_tmp = new Entity[initialSize];
    }

    public EntityPool(int initial) {
        if (initial > 0) {
            e = new Entity[initial];
            e_tmp = new Entity[initial];
        } else {
            e = new Entity[initialSize];
            e_tmp = new Entity[initialSize];
        }
    }

    public void add(Entity entity) {
        if (contains(entity)) return;

        size++;

        if (size > e.length - 1) {
            Entity[] tmp = new Entity[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = entity;
    }

    public void add(Entity[] entities) {
        for (Entity tmp : entities)
            add(tmp);
    }

    public void put(Entity entity, int pos) {
        if (pos > size - 1) add(entity);
        else
            e[pos] = entity;
    }

    public void swap(int i, int i2) {
        Entity tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public Entity get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public Entity[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new Entity[size];

        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public Entity get_last() {
        return get(size - 1);
    }

    public Entity pop() {
        Entity t = get_last();

        Entity[] tmp = new Entity[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, size - 1);
        e = tmp;

        size--;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        Entity[] tmp = new Entity[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, size - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(Entity entity) {
        if (is_empty()) return;

        int i = 0;

        for (Entity a : e) {
            if (a == entity) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new Entity[initialSize];
        e_tmp = new Entity[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Entity entity) {
        for (Entity tmp : e) if (tmp == entity) return true;
        return false;
    }

    public void invert() {
        for (int i = 0; i < e.length; i++) {
            Entity temp = e[i];
            e[i] = e[e.length - i - 1];
            e[e.length - i - 1] = temp;
        }
    }
}
