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
package jp.llv.nest.bukkit.listener;

import jp.llv.nest.NestBukkitPluginImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author toyblocks
 */
public class ConsoleListener implements Listener {
    
    private final NestBukkitPluginImpl plugin;
    private final String prefix;

    public ConsoleListener(NestBukkitPluginImpl plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(ServerCommandEvent eve) {
        if (eve.getCommand().startsWith(prefix)) {
            this.plugin.getAPI().executeNow(eve.getSender(), eve.getCommand().replaceFirst(prefix, ""));
            eve.setCancelled(true);
        }
    }
}
