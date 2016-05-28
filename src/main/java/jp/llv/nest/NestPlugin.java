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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jp.llv.nest.command.AsyncCommandExecutor;
import jp.llv.nest.command.CommandExecutor;
import jp.llv.nest.command.SyncCommandExecutor;
import jp.llv.nest.command.exceptions.CommandException;
import jp.llv.nest.command.obj.NestPermitter;
import jp.llv.nest.command.obj.bukkit.BukkitConsole;
import jp.llv.nest.listener.ChatListener;
import jp.llv.nest.listener.CommandListener;
import jp.llv.nest.listener.ConsoleListener;
import jp.llv.nest.module.DependencyException;
import jp.llv.nest.module.InvalidModuleException;
import jp.llv.nest.module.ModuleManager;
import jp.llv.nest.module.SimpleModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author toyblocks
 */
public class NestPlugin extends JavaPlugin {

    private String prefix = "/n:";
    private boolean debug = false;
    private CommandExecutor executor = AsyncCommandExecutor.getInstance();
    
    private NestAPIBukkitImpl api;
    private ModuleManager modules;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Configuration config = this.getConfig();
        this.prefix = config.getString("prefix", this.prefix);
        this.debug = config.getBoolean("debug", this.debug);
        this.executor = config.getString("executor", "async").equalsIgnoreCase("async") ? AsyncCommandExecutor.getInstance() : SyncCommandExecutor.getInstance();
        
        this.api = new NestAPIBukkitImpl(this, this.executor, this.debug);

        this.modules = new SimpleModuleManager(this.api);
        this.modules.setDependable(this.api);
        this.modules.setDependable(Bukkit.getServer());
        this.modules.setDependable(this);

        this.saveResource("config.st", false);
        
        this.getServer().getScheduler().runTaskLater(this, this::init, 1L);
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
    
    public void init() {
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            this.modules.setDependable(plugin);
        }
        for (Class<?> serviceClass : this.getServer().getServicesManager().getKnownServices()) {
            this.modules.setDependable(this.getServer().getServicesManager().getRegistration(serviceClass).getProvider());
        }
        
        try {
            this.modules.load(this.getDataFolder().listFiles());
        } catch (IOException | InvalidModuleException | DependencyException ex) {
            this.getLogger().log(Level.WARNING, "Failed to load modules", ex);
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(new File(this.getDataFolder(), "config.st")), "UTF-8"
                ))) {
            this.api.execute(NestPermitter.SUPERUSER, BukkitConsole.getInstance(), br.lines().collect(Collectors.joining("\n")))
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

}
