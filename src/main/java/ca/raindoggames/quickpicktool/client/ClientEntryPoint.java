package ca.raindoggames.quickpicktool.client;

import static ca.raindoggames.quickpicktool.QuickPickToolMod.LOGGER;

import ca.raindoggames.quickpicktool.blocktags.ToolBlockTags;
import ca.raindoggames.quickpicktool.inventory.PlayerInventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;

public class ClientEntryPoint implements ClientModInitializer {
	
	boolean breakHeld = false;
	boolean saveHeld = false;
	private ClientPlayerInteractionManager interactionManager = null;
	PlayerInventoryHelper helper = new PlayerInventoryHelper();
	
	
	// Add KeyBinding declaration and registration here	
	private static KeyBinding keyBindingBreak = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.quickpicktool.break",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_B,
			"category.quickpicktool.utils"
		));
	
	private static KeyBinding keyBindingSave = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.quickpicktool.save",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_V,
			"category.quickpicktool.utils"
		));
	
	@Override
	public void onInitializeClient() {
		LOGGER.info("Running client entrypoint initialize");
		// Event registration will be executed inside this method.
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Connect interactionManager
			if (this.interactionManager == null) {
				this.interactionManager = client.interactionManager;
			}
			
			if (keyBindingBreak.isPressed()) {
				this.breakHeld = true;
			} else {
				this.breakHeld = false;
			}
			
			if (keyBindingSave.isPressed()) {
				this.saveHeld = true;
			} else {
				this.saveHeld = false;
			}
		});
		
		
		// stack information is returned regardless of the block being in the player inventory or not
		ClientPickBlockApplyCallback.EVENT.register((player, hitResult, _stack) -> {
			ItemStack stack = _stack;
			if (this.breakHeld) {
				// replaces action with pick tool action				
				Block block = Block.getBlockFromItem(stack.getItem());
				BlockState state = block.getDefaultState();
				if (state.isIn(ToolBlockTags.SHEAR_MINEABLE)) {
					helper.selectTool(player.getInventory(), "shears", this.interactionManager, false);
				} else if (state.isIn(ToolBlockTags.SILK_TOUCH_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_pickaxe", this.interactionManager, false);
				} else if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_shovel", this.interactionManager, false);
				} else if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_pickaxe", this.interactionManager, false);
				} else if (state.isIn(BlockTags.AXE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_axe", this.interactionManager, false);
				} else if (state.isIn(BlockTags.HOE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_hoe", this.interactionManager, false);
				}
				return ItemStack.EMPTY;
			}
			if (this.saveHeld) {
				// replaces action with pick tool action				
				Block block = Block.getBlockFromItem(stack.getItem());
				BlockState state = block.getDefaultState();
				if (state.isIn(ToolBlockTags.SHEAR_MINEABLE)) {
					helper.selectTool(player.getInventory(), "shears", this.interactionManager, false);
				} else if (state.isIn(ToolBlockTags.SILK_TOUCH_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_pickaxe", this.interactionManager, true);
				} else if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_shovel", this.interactionManager, true);
				} else if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_pickaxe", this.interactionManager, true);
				} else if (state.isIn(BlockTags.AXE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_axe", this.interactionManager, true);
				} else if (state.isIn(BlockTags.HOE_MINEABLE)) {
					helper.selectTool(player.getInventory(), "_hoe", this.interactionManager, true);
				}
				return ItemStack.EMPTY;
			}
			// continue to return selected block if hotkey not pressed
			return stack;
		});
	}
}
