package aftergames.engine.ui;

import aftergames.engine.EngineRuntime;

/**
 *
 * @author KiQDominaN
 */
public final class UIManager {

    private static UIPool uis = new UIPool();

    public static void destroy(UI ui) {
        ui.onDestroy();

        if (uis.contains(ui)) uis.remove(ui);
    }

    public static void add(UI ui) {
        uis.add(ui);
    }

    public static void remove(UI ui) {
        uis.remove(ui);
    }

    public static void clear() {
        uis.clear();
    }

    public static void show(UI ui) {
        EngineRuntime.resetKeys(); //HACKHACK
        ui.setVisible(true);
    }

    public static void show(UI ui, int x, int y) {
        EngineRuntime.resetKeys(); //HACKHACK
        ui.setPosition(x, y);
        ui.setVisible(true);
    }

    public static void hide(UI ui) {
        EngineRuntime.resetKeys(); //HACKHACK
        ui.setVisible(false);
    }

    public static int size() {
        return uis.size();
    }

    public static void update() {
        for (UI ui : uis.get_all()) ui.update();
    }

    public static void draw() {
        sort();

        for (UI ui : uis.get_all()) {
            if (ui.isVisible())
                ui.draw();
        }
    }

    private static void sort() {
        UI ui1, ui2;

        for (int i = 0; i < size(); i++) {
            for (int j = size() - 2; j >= i; j--) {
                ui1 = uis.get(j);
                ui2 = uis.get(j + 1);

                if (ui1.layer < ui2.layer) uis.swap(j, j + 1);
            }
        }
    }

    public static boolean is_empty() {
        return uis.is_empty();
    }
}
