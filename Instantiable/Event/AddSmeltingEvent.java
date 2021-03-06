/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class AddSmeltingEvent extends Event {

	private final ItemStack input;
	private final ItemStack output;

	public final float originalXP;
	public float experienceValue;

	private boolean isInvalid = false;

	public static boolean isVanillaPass = false;

	public AddSmeltingEvent(ItemStack in, ItemStack out, float xp) {
		this(in, out, xp, isVanillaPass);
	}

	public AddSmeltingEvent(ItemStack in, ItemStack out, float xp, boolean v) {
		input = in;
		output = out;

		originalXP = xp;
		experienceValue = xp;

		isVanillaPass = v;
	}

	public ItemStack getInput() {
		return input.copy();
	}

	public ItemStack getOutput() {
		return output.copy();
	}

	public void markInvalid() {
		isInvalid = true;
		this.setCanceled(true);
	}

	@Override
	public void setCanceled(boolean cancel) {
		if (isInvalid)
			cancel = true;
		super.setCanceled(cancel);
	}

	@Override
	public boolean isCanceled() {
		return super.isCanceled() || isInvalid;
	}

	public boolean isValid() {
		return !isInvalid;
	}

	/** Returns true if recipe was added. */
	public static boolean fire(ItemStack in, ItemStack out, float xp) {
		if (in == null || out == null)
			throw new IllegalArgumentException("You cannot add null to smelting recipes!");
		AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
		return !MinecraftForge.EVENT_BUS.post(evt);
	}

}
