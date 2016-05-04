/* 
 * Copyright (C) 2016 toyblocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.llv.nest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jp.llv.nest.command.exceptions.CommandException;
import jp.llv.nest.command.obj.bukkit.BukkitConsole;
import jp.llv.nest.listener.ChatListener;
import jp.llv.nest.listener.CommandListener;
import jp.llv.nest.listener.ConsoleListener;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author toyblocks
 */
public class NestPlugin extends JavaPlugin {

    private String prefix = "/n:";
    private NestAPIBukkitImpl api;
    private boolean debug = true;

    @Override
    public void onEnable() {
        this.api = new NestAPIBukkitImpl(this, false);

        this.saveDefaultConfig();
        Configuration config = this.getConfig();
        this.prefix = config.getString("prefix", this.prefix);
        config.getStringList("defaults").stream().forEach(defCmdClass -> {
            try {
                Class<?> clazz = Class.forName(defCmdClass);
                Object cmd;
                try {
                    cmd = clazz.newInstance();
                } catch (IllegalAccessException | InstantiationException ex) {
                    cmd = clazz.getConstructor(NestAPI.class).newInstance(this.getAPI());
                }
                this.getAPI().registerFunc(cmd);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | RuntimeException ex) {
                this.getLogger().log(Level.WARNING, "Failed to load default command '" + defCmdClass + "'", ex);
            }
        });

        this.saveResource("config.st", false);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(this.getDataFolder(), "config.st")))) {
            this.api.execute(BukkitConsole.getInstance(), br.lines().collect(Collectors.joining("\n")))
                    .get();
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed to load initialize command file", ex);
        } catch (CommandException | InterruptedException | ExecutionException ex) {
            this.getLogger().log(Level.WARNING, "Failed to execute initialize command file", ex);
        }
        
        if (prefix.startsWith("/")) {
            this.getServer().getPluginManager().registerEvents(new CommandListener(this, prefix), this);
            this.getServer().getPluginManager().registerEvents(new ConsoleListener(this, prefix.substring(1, this.prefix.length() - 1)), this);
        } else {
            this.getServer().getPluginManager().registerEvents(new ChatListener(this, prefix), this);
            this.getServer().getPluginManager().registerEvents(new ConsoleListener(this, prefix), this);
        }
    }

    @Override
    public void onDisable() {
        this.api = null;
    }

    public NestAPIBukkit getAPI() {
        return this.api;
    }
    
    public boolean isDebugMode() {
        return this.debug;
    }

}
