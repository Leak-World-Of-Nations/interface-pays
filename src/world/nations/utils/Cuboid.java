package world.nations.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {
	protected final String worldName;
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;
			
	public Cuboid(Location l1, Location l2) {
		if (! l1.getWorld().equals(l2.getWorld())) {
			throw new IllegalArgumentException("locations must be on the same world");
		}
		worldName = l1.getWorld().getName();
		x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
	}
	
	public Cuboid(Location l1) {
		this(l1, l1);
	}
	
	public Cuboid(Cuboid other) {
		this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
	}
	
	public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.worldName = world.getName();
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.z2 = Math.max(z1, z2);
	}
	private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.worldName = worldName;
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.z2 = Math.max(z1, z2);
	}

	public Cuboid(Map<String, Object> map) {
		worldName = (String) map.get("worldName");
		x1 = (Integer) map.get("x1");
		x2 = (Integer) map.get("x2");
		y1 = (Integer) map.get("y1");
		y2 = (Integer) map.get("y2");
		z1 = (Integer) map.get("z1");
		z2 = (Integer) map.get("z2");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("worldName", worldName);
		map.put("x1", x1);
		map.put("y1", y1);
		map.put("z1", z1);
		map.put("x2", x2);
		map.put("y2", y2);
		map.put("z2", z2);
		return map;
	}

	public List<Block> getBlocks() {
		Iterator<Block> blockI = this.iterator();
		List<Block> copy = new ArrayList<Block>();
		while (blockI.hasNext())
			copy.add(blockI.next());
		return copy;
	}
	
	public Location getLowerNE() {
		return new Location(getWorld(), x1, y1, z1);
	}
	
	public Location getUpperSW() {
		return new Location(getWorld(), x2, y2, z2);
	}

	public Location getCenter() {
		int x1 = getUpperX() + 1;
		int y1 = getUpperY() + 1;
		int z1 = getUpperZ() + 1;
		return new Location(getWorld(), getLowerX() + (x1 - getLowerX()) / 2.0,
		                    getLowerY() + (y1 - getLowerY()) / 2.0,
		                    getLowerZ() + (z1 - getLowerZ()) / 2.0);
	}
	
	public World getWorld() {
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			throw new IllegalStateException("world '" + worldName + "' is not loaded");
		}
		return world;
	}
	
	public int getSizeX() {
		return (x2 - x1) + 1;
	}
	
	public int getSizeY() {
		return (y2 - y1) + 1;
	}

	public int getSizeZ() {
		return (z2 - z1) + 1;
	}
	
	public int getLowerX() {
		return x1;
	}

	
	public int getLowerY() {
		return y1;
	}
	
	public int getLowerZ() {
		return z1;
	}
	
	public int getUpperX() {
		return x2;
	}
	
	public int getUpperY() {
		return y2;
	}
	
	public int getUpperZ() {
		return z2;
	}
	
	public Block[] corners() {
		Block[] res = new Block[8];
		World w = getWorld();
		res[0] = w.getBlockAt(x1, y1, z1);
		res[1] = w.getBlockAt(x1, y1, z2);
		res[2] = w.getBlockAt(x1, y2, z1);
		res[3] = w.getBlockAt(x1, y2, z2);
		res[4] = w.getBlockAt(x2, y1, z1);
		res[5] = w.getBlockAt(x2, y1, z2);
		res[6] = w.getBlockAt(x2, y2, z1);
		res[7] = w.getBlockAt(x2, y2, z2);
		return res;
	}
	
	public Cuboid expand(CuboidDirection dir, int amount) {
		switch (dir) {
		case North:
			return new Cuboid(worldName, x1 - amount, y1, z1, x2, y2, z2);
		case South:
			return new Cuboid(worldName, x1, y1, z1, x2 + amount, y2, z2);
		case East:
			return new Cuboid(worldName, x1, y1, z1 - amount, x2, y2, z2);
		case West:
			return new Cuboid(worldName, x1, y1, z1, x2, y2, z2 + amount);
		case Down:
			return new Cuboid(worldName, x1, y1 - amount, z1, x2, y2, z2);
		case Up:
			return new Cuboid(worldName, x1, y1, z1, x2, y2 + amount, z2);
		default:
			throw new IllegalArgumentException("invalid direction " + dir);
		}
	}
	
	public Cuboid shift(CuboidDirection dir, int amount) {
		return expand(dir, amount).expand(dir.opposite(), -amount);
	}
	
	public Cuboid outset(CuboidDirection dir, int amount) {
		Cuboid c;
		switch (dir) {
		case Horizontal:
			c = expand(CuboidDirection.North, amount).expand(CuboidDirection.South, amount).expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount);
			break;
		case Vertical:
			c = expand(CuboidDirection.Down, amount).expand(CuboidDirection.Up, amount);
			break;
		case Both:
			c = outset(CuboidDirection.Horizontal, amount).outset(CuboidDirection.Vertical, amount);
			break;
		default:
			throw new IllegalArgumentException("invalid direction " + dir);
		}
		return c;
	}
	
	public Cuboid inset(CuboidDirection dir, int amount) {
		return outset(dir, -amount);
	}

	public boolean contains(int x, int y, int z) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
	}
	
	public boolean contains(Block b) {
		return contains(b.getLocation());
	}
	
	public boolean contains(Location l) {
		return worldName.equals(l.getWorld().getName()) && contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public int volume() {
		return getSizeX() * getSizeY() * getSizeZ();
	}
	
	public byte averageLightLevel() {
		long total = 0;
		int n = 0;
		for (Block b : this) {
			if (b.isEmpty()) {
				total += b.getLightLevel();
				++n;
			}
		}
		return n > 0 ? (byte) (total / n) : 0;
	}

	public Cuboid contract() {
		return this.
				contract(CuboidDirection.Down).
				contract(CuboidDirection.South).
				contract(CuboidDirection.East).
				contract(CuboidDirection.Up).
				contract(CuboidDirection.North).
				contract(CuboidDirection.West);
	}
	
	public Cuboid contract(CuboidDirection dir) {
		Cuboid face = getFace(dir.opposite());
		switch (dir) {
		case Down:
			while (face.containsOnly(Material.AIR) && face.getLowerY() > this.getLowerY()) {
				face = face.shift(CuboidDirection.Down, 1);
			}
			return new Cuboid(worldName, x1, y1, z1, x2, face.getUpperY(), z2);
		case Up:
			while (face.containsOnly(Material.AIR) && face.getUpperY() < this.getUpperY()) {
				face = face.shift(CuboidDirection.Up, 1);
			}
			return new Cuboid(worldName, x1, face.getLowerY(), z1, x2, y2, z2);
		case North:
			while (face.containsOnly(Material.AIR) && face.getLowerX() > this.getLowerX()) {
				face = face.shift(CuboidDirection.North, 1);
			}
			return new Cuboid(worldName, x1, y1, z1, face.getUpperX(), y2, z2);
		case South:
			while (face.containsOnly(Material.AIR) && face.getUpperX() < this.getUpperX()) {
				face = face.shift(CuboidDirection.South, 1);
			}
			return new Cuboid(worldName, face.getLowerX(), y1, z1, x2, y2, z2);
		case East:
			while (face.containsOnly(Material.AIR) && face.getLowerZ() > this.getLowerZ()) {
				face = face.shift(CuboidDirection.East, 1);
			}
			return new Cuboid(worldName, x1, y1, z1, x2, y2, face.getUpperZ());
		case West:
			while (face.containsOnly(Material.AIR) && face.getUpperZ() < this.getUpperZ()) {
				face = face.shift(CuboidDirection.West, 1);
			}
			return new Cuboid(worldName, x1, y1, face.getLowerZ(), x2, y2, z2);
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}
	
	public Cuboid getFace(CuboidDirection dir	) {
		switch (dir) {
		case Down:
			return new Cuboid(worldName, x1, y1, z1, x2, y1, z2);
		case Up:
			return new Cuboid(worldName, x1, y2, z1, x2, y2, z2);
		case North:
			return new Cuboid(worldName, x1, y1, z1, x1, y2, z2);
		case South:
			return new Cuboid(worldName, x2, y1, z1, x2, y2, z2);
		case East:
			return new Cuboid(worldName, x1, y1, z1, x2, y2, z1);
		case West:
			return new Cuboid(worldName, x1, y1, z2, x2, y2, z2);
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}
	
	public boolean containsOnly(Material material) {
		for (Block b : this) {
			if (b.getType() != material) {
				return false;
			}
		}
		return true;
	}
	
	public Cuboid getBoundingCuboid(Cuboid other) {
		if (other == null) {
			return this;
		}

		int xMin = Math.min(getLowerX(), other.getLowerX());
		int yMin = Math.min(getLowerY(), other.getLowerY());
		int zMin = Math.min(getLowerZ(), other.getLowerZ());
		int xMax = Math.max(getUpperX(), other.getUpperX());
		int yMax = Math.max(getUpperY(), other.getUpperY());
		int zMax = Math.max(getUpperZ(), other.getUpperZ());

		return new Cuboid(worldName, xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	public Block getRelativeBlock(int x, int y, int z) {
		return getWorld().getBlockAt(x1 + x, y1 + y, z1 + z);
	}
	
	public Block getRelativeBlock(World w, int x, int y, int z) {
		return w.getBlockAt(x1 + x, y1 + y, z1 + z);
	}
	
	public List<Chunk> getChunks() {
		List<Chunk> res = new ArrayList<Chunk>();

		World w = getWorld();
		int x1 = getLowerX() & ~0xf; int x2 = getUpperX() & ~0xf;
		int z1 = getLowerZ() & ~0xf; int z2 = getUpperZ() & ~0xf;
		for (int x = x1; x <= x2; x += 16) {
			for (int z = z1; z <= z2; z += 16) {
				res.add(w.getChunkAt(x >> 4, z >> 4));
			}
		}
		return res;
	}
	
	
	public Iterator<Block> iterator() {
		return new CuboidIterator(getWorld(), x1, y1, z1, x2, y2, z2);
	}
	
    @Override
	public Cuboid clone() throws CloneNotSupportedException {
		return new Cuboid(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Cuboid: " + worldName + "," + x1 + "," + y1 + "," + z1 + "=>" + x2 + "," + y2 + "," + z2;
	}

	public class CuboidIterator implements Iterator<Block> {
		private World w;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.w = w;
			baseX = x1;
			baseY = y1;
			baseZ = z1;
			sizeX = Math.abs(x2 - x1) + 1;
			sizeY = Math.abs(y2 - y1) + 1;
			sizeZ = Math.abs(z2 - z1) + 1;
			x = y = z = 0;
		}

		public boolean hasNext() {
			return x < sizeX && y < sizeY && z < sizeZ;
		}

		public Block next() {
			Block b = w.getBlockAt(baseX + x, baseY + y, baseZ + z);
			if (++x >= sizeX) {
				x = 0;
				if (++y >= sizeY) {
					y = 0;
					++z;
				}
			}
			return b;
		}

		public void remove() {
			// nop
		}
	}

	public enum CuboidDirection {

		North, East, South, West, Up, Down, Horizontal, Vertical, Both, Unknown;

		public CuboidDirection opposite() {
			switch(this) {
			case North:
				return South;
			case East:
				return West;
			case South:
				return North;
			case West:
				return East;
			case Horizontal:
				return Vertical;
			case Vertical:
				return Horizontal;
			case Up:
				return Down;
			case Down:
				return Up;
			case Both:
				return Both;
			default:
				return Unknown;
			}
		}
	}

}
