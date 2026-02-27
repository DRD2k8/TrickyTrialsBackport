package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.block.entity.trialspawner.Spawner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrialSpawnerItem extends BlockItem {
    public TrialSpawnerItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, level, tooltip, flag);
        Spawner.appendHoverText(stack, tooltip, "spawn_data");
    }
}
