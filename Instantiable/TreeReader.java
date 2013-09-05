/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public class TreeReader extends BlockArray {

	private int leafCount;
	private int logCount;

	private ReikaTreeHelper vanilla;
	private ModWoodList wood;

	public void addTree(World world, int x, int y, int z, int blockID, int blockMeta) {
		int id = world.getBlockId(x, y, z);
		if (id == 0)
			return;
		int meta = world.getBlockMetadata(x, y, z);
		if (id != blockID)
			return;
		if (meta != blockMeta)
			return;
		if (id != Block.wood.blockID && !ModWoodList.isModWood(new ItemStack(id, 1, meta)))
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);

		try {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						if (i != 0 || j != 0 || k != 0)
							this.addTree(world, x+i, y+j, z+k, blockID, blockMeta);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow();
			e.printStackTrace();
		}
	}

	public void addModTree(World world, int x, int y, int z) {
		if (wood == ModWoodList.SEQUOIA) {
			ReikaJavaLibrary.pConsole("Use sequoia handler!");
			Thread.dumpStack();
			return;
		}
		if (wood == ModWoodList.DARKWOOD) {
			ReikaJavaLibrary.pConsole("Use darkwood handler!");
			Thread.dumpStack();
			return;
		}
		if (wood == null) {
			throw new MisuseException("You must set the mod tree type!");
		}
		if (this.hasBlock(x, y, z))
			return;
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ModWoodList get = ModWoodList.getModWood(id, meta);
		ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

		//ReikaJavaLibrary.pConsole("GET: "+get+"   WOOD: "+wood+"    LEAF: "+leaf);

		if (get != wood && leaf != wood)
			return;

		this.addBlockCoordinate(x, y, z);
		if (get == wood)
			logCount++;
		else if (leaf == wood)
			leafCount++;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					this.addModTree(world, x+i, y+j, z+k);
				}
			}
		}
	}

	public void addTree(World world, int x, int y, int z) {
		if (vanilla == null) {
			throw new MisuseException("You must set the tree type!");
		}
		if (this.hasBlock(x, y, z))
			return;
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaTreeHelper get = ReikaTreeHelper.getTree(id, meta);
		ReikaTreeHelper leaf = ReikaTreeHelper.getTreeFromLeaf(id, meta);
		if (get != vanilla && leaf != vanilla)
			return;

		//ReikaJavaLibrary.pConsoleSideOnly("GET: "+get+"     LEAF: "+leaf+"    ## LOG: "+logCount+"    LEAVES: "+leafCount, Side.SERVER);

		this.addBlockCoordinate(x, y, z);
		if (get == vanilla)
			logCount++;
		else if (leaf == vanilla)
			leafCount++;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					this.addTree(world, x+i, y+j, z+k);
				}
			}
		}
	}

	/** For natura's massive redwood trees. Warning: may lag-spike! *//*
	public void addSequoia(World world, int x, int y, int z, boolean debug) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ModWoodList wood = ModWoodList.getModWood(id, meta);
		if (wood == ModWoodList.SEQUOIA) {
			this.addBlockCoordinate(x, y, z);
			if (debug) {
				ReikaJavaLibrary.pConsole("Adding "+x+", "+y+", "+z);
			}
			try {
				for (int i = -20; i <= 20; i++) {
					for (int j = 0; j < world.provider.getHeight(); j++) { //full height
						for (int k = -20; k <= 20; k++) {
							if (!this.hasBlock(x+i, y+j, z+k)) {
								int idread = world.getBlockId(x, y, z);
								int metaread = world.getBlockMetadata(x, y, z);
								ItemStack blockread = new ItemStack(idread, 1, metaread);
								ModWoodList woodread = ModWoodList.getModWood(blockread);
								if (woodread == ModWoodList.SEQUOIA) {
									this.addBlockCoordinate(x+i, j, z+k);
									if (debug) {
										ReikaJavaLibrary.pConsole("Adding "+(x+i)+", "+y+", "+(z+k));
									}
								}
								else if (ReikaItemHelper.matchStacks(ModWoodList.SEQUOIA.getCorrespondingLeaf(), blockread)) {
									this.addBlockCoordinate(x+i, j, z+k);
									if (debug) {
										ReikaJavaLibrary.pConsole("Adding "+(x+i)+", "+y+", "+(z+k));
									}
								}
							}
						}
					}
				}
			}
			catch (StackOverflowError e) {
				this.throwOverflow();
				e.printStackTrace();
			}
		}
	}*/

	/** For Natura's massive redwood trees. Warning: may lag-spike! */
	public void addSequoia(World world, int x, int y, int z, boolean debug) {
		this.setModTree(ModWoodList.SEQUOIA);
		int r = 24;
		int minx = x-r;
		int maxx = x+r;
		int minz = z-r;
		int maxz = z+r;

		int yr = 16;

		for (int j = y; j <= y+yr; j++) {
			for (int i = minx; i <= maxx; i++) {
				for (int k = minz; k <= maxz; k++) {
					int id = world.getBlockId(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					ModWoodList get = ModWoodList.getModWood(id, meta);
					ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

					if (get == ModWoodList.SEQUOIA) {
						logCount++;
						this.addBlockCoordinate(i, j, k);
					}
					else if (leaf == ModWoodList.SEQUOIA) {
						leafCount++;
						this.addBlockCoordinate(i, j, k);
					}
				}
			}
		}
	}

	/** For Twilight's dark forests. */
	public void addDarkForest(World world, int x, int y, int z, int minx, int maxx, int minz, int maxz, boolean debug) {
		this.setModTree(ModWoodList.DARKWOOD);
		for (int j = y; j <= y+16; j++) {
			for (int i = minx; i <= maxx; i++) {
				for (int k = minz; k <= maxz; k++) {
					int id = world.getBlockId(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					ModWoodList get = ModWoodList.getModWood(id, meta);
					ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

					if (get == ModWoodList.DARKWOOD) {
						logCount++;
						this.addBlockCoordinate(i, j, k);
					}
					else if (leaf == ModWoodList.DARKWOOD) {
						leafCount++;
						this.addBlockCoordinate(i, j, k);
					}
				}
			}
		}
	}

	public void addGenerousTree(World world, int x, int y, int z, int dw) {
		if (this.hasBlock(x, y, z))
			return;
		try {
			int id = world.getBlockId(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			Material mat = world.getBlockMaterial(x, y, z);
			ModWoodList wood = ModWoodList.getModWood(id, meta);
			if (wood == ModWoodList.SEQUOIA) {
				ReikaJavaLibrary.pConsole("Use sequoia handler for "+id+":"+meta+"!");
				Thread.dumpStack();
				return;
			}
			//ItemStack leaf = wood.getCorrespondingLeaf();
			if (id == Block.wood.blockID || wood != null) {
				this.addBlockCoordinate(x, y, z);
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if (!this.hasBlock(x+i, y+j, z+k)) {
								Material read = world.getBlockMaterial(x+i, y+j, z+k);
								int readid = world.getBlockId(x+i, y+j, z+k);
								int readmeta = world.getBlockMetadata(x+i, y+j, z+k);
								if (read == Material.leaves) {
									int leafID = readid;
									int leafMeta = readmeta;
									ModWoodList leaf = ModWoodList.getModWoodFromLeaf(leafID, leafMeta);
									if (leaf == wood)
										//this.recursiveAddWithBoundsMetadata(world, x+i, y+j, z+k, leafID, leafMeta, x-dw, 0, z-dw, x+dw, 256, z+dw);
										this.addGenerousTree(world, x+i, y+j, z+k, dw);
									else {
										ReikaTreeHelper tree = ReikaTreeHelper.getTreeFromLeaf(leafID, leafMeta);
										if (tree != null) {
											this.addGenerousTree(world, x+i, y+j, z+k, dw);
										}
									}
								}
								else if (readid == Block.wood.blockID || ModWoodList.getModWood(readid, readmeta) == wood)
									this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
			}
			else if (mat == Material.leaves) {
				ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);
				ReikaTreeHelper tree = ReikaTreeHelper.getTreeFromLeaf(id, meta);
				if (leaf != null) {
					this.addBlockCoordinate(x, y, z);
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							for (int k = -1; k <= 1; k++) {
								this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
				else if (tree != null) {
					this.addBlockCoordinate(x, y, z);
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							for (int k = -1; k <= 1; k++) {
								this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow();
			e.printStackTrace();
		}
	}

	public int getNumberLeaves() {
		return leafCount;
	}

	public int getNumberLogs() {
		return logCount;
	}

	public void reset() {
		logCount = 0;
		leafCount = 0;
		vanilla = null;
		wood = null;
	}

	public void setTree(ReikaTreeHelper tree) {
		vanilla = tree;
	}

	public void setModTree(ModWoodList tree) {
		wood = tree;
	}

	@Override
	public int[] getNextAndMoveOn() {
		int[] next = super.getNextAndMoveOn();
		if (this.isEmpty())
			this.reset();
		return next;
	}

	public boolean isValidTree() {
		if (wood == ModWoodList.SEQUOIA)
			return true;
		return this.getNumberLeaves() >= ReikaTreeHelper.TREE_MIN_LEAF && this.getNumberLogs() >= ReikaTreeHelper.TREE_MIN_LOG;
	}

	public ReikaTreeHelper getVanillaTree() {
		return vanilla;
	}

	public ModWoodList getModTree() {
		return wood;
	}

	public boolean isModTree() {
		return this.getModTree() != null;
	}

	public boolean isVanillaTree() {
		return this.getVanillaTree() != null;
	}

}
