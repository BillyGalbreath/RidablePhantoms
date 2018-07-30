package net.pl3x.bukkit.ridablephantoms.configuration;

import net.pl3x.bukkit.ridablephantoms.RidablePhantoms;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String RIDE_NO_PERMISSION;
    public static String VERSION;
    public static String RELOAD;

    public static void reload() {
        RidablePhantoms plugin = RidablePhantoms.getPlugin(RidablePhantoms.class);
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&cYou do not have permission for that command!");
        RIDE_NO_PERMISSION = config.getString("ride-no-permission", "&cYou do not have permission to ride!");
        VERSION = config.getString("version", "&d{plugin} v{version}.");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded config.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
