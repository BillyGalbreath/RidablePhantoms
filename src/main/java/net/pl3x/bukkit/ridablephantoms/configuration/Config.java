package net.pl3x.bukkit.ridablephantoms.configuration;

import net.pl3x.bukkit.ridablephantoms.RidablePhantoms;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static float SPEED_MODIFIER = 1.0F;
    public static double DEAD_STICK_GRAVITY = 0.05D;
    public static boolean FALLING_HURTS = true;
    public static boolean IMMUNE_TO_SUNLIGHT = true;

    public static void reload() {
        RidablePhantoms plugin = RidablePhantoms.getPlugin(RidablePhantoms.class);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        SPEED_MODIFIER = (float) config.getDouble("speed-modifier", 1.0D);
        DEAD_STICK_GRAVITY = config.getDouble("dead-stick-gravity", 0.05D);
        FALLING_HURTS = config.getBoolean("falling-hurts", true);
        IMMUNE_TO_SUNLIGHT = config.getBoolean("immune-to-sunlight", true);
    }
}
