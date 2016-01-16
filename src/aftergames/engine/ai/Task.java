package aftergames.engine.ai;

/**
 *
 * @author KiQDominaN
 */
public class Task {

    public boolean initialized = false;
    private long next_update;
    private long last_update = System.currentTimeMillis();

    public ConditionPool choose_conditions = new ConditionPool();
    public ConditionPool done_conditions = new ConditionPool();
    public ConditionPool interrupt_conditions = new ConditionPool();

    public void init() {
    }

    public void update() {
    }

    //TODO: difference of interrupt and done (it's important!)
    public boolean done() {
        boolean done = true;

        for (Condition c : done_conditions.get_all()) if (!c.satisfied()) done = false;
        for (Condition c : interrupt_conditions.get_all()) if (c.satisfied()) done = true;

        return done;
    }

    public void addCondition(Condition c) {
        if (interrupt_conditions.contains(c)) interrupt_conditions.remove(c);

        choose_conditions.add(c);
        interrupt_conditions.add(c.invert()); //Automatically prevent potentially erroneous logic
    }

    public void addDoneCondition(Condition c) {
        done_conditions.add(c);
    }

    public void addInterrupt(Condition c) {
        if (choose_conditions.contains(c)) choose_conditions.remove(c);
        interrupt_conditions.add(c);
    }

    public void addInterrupt(Task t) {
        for (Condition c : t.choose_conditions.get_all()) addInterrupt(c);
    }

    public void setNextUpdate(float time) {
        next_update = (long) (time * 1000);
    }

    public boolean canUpdate() {
        if (System.currentTimeMillis() - last_update >= next_update) {
            last_update = System.currentTimeMillis();

            return true;
        }

        return false;
    }

}
