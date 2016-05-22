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

import jp.llv.nest.command.obj.NestValueAdapter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author toyblocks
 */
public class BukkitLocation extends NestValueAdapter<Location> {
    
    private Location value;
    
    public BukkitLocation(Location e) {
        super(e);
    }

    public void setWorld(World world) {
        value.setWorld(world);
    }

    public World getWorld() {
        return value.getWorld();
    }

    public Block getBlock() {
        return value.getBlock();
    }

    public void setX(double x) {
        value.setX(x);
    }

    public double getX() {
        return value.getX();
    }

    public int getBlockX() {
        return value.getBlockX();
    }

    public void setY(double y) {
        value.setY(y);
    }

    public double getY() {
        return value.getY();
    }

    public int getBlockY() {
        return value.getBlockY();
    }

    public void setZ(double z) {
        value.setZ(z);
    }

    public double getZ() {
        return value.getZ();
    }

    public int getBlockZ() {
        return value.getBlockZ();
    }

    public void setYaw(float yaw) {
        value.setYaw(yaw);
    }

    public float getYaw() {
        return value.getYaw();
    }

    public void setPitch(float pitch) {
        value.setPitch(pitch);
    }

    public float getPitch() {
        return value.getPitch();
    }

    public Location add(BukkitLocation vec) {
        return value.add(vec.value);
    }

    public Location add(double x, double y, double z) {
        return value.add(x, y, z);
    }

    public Location subtract(BukkitLocation vec) {
        return value.subtract(vec.value);
    }

    public Location subtract(double x, double y, double z) {
        return value.subtract(x, y, z);
    }

    public double length() {
        return value.length();
    }

    public double lengthSquared() {
        return value.lengthSquared();
    }

    public double distance(BukkitLocation o) {
        return value.distance(o.value);
    }

    public double distanceSquared(BukkitLocation o) {
        return value.distanceSquared(o.value);
    }

    public Location multiply(double m) {
        return value.multiply(m);
    }

    @Override
    public String toString() {
        return value.toString();
    }
    
}
