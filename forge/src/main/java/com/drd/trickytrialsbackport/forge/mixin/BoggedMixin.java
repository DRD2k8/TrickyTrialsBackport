package com.drd.trickytrialsbackport.forge.mixin;

import com.drd.trickytrialsbackport.entity.monster.Bogged;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(Bogged.class)
public abstract class BoggedMixin implements IForgeShearable {
    @Shadow public abstract boolean readyForShearing();
    @Shadow public abstract void shear(SoundSource source);

    @Override
    public boolean isShearable(ItemStack item, Level level, BlockPos pos) {
        return this.readyForShearing();
    }

    @Override
    public List<ItemStack> onSheared(Player player, ItemStack item, Level level, BlockPos pos, int fortune) {
        this.shear(SoundSource.PLAYERS);
        return Collections.emptyList();
    }
}
