/* 
 * Copyright 2016 toyblocks All rights reserved.
 */
package jp.llv.nest.bukkit.listener;

import jp.llv.nest.NestBukkitPluginImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author toyblocks
 */
public class CommandListener implements Listener {
    
    private final NestBukkitPluginImpl plugin;
    private final String prefix;

    public CommandListener(NestBukkitPluginImpl plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerCommandPreprocessEvent eve) {
        if (eve.getMessage().startsWith(prefix)) {
            this.plugin.getAPI().executeNow(eve.getPlayer(), eve.getMessage().replaceFirst(prefix, ""));
            eve.setCancelled(true);
        }
    }
     
}
