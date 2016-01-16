package aftergames.engine.ai;

/**
 *
 * @author KiQDominaN
 */
public class Condition {

    public boolean satisfied() {
        return false;
    }

    public Condition invert() {
        final Condition c = this;

        return new Condition() {
            public boolean satisfied() {
                return !c.satisfied();
            }
        };
    }

}
