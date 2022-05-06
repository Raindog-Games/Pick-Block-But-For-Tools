package ca.raindoggames.quickpicktool.inventory;

import static ca.raindoggames.quickpicktool.QuickPickToolMod.LOGGER;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;

public class PlayerInventoryHelper {
	
	public PlayerInventoryHelper() {}
	
	private static int WOOD = 0;
	private static int STONE = 1;
	private static int GOLD = 2;
	private static int IRON = 3;
	private static int DIAMOND = 4;
	private static int NETHERITE = 5;
	
	// Weight which tool should be returned
	// 1. highest level [nether, diamond, iron, gold, stone, wood]
	// 2. named
	// 3. most enchantments
	// tie first best found
	public void selectTool(PlayerInventory inventory, String tool, ClientPlayerInteractionManager interactionManager, boolean silkTouch) {
		int material = 0;
		boolean named = false;
		boolean fortune = false;
		int numEnchants = 0;
		int bestIndex = -1;
		for (int i = 0; i < inventory.main.size(); ++i) {
			ItemStack curStack = inventory.main.get(i);
			String stackString = curStack.toString();
			LOGGER.info(stackString);
			boolean replace = false;
			// Find the best item
			if (stackString.contains(tool)) {
				// skip tool if not silk_touch
				NbtList enchantments = curStack.getEnchantments();
				if (silkTouch && enchantments.asString().indexOf("silk_touch") == -1) {
					continue;
				} else if (!silkTouch && enchantments.asString().indexOf("silk_touch") > -1) {
					continue;
				}
				
				// decide best tool
				if (bestIndex == -1) {
					replace = true;
				} else {
					int curMaterial = this.matchMaterial(stackString);
					if (curMaterial > material) {
						replace = true;
					} else if (curMaterial == material && curStack.getName().asString() != "" && !named) {
						replace = true;
					} else if (curMaterial == material && curStack.getName().asString() != "" && (!silkTouch && enchantments.asString().indexOf("fortune") > -1 && !fortune)) {
						replace = true;
					} else if (curMaterial == material && curStack.getName().asString() != "" && (!silkTouch && enchantments.asString().indexOf("fortune") > -1) && enchantments.size() > numEnchants) {
						replace = true;
					} else if (curMaterial == material && curStack.getName().asString() != "" && !fortune && enchantments.size() > numEnchants) {
						replace = true;
					// Set of cases where tools are not named just enchanted
					} else if (curMaterial == material && !named && (!silkTouch && enchantments.asString().indexOf("fortune") > -1 && !fortune)) {
						replace = true;
					} else if (curMaterial == material && !named && (!silkTouch && enchantments.asString().indexOf("fortune") > -1) && enchantments.size() > numEnchants) {
						replace = true;
					} else if (curMaterial == material && !named && !fortune && enchantments.size() > numEnchants) {
						replace = true;
					}
				}
				
				if (replace) {
					material = this.matchMaterial(stackString);
					named = curStack.getName().asString() != "";
					fortune = enchantments.asString().indexOf("fortune") > -1;
					numEnchants = enchantments.size();
					bestIndex = i;
				}
			}
		}
			
		// Place the best item in your toolbar or select it if there
		if (bestIndex > -1) {
			if (PlayerInventory.isValidHotbarIndex(bestIndex)) {
				inventory.selectedSlot = bestIndex;
			} else {
				interactionManager.pickFromInventory(bestIndex);
			}
		}
	}
	
	private int matchMaterial(String stackString) {
		if (stackString.contains("wood")) {
			return WOOD;
		} else if (stackString.contains("stone")) {
			return STONE;
		} else if (stackString.contains("gold")) {
			return GOLD;
		} else if (stackString.contains("iron")) {
			return IRON;
		} else if (stackString.contains("diamond")) {
			return DIAMOND;
		} else if (stackString.contains("netherite")) {
			return NETHERITE;
		} else {
			return -1;
		}
	}
}
