/* 
 * Copyright 2016 toyblocks All rights reserved.
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
