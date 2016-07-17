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

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.logging.Level;
import jp.llv.nest.command.CommandExecutor;
import jp.llv.nest.command.exceptions.CommandException;
import jp.llv.nest.command.exceptions.InternalException;
import jp.llv.nest.command.obj.NestObject;
import jp.llv.nest.command.obj.bukkit.BukkitCommandBlock;
import jp.llv.nest.command.obj.bukkit.BukkitCommandBlockMinecart;
import jp.llv.nest.command.obj.bukkit.BukkitCommandSender;
import jp.llv.nest.command.obj.bukkit.BukkitConsole;
import jp.llv.nest.command.obj.bukkit.BukkitPlayer;
import jp.llv.nest.command.token.CommandTokenizer;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.slf4j.Logger;

/**
 *
 * @author toyblocks
 */
public class NestAPIBukkitImpl extends NestAPIImpl implements NestAPIBukkit {

    private final NestBukkitPluginImpl plugin;
    private Logger logger;
    private final boolean debug;

    public NestAPIBukkitImpl(NestBukkitPluginImpl plugin, CommandExecutor executor, boolean debug) {
        super(new CommandTokenizer(), executor);
        this.plugin = plugin;
        this.debug = debug;
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        try {
            Constructor<? extends Logger> constructor = org.slf4j.impl.JDK14LoggerAdapter.class
                    .getDeclaredConstructor(java.util.logging.Logger.class);
            constructor.setAccessible(true);
            return (this.logger = constructor.newInstance(this.plugin.getLogger()));
        } catch (ReflectiveOperationException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to adapt logger", ex);
            return null;
        }
    }

    @Override
    public Path getDataFolder() {
        return this.plugin.getDataFolder().toPath();
    }

    @Override
    public void executeNow(CommandSender sender, String command) {
        BukkitCommandSender<?> s;
        if (sender instanceof Player) {
            s = new BukkitPlayer((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            s = BukkitConsole.getInstance();
        } else if (sender instanceof BlockCommandSender) {
            s = new BukkitCommandBlock((BlockCommandSender) sender);
        } else if (sender instanceof CommandMinecart) {
            s = new BukkitCommandBlockMinecart((CommandMinecart) sender);
        } else {
            throw new IllegalArgumentException("Unsupported sender type");
        }
        try {
            this.plugin.getLogger().log(Level.INFO, "{0} has executed command ''{1}''", new Object[]{sender.getName(), command});
            NestObject<?> res = this.executeNow(s, s, command);
            if (this.debug) {
                sender.sendMessage(ChatColor.GRAY + "-> " + (res == null ? "nil" : res.toString()));
            }
        } catch (InternalException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            this.plugin.getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            if (this.debug) {
                this.plugin.getLogger().log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

}
