package ca.raindoggames.quickpicktool.blocktags;

import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;

public class ToolBlockTags {
	public static final TagKey<Block> SILK_TOUCH_MINEABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier("quickpicktool", "mineable/silk_touch"));
	public static final TagKey<Block> SHEAR_MINEABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier("quickpicktool", "mineable/shear"));
}
