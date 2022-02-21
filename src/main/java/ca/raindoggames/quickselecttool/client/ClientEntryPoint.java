package ca.raindoggames.quickselecttool.client;

import static ca.raindoggames.quickselecttool.QuickPickToolMod.LOGGER;

import java.util.HashSet;
import java.util.Set;

import ca.raindoggames.quickselecttool.inventory.PlayerInventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;

public class ClientEntryPoint implements ClientModInitializer {
	
	private boolean keyHeld = false;
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
		    if (keyBinding.isPressed()) {
		    	this.keyHeld = true;
		    } else {
		    	this.keyHeld = false;
		    }
		});
		
		// keybinding is not picked up on other events, need to combine Client tick event with this
		/*
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (this.keyHeld) {
				// replaces action
				return ActionResult.SUCCESS;
			}
			// continues to use other actions
			return ActionResult.PASS;
		});
		*/
		
		// stack information is returned regardless of the block being in the player inventory or not
		ClientPickBlockApplyCallback.EVENT.register((player, hitResult, _stack) -> {		
			ItemStack stack = _stack;
			if (this.keyHeld) {
				// replaces action with pick tool action				
				Block block = Block.getBlockFromItem(stack.getItem());
				if (BlockTags.SHOVEL_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "shovel");
				} else if (BlockTags.PICKAXE_MINEABLE.contains(block)) {
					helper.selectTool(player.getInventory(), "pickaxe");
				}
				return ItemStack.EMPTY;
			}
			// continue to return selected block if hotkey not pressed
			return stack;
		});
		
	}
}
