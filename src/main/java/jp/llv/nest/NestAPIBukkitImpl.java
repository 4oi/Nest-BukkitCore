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

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.llv.nest.command.exceptions.CommandException;
import jp.llv.nest.command.exceptions.InternalException;
import jp.llv.nest.command.obj.NestObject;
import jp.llv.nest.command.obj.bukkit.BukkitCommandSender;
import jp.llv.nest.command.obj.bukkit.BukkitConsole;
import jp.llv.nest.command.obj.bukkit.BukkitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
public class NestAPIBukkitImpl extends NestAPIImpl implements NestAPIBukkit {
    
    private final NestPlugin plugin;

    public NestAPIBukkitImpl(NestPlugin plugin, boolean async) {
        super(async);
        this.plugin = plugin;
    }
    
    @Override
    public String getVersion () {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public void executeNow(CommandSender sender, String command) {
        BukkitCommandSender<?> s;
        if (sender instanceof Player) {
            s = new BukkitPlayer((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            s = BukkitConsole.getInstance();
        } else {
            s = new BukkitCommandSender<>(sender);
        }
        try {
            NestObject<?> res = this.execute(s, command).join();
            sender.sendMessage(ChatColor.GRAY+"-> "+Objects.toString(res));
        } catch(CommandException ex) {
            sender.sendMessage(ChatColor.RED+ex.getMessage());
            if (this.plugin.isDebugMode() || ex instanceof InternalException) {
                this.plugin.getLogger().log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
}
