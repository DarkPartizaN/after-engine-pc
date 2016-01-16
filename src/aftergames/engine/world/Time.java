package aftergames.engine.world;

import aftergames.engine.EngineRuntime;
import aftergames.engine.utils.StringUtils;

/**
 *
 * @author DominaN
 */
public class Time {

    public final static int DEFAULT_TIME_SPEED = 100;
    public static int timeSpeed = DEFAULT_TIME_SPEED;
    private static long lastTime = System.currentTimeMillis();
    public static long time;
    //Game time
    public static int SEASON_SUMMER = 0, SEASON_AUTUMN = 1, SEASON_WINTER = 2, SEASON_SPRING = 3;
    public static int seconds = 0, minutes = 0, hours = 0, days = 0, months = 0, years = 0, season = 0;
    public static String timeOfDay = "00:00";

    public static void checkTime() {
        if (timeSpeed <= 0) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > 1000 / timeSpeed) {
            long delta = (long) (timeSpeed / 1000f * EngineRuntime.frametime);
            if (delta <= 1) delta = 1;

            time += delta;
            seconds += delta;

            lastTime = currentTime;
        }

        if (seconds > 59) {
            minutes += seconds / 60;
            seconds = 0;
        }

        if (minutes > 59) {
            hours += minutes / 60;
            minutes = 0;
        }

        if (hours > 23) {
            days += hours / 24;
            hours = 0;
        }

        if (days > 30) {
            months += days / 30;
            days = 0;
        }

        if (months > 12) {
            years += months / 12;
            months = 0;
        }

        //Season change
        season = months % 4;

        timeOfDay = StringUtils.concat((hours < 10) ? StringUtils.concat("0", String.valueOf(hours)) : String.valueOf(hours), (minutes < 10) ? StringUtils.concat(":0", String.valueOf(minutes)) : StringUtils.concat(":", String.valueOf(minutes)));
    }
}
