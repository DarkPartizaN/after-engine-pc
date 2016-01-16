package aftergames.engine.ai;

/**
 *
 * @author KiQDominaN
 */
public class TaskPool {

    private Task[] e;
    private Task[] e_tmp;
    private int size = 0;
    private int initialSize = 10;

    public TaskPool() {
        e = new Task[initialSize];
        e_tmp = new Task[initialSize];
    }

    public TaskPool(int initial) {
        if (initial > 0) {
            e = new Task[initial];
            e_tmp = new Task[initial];
        } else {
            e = new Task[initialSize];
            e_tmp = new Task[initialSize];
        }
    }

    public void add(Task task) {
        if (contains(task)) return;

        size++;

        if (size > e.length - 1) {
            Task[] tmp = new Task[size + initialSize];
            System.arraycopy(e, 0, tmp, 0, e.length);
            e = tmp;
        }

        e[size - 1] = task;
    }

    public void add(Task[] tasks) {
        for (Task tmp : tasks)
            add(tmp);
    }

    public void put(Task task, int pos) {
        if (pos > size - 1) add(task);
        else
            e[pos] = task;
    }

    public void swap(int i, int i2) {
        Task tmp = get(i);
        e[i] = e[i2];
        e[i2] = tmp;
    }

    public Task get(int i) {
        if (i > size - 1) return null;
        else return e[i];
    }

    public Task[] get_all() {
        if (e_tmp.length != size)
            e_tmp = new Task[size];

        System.arraycopy(e, 0, e_tmp, 0, size);

        return e_tmp;
    }

    public Task get_last() {
        return get(size - 1);
    }

    public Task pop() {
        Task t = get_last();

        Task[] tmp = new Task[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, size - 1);
        e = tmp;

        size--;

        return t;
    }

    public void remove(int i) {
        e[i] = null;

        Task[] tmp = new Task[e.length - 1];
        System.arraycopy(e, 0, tmp, 0, i);
        System.arraycopy(e, i + 1, tmp, i, size - (i + 1));
        e = tmp;

        size--;
    }

    public void remove(Task task) {
        int i = 0;

        for (Task a : e) {
            if (a == task) {
                remove(i);
                return;
            }

            i++;
        }
    }

    public void clear() {
        e = new Task[initialSize];
        e_tmp = new Task[initialSize];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean is_empty() {
        return size == 0;
    }

    public boolean contains(Task task) {
        for (Task tmp : e) if (tmp == task) return true;
        return false;
    }
}
