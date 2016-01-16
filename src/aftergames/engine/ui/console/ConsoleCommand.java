package aftergames.engine.ui.console;

import aftergames.engine.EngineRuntime;
import aftergames.engine.utils.StringUtils;

/**
 *
 * @author KiQDominaN
 */
public class ConsoleCommand {

    public String name;
    public String[] args;
    public String desc;

    protected void init() {
    }

    public void check(String... args) {
        if ((args == null || args.length == 0) && this.args != null) {
            print();
            return;
        }

        if (this.args != null && args != null) {
            System.arraycopy(args, 0, this.args, 0, this.args.length);
            if (this.args.length < args.length)
                EngineRuntime.console.warn_print(StringUtils.concat("Too much arguments, only the first ", String.valueOf(this.args.length), " were used!"));
        }

        exec();
    }

    protected void exec() {
    }

    protected void print() {
        String out = StringUtils.concat(name, " ", StringUtils.arrayToString(args, " "));
        EngineRuntime.console.info_print(out);
    }

}
