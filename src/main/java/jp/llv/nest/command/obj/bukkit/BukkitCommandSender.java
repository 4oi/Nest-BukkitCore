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
package jp.llv.nest.command.obj.bukkit;

import java.util.Optional;
import jp.llv.nest.command.Type;
import jp.llv.nest.command.obj.NestCommandSender;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 *
 * @author toyblocks
 */
@Type("BukkitUser")
public abstract class BukkitCommandSender<E extends CommandSender> extends NestCommandSender<E> {

    public static String PERM_PREFIX = "nest.";
    
    public BukkitCommandSender(E sender) {
        super(sender);
    }
    
    @Override
    public void sendMessage(String name) {
        super.value.sendMessage(name);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.value.hasPermission(PERM_PREFIX+permission);
    }
    
    public String getName() {
        return super.value.getName();
    }
    
    public abstract Optional<Location> getLocation();
    
}
