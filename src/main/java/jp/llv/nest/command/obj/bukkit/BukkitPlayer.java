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
import jp.llv.nest.command.exceptions.TypeMismatchException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author toyblocks
 */
@Type("Player")
public class BukkitPlayer extends BukkitCommandSender<Player> {

    public BukkitPlayer(Player sender) {
        super(sender);
    }

    public BukkitPlayer(String name) throws TypeMismatchException {
        this(getPlayer(name));
    }

    @Override
    public <T> T to(Class<T> toClass) throws TypeMismatchException {
        return super.to(toClass, ifClass(BukkitLocation.class, () -> new BukkitLocation(super.value.getLocation())));
    }

    private static Player getPlayer(String name) throws TypeMismatchException {
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            throw new TypeMismatchException("Unknown player");
        }
        return p;
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.of(super.value.getLocation());
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
