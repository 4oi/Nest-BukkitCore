/* 
 * Copyright 2016 toyblocks All rights reserved.
 */
package jp.llv.nest;

import com.google.inject.AbstractModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jp.llv.nest.command.AsyncCommandExecutor;
import jp.llv.nest.command.CommandExecutor;
import jp.llv.nest.command.SyncCommandExecutor;
import jp.llv.nest.command.exceptions.CommandException;
import jp.llv.nest.command.obj.NestPermitter;
import jp.llv.nest.command.obj.bukkit.BukkitConsole;
import jp.llv.nest.bukkit.listener.ChatListener;
import jp.llv.nest.bukkit.listener.CommandListener;
import jp.llv.nest.bukkit.listener.ConsoleListener;
import jp.llv.nest.i18n.ConfigMessageProvider;
import jp.llv.nest.i18n.I18n;
import jp.llv.nest.module.ConfigManagerImpl;
import jp.llv.nest.module.DynInjector;
import jp.llv.nest.module.JarModuleManager;
import jp.llv.nest.module.ModuleManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author toyblocks
 */
public class NestBukkitPluginImpl extends JavaPlugin implements NestBukkitPlugin {

    private String prefix = ":";
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
        try {
            I18n.setProvider("nest", new ConfigMessageProvider(new ConfigManagerImpl(this.getDataFolder(), this.getClass()).getConfig("messages.conf")));
        } catch (IOException ex) {
            this.getLogger().warning("Failed to load messages configuration!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.modules = new JarModuleManager(this.api, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Server.class).toInstance(NestBukkitPluginImpl.this.getServer());
                bind(PluginManager.class).toInstance(NestBukkitPluginImpl.this.getServer().getPluginManager());
                bind(NestBukkitPlugin.class).toInstance(NestBukkitPluginImpl.this);
            }
        });

        this.saveResource("config.st", false);

        this.getServer().getScheduler().runTaskLater(this, this::init, 1L);
    }

    @Override
    public void onDisable() {
        this.api = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.api.executeNow(sender, String.join(" ", args));
        return true;
    }

    public NestAPIBukkit getAPI() {
        return this.api;
    }

    public boolean isDebugMode() {
        return this.debug;
    }

    public void init() {
        this.modules.getInjector().createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Arrays.stream(NestBukkitPluginImpl.this.getServer().getPluginManager().getPlugins())
                        .filter(p -> DynInjector.isSafe(p.getClass()))
                        .forEach(p -> bind((Class) p.getClass()).toProvider(() -> p));
            }
        }, new AbstractModule() {
            @Override
            protected void configure() {
                NestBukkitPluginImpl.this.getServer().getServicesManager().getKnownServices().stream()
                        .filter(DynInjector::isSafe)
                        .forEach(sc -> bind((Class) sc).toProvider(() -> NestBukkitPluginImpl.this.getServer().getServicesManager().getRegistration(sc).getProvider()));
            }
        });

        for (File f : this.getDataFolder().listFiles()) {
            if (!f.getName().endsWith(".jar")) {
                continue;
            }
            try {
                this.modules.load(f.toPath());
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, "Failed to load module \"" + f.getName() + "\"", ex);
            }
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
