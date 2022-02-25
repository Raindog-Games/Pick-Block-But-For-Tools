package ca.raindoggames.quickpicktool.client;

import static ca.raindoggames.quickpicktool.QuickPickToolMod.LOGGER;

import ca.raindoggames.quickpicktool.inventory.PlayerInventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;

public class ClientEntryPoint implements ClientModInitializer {
	
	boolean keyHeld = false;
	private ClientPlayerInteractionManager interactionManager = null;
	PlayerInventoryHelper helper = new PlayerInventoryHelper();
	
	
	// Add KeyBinding declaration and registration here	
	private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.quickpicktool.select",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_B,
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
			
			if (keyBinding.isPressed()) {
				this.keyHeld = true;
			} else {
				this.keyHeld = false;
			}
		});
		
		
		// stack information is returned regardless of the block being in the player inventory or not
		ClientPickBlockApplyCallback.EVENT.register((player, hitResult, _stack) -> {		
			ItemStack stack = _stack;
			if (this.keyHeld) {
				// replaces action with pick tool action				
				Block block = Block.getBlockFromItem(stack.getItem());
				if (BlockTags.SHOVEL_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "_shovel", this.interactionManager);
				} else if (BlockTags.PICKAXE_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "_pickaxe", this.interactionManager);
				} else if (BlockTags.AXE_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "_axe", this.interactionManager);
				} else if (BlockTags.HOE_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "_hoe", this.interactionManager);
				}
				return ItemStack.EMPTY;
			}
			// continue to return selected block if hotkey not pressed
			return stack;
		});
		
	}
}
