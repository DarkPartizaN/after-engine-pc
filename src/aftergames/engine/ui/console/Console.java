package aftergames.engine.ui.console;

import aftergames.engine.Controllable;
import aftergames.engine.EngineAPI;
import aftergames.engine.EngineRuntime;
import aftergames.engine.render.Color;
import aftergames.engine.render.Renderer;
import aftergames.engine.ui.UI;
import aftergames.engine.ui.UIPanel;
import aftergames.engine.ui.UIText;
import aftergames.engine.utils.StringUtils;
import aftergames.engine.world.Time;
import java.util.Arrays;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 *
 * @author KiQDominaN
 */
public class Console extends UIPanel {

    private static final int FADEIN = -1;
    private static final int FADEOUT = 1;
    private int fade = 0;

    private float current_y, offset;
    private final float slide_speed = 8f;
    private final long key_delay = 80, carret_delay = 350;
    private long last_key_press = System.currentTimeMillis();
    private long last_carret_blink = System.currentTimeMillis();
    private int line;
    private int lines_max;

    private Color bg_color, bg_line_color;

    private final LinkedList<UIText> lines = new LinkedList<>();
    private final LinkedList<UIText> commands = new LinkedList<>();
    private final LinkedHashSet<ConsoleCommand> registered_commands = new LinkedHashSet<>();

    private final UIText start_line = new UIText();
    private final UIText current_line = new UIText();
    private final UIText carret = new UIText();

    public void init() {
        setVisible(false);
        setEnabled(true);
        setActive(false);

        setPosition(0, 0);
        setSize(EngineRuntime.screen_width, EngineRuntime.screen_height / 2);

        bg_color = new Color(0x88000000);
        bg_line_color = new Color(0x88222222);

        current_y = -getHeight() - 1;

        lines_max = (int) (getHeight() / font.getHeight());

        start_line.setText("> ");
        carret.setText("_");

        add(start_line);
        add(current_line);
        add(carret);

        //Console commands
        addCommand(r_debug);
        addCommand(r_diffuse);
        addCommand(r_lightning);
        addCommand(r_shadows);
        addCommand(r_grayscale);
        addCommand(time);
        addCommand(cmdlist);
        addCommand(clear);
        addCommand(exit);
        addCommand(quit);
    }

    public void onDraw() {
        Renderer.clipRect(x, y, width, height);
        Renderer.fillRect(0, (int) current_y, getWidth(), getHeight(), bg_color);

        //Draw tile background
        if (background != null) {
            //Tiled background
            if (!resize_background) {
                for (int tmp_y = y; tmp_y < height; tmp_y += background.getHeight())
                    for (int tmp_x = x; tmp_x < width; tmp_x += background.getWidth())
                        Renderer.drawImage(background.getImage(), tmp_x, tmp_y);
            } else
                //or fit background
                Renderer.drawImage(background.getImage(), x, y, width, height);
        }

        for (UI e : elements.get_all())
            if (e != null)
                if (e.isVisible())
                    e.onDraw();

        if (!lines.isEmpty()) {
            int tmp_line = lines.size() + 1;
            for (UIText t : lines) {
                t.setPosition(2, (int) (offset + current_y + lines_max * font.getHeight() - font.getHeight() * tmp_line));
                t.onDraw();

                tmp_line--;
            }
        }

        Renderer.resetClip();
        Renderer.drawLine(0, (int) (current_y + getHeight()), getWidth(), (int) (current_y + getHeight()), bg_line_color);
    }

    public void onIdle() {
        start_line.setPosition(2, (int) (current_y + lines_max * font.getHeight() - font.getHeight()));
        current_line.setPosition(2 + font.stringWidth(start_line.getText()), (int) (current_y + lines_max * font.getHeight() - font.getHeight()));
        carret.setPosition(2 + font.stringWidth(start_line.getText()) + current_line.getWidth(), (int) (current_y + lines_max * font.getHeight() - font.getHeight()));

        if (fade == FADEIN) {
            fadein();
            return;
        }

        if (fade == FADEOUT) {
            fadeout();
            return;
        }

        if (isVisible() && isActive()) process();
    }

    public void open() {
        sortCommands();
        fade = FADEIN;
    }

    public void close() {
        fade = FADEOUT;
    }

    private void fadein() {
        setVisible(true);

        current_y += slide_speed * EngineRuntime.frametime;

        if (current_y > getY()) {
            current_y = getY();
            fade = 0;

            setActive(true);
        }
    }

    private void fadeout() {
        current_y -= slide_speed * EngineRuntime.frametime;

        if (current_y < -getHeight() - 1) {
            current_y = -getHeight() - 1;
            fade = 0;

            setVisible(false);
            setActive(false);
        }
    }

    private int tab_press = 0; //Autocomplete

    private void process() {
        long curr_time = System.currentTimeMillis();
        boolean delay = curr_time - last_key_press > key_delay;

        if ((curr_time - last_carret_blink > carret_delay)) {
            if (carret.equals("_")) carret.setText(" ");
            else carret.setText("_");

            last_carret_blink = curr_time;
        }

        if (EngineRuntime.keyPressed(Controllable.KEY_ANY) && !EngineRuntime.keyPressed(Controllable.CONSOLE) && delay) {
            String add = EngineRuntime.getInput();
            if (!add.isEmpty()) {
                EngineRuntime.resetKey(EngineRuntime.lastKey());
                current_line.setText(StringUtils.concat(current_line.getText(), add));
            }

            if (EngineRuntime.keyPressed(Controllable.LINEUP)) {
                if (!commands.isEmpty()) {
                    line--;

                    if (line <= 0) line = 0;

                    current_line.setText(commands.get(line).getText());
                }

                last_key_press = curr_time;
            }

            if (EngineRuntime.keyPressed(Controllable.LINEDOWN)) {
                if (!commands.isEmpty()) {
                    line++;

                    if (line >= commands.size()) {
                        line = commands.size();
                        current_line.setText("");
                    } else
                        current_line.setText(commands.get(line).getText());
                }

                last_key_press = curr_time;
            }

            if (EngineRuntime.keyPressed(Controllable.BACKSPACE) && !current_line.isEmpty()) {
                current_line.setText(current_line.getText().substring(0, current_line.getText().length() - 1));
                last_key_press = curr_time;
            }

            //Autocomplete
            if (EngineRuntime.keyPressed(Controllable.TAB) && !current_line.isEmpty()) {
                if (registered_commands.isEmpty()) return;

                LinkedList<ConsoleCommand> autocomplete = new LinkedList<>();

                for (ConsoleCommand c : registered_commands)
                    if (c.name.startsWith(current_line.getText().trim()))
                        autocomplete.add(c);

                if (autocomplete.isEmpty())
                    info_print(StringUtils.concat("Autocomplete for \"", current_line.getText(), "\" not found!"));
                else {
                    if (autocomplete.size() == 1 || tab_press > 0) {
                        current_line.setText(StringUtils.concat(autocomplete.getFirst().name, " "));
                        tab_press = 0;
                    } else {
                        for (ConsoleCommand c : autocomplete)
                            info_print(c.name);

                        tab_press++;
                    }
                }

                last_key_press = curr_time + 100;
            }

            //Execute!
            if (EngineRuntime.keyPressed(Controllable.ENTER)) {
                EngineRuntime.resetKey(Controllable.ENTER);

                con_print(current_line.getText().trim());
                exec(StringUtils.splitString(StringUtils.replace(current_line.getText().trim(), "  ", " "), " "));

                current_line.setText("");
            }
        }
    }

    public void exec(String cmd) {
        con_print(current_line.getText().trim());
        exec(StringUtils.splitString(StringUtils.replace(current_line.getText().trim(), "  ", " "), " "));

        current_line.setText("");
    }

    private void exec(String... cmd) {
        UIText tmp = new UIText(current_line.getText());

        commands.add(tmp);
        line = commands.size();

        if (registered_commands.isEmpty()) {
            warn_print(StringUtils.concat("Command list is empty!"));
            return;
        }

        String name = cmd[0];

        String[] args = null;
        if (cmd.length > 1) {
            args = new String[cmd.length - 1];
            System.arraycopy(cmd, 1, args, 0, cmd.length - 1);
        }

        for (ConsoleCommand c : registered_commands) {
            if (name.equals(c.name)) {
                c.check(args);
                return;
            }
        }
        warn_print(StringUtils.concat("Command ", name, " was not found!")); // Command not registered
    }

    public void con_print(String s) {
        UIText t = new UIText();
        t.setText(s);

        lines.add(t);
    }

    public void info_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.green);
        t.setText(StringUtils.concat("[I] ", s));

        lines.add(t);
    }

    public void debug_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.cyan);
        t.setText(StringUtils.concat("[D] ", s));

        lines.add(t);
    }

    public void warn_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.yellow);
        t.setText(StringUtils.concat("[W] ", s));

        lines.add(t);
    }

    public void err_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.red);
        t.setText(StringUtils.concat("[E] ", s));

        lines.add(t);
    }

    public void addCommand(ConsoleCommand cmd) {
        cmd.init();
        registered_commands.add(cmd);
    }

    private void sortCommands() {
        ConsoleCommand[] tmp;
        ConsoleCommand cc1, cc2, ctmp;
        char c1, c2;

        tmp = registered_commands.toArray(new ConsoleCommand[registered_commands.size()]);
        for (ConsoleCommand c : tmp) System.out.println(c.name);
        System.out.println("--------------------------------------");

        for (int i = 0; i < registered_commands.size(); i++) {
            for (int j = registered_commands.size() - 2; j >= i; j--) {
                cc1 = tmp[j];
                cc2 = tmp[j + 1];
                
                c1 = cc1.name.charAt(0);
                c2 = cc2.name.charAt(0);

                if (c1 > c2) {
                    ctmp = tmp[j];
                    tmp[j] = cc2;
                    tmp[j + 1] = ctmp;
                }
            }
        }

        registered_commands.clear();
        registered_commands.addAll(Arrays.asList(tmp));
    }

    //===============
    //CONSOLE COMMANDS
    //===============
    public ConsoleCommand exit = new ConsoleCommand() {
        public void init() {
            name = "exit";
            args = null;
            desc = "Exit application";
        }

        public void exec() {
            EngineAPI.stopEngine();
        }

        public void print() {
            info_print("Exiting app");
        }
    };

    public ConsoleCommand quit = new ConsoleCommand() {
        public void init() {
            name = "quit";
            args = null;
            desc = "Exit application";
        }

        public void exec() {
            EngineAPI.stopEngine();
        }

        public void print() {
            info_print("Exiting app");
        }
    };

    public ConsoleCommand r_lightning = new ConsoleCommand() {

        public void init() {
            name = "r_lightning";

            args = new String[1];
            args[0] = args[0] = (Renderer.r_lightning) ? "1" : "0";

            desc = "On/off lights";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Renderer.r_lightning = args[0].toLowerCase().equals("1");
        }
    };

    public ConsoleCommand r_grayscale = new ConsoleCommand() {

        public void init() {
            name = "r_grayscale";

            args = new String[1];
            args[0] = args[0] = (Renderer.r_grayscale) ? "1" : "0";

            desc = "On/off grayscale mode";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Renderer.r_grayscale = args[0].toLowerCase().equals("1");
        }
    };

    public ConsoleCommand r_diffuse = new ConsoleCommand() {

        public void init() {
            name = "r_diffuse";

            args = new String[1];
            args[0] = args[0] = (Renderer.r_diffuse) ? "1" : "0";

            desc = "On/off objects drawing";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Renderer.r_diffuse = args[0].toLowerCase().equals("1");
        }
    };

    public ConsoleCommand r_shadows = new ConsoleCommand() {

        public void init() {
            name = "r_shadows";

            args = new String[1];
            args[0] = args[0] = (Renderer.r_shadows) ? "1" : "0";

            desc = "On/off shadows";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Renderer.r_shadows = args[0].toLowerCase().equals("1");
        }
    };

    public ConsoleCommand r_debug = new ConsoleCommand() {

        public void init() {
            name = "r_debug";

            args = new String[1];
            args[0] = (Renderer.r_debug) ? "1" : "0";

            desc = "Draw debug info";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Renderer.r_debug = args[0].toLowerCase().equals("1");
        }

        public void print() {
            String out = StringUtils.concat(name, " ", StringUtils.arrayToString(args, " "));
            info_print(out);
        }
    };

    public ConsoleCommand time = new ConsoleCommand() {

        public void init() {
            name = "time";

            args = new String[1];
            args[0] = "0";

            desc = "Set game time (hour only)";
        }

        public void exec() {
            if (args == null || args.length == 0) {
                print();
                return;
            }

            Time.hours = Integer.parseInt(args[0]);
            Time.minutes = 0;
        }

        public void print() {
            info_print(StringUtils.concat("Time: ", Time.timeOfDay));
        }
    };

    public ConsoleCommand cmdlist = new ConsoleCommand() {

        public void init() {
            name = "cmdlist";
            args = null;
            desc = "List registered commands";
        }

        public void exec() {
            print();
        }

        public void print() {
            for (ConsoleCommand c : registered_commands)
                info_print(StringUtils.concat(c.name, " - ", c.desc));
        }
    };

    public ConsoleCommand clear = new ConsoleCommand() {

        public void init() {
            name = "clear";
            args = null;
            desc = "Clear console";
        }

        public void exec() {
            lines.clear();
        }
    };

}
