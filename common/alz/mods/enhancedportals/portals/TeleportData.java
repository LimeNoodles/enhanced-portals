package alz.mods.enhancedportals.portals;

import alz.mods.enhancedportals.helpers.EntityHelper;
import alz.mods.enhancedportals.helpers.WorldHelper;
import net.minecraft.server.MinecraftServer;

public class TeleportData
{
	private double x, y, z;
	private boolean linksToModifier;
	private int dimension;

	private int[] blockOffsetLocation;
	private double[] entityOffsetLocation;

	public TeleportData(int x, int y, int z, int dimension, boolean modifier)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		linksToModifier = modifier;
	}
	
	public TeleportData(int x, int y, int z, int dimension)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		linksToModifier = false;
	}

	public TeleportData(double x, double y, double z, int dimension)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		linksToModifier = false;
	}

	public boolean GetLinksToModifier()
	{
		return linksToModifier;
	}
	
	public void SetLinksToModifier(boolean state)
	{
		linksToModifier = state;
	}
	
	public int GetX()
	{
		return (int) Math.floor(x);
	}

	public int GetY()
	{
		return (int) Math.floor(y);
	}

	public int GetZ()
	{
		return (int) Math.floor(z);
	}

	private void setupEntityOffset()
	{
		if (entityOffsetLocation == null)
		{
			entityOffsetLocation = EntityHelper.offsetDirectionBased(MinecraftServer.getServer().worldServerForDimension(dimension), GetX(), GetY(), GetZ());
		}
	}

	private void setupBlockOffset()
	{
		if (blockOffsetLocation == null)
		{
			blockOffsetLocation = WorldHelper.offsetDirectionBased(MinecraftServer.getServer().worldServerForDimension(dimension), GetX(), GetY(), GetZ());
		}
	}

	public int GetXOffsetBlock()
	{
		setupBlockOffset();

		return blockOffsetLocation[0];
	}

	public int GetYOffsetBlock()
	{
		setupBlockOffset();

		return blockOffsetLocation[1];
	}

	public int GetZOffsetBlock()
	{
		setupBlockOffset();

		return blockOffsetLocation[2];
	}

	public double GetXOffsetEntity()
	{
		setupEntityOffset();

		return entityOffsetLocation[0];
	}

	public double GetYOffsetEntity()
	{
		setupEntityOffset();

		return entityOffsetLocation[1];
	}

	public double GetZOffsetEntity()
	{
		setupEntityOffset();

		return entityOffsetLocation[2];
	}

	public int GetDimension()
	{
		return dimension;
	}
	
	public String GetDimensionAsString()
	{
		switch (dimension)
		{
			case -1:
				return "The Nether";
			case 0:
				return "Overworld";
			case 1:
				return "The End";
			default:
				return "Unknown";
		}
	}
}