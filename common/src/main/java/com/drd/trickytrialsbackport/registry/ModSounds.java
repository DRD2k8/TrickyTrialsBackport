package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final RegistrySupplier<SoundEvent> HEAVY_CORE_BREAK = registerSound("block.heavy_core.break");
    public static final RegistrySupplier<SoundEvent> HEAVY_CORE_STEP = registerSound("block.heavy_core.step");
    public static final RegistrySupplier<SoundEvent> HEAVY_CORE_FALL = registerSound("block.heavy_core.fall");
    public static final RegistrySupplier<SoundEvent> HEAVY_CORE_PLACE = registerSound("block.heavy_core.place");
    public static final RegistrySupplier<SoundEvent> HEAVY_CORE_HIT = registerSound("block.heavy_core.hit");
    public static final RegistrySupplier<SoundEvent> POLISHED_TUFF_BREAK = registerSound("block.polished_tuff.break");
    public static final RegistrySupplier<SoundEvent> POLISHED_TUFF_STEP = registerSound("block.polished_tuff.step");
    public static final RegistrySupplier<SoundEvent> POLISHED_TUFF_FALL = registerSound("block.polished_tuff.fall");
    public static final RegistrySupplier<SoundEvent> POLISHED_TUFF_PLACE = registerSound("block.polished_tuff.place");
    public static final RegistrySupplier<SoundEvent> POLISHED_TUFF_HIT = registerSound("block.polished_tuff.hit");
    public static final RegistrySupplier<SoundEvent> TUFF_BRICKS_BREAK = registerSound("block.tuff_bricks.break");
    public static final RegistrySupplier<SoundEvent> TUFF_BRICKS_STEP = registerSound("block.tuff_bricks.step");
    public static final RegistrySupplier<SoundEvent> TUFF_BRICKS_FALL = registerSound("block.tuff_bricks.fall");
    public static final RegistrySupplier<SoundEvent> TUFF_BRICKS_PLACE = registerSound("block.tuff_bricks.place");
    public static final RegistrySupplier<SoundEvent> TUFF_BRICKS_HIT = registerSound("block.tuff_bricks.hit");
    public static final RegistrySupplier<SoundEvent> WIND_CHARGE_THROW = registerSound("entity.wind_charge.throw");
    public static final RegistrySupplier<SoundEvent> WIND_CHARGE_BURST = registerSound("entity.wind_charge.wind_burst");
    public static final RegistrySupplier<SoundEvent> MACE_SMASH_AIR = registerSound("item.mace.smash_air");
    public static final RegistrySupplier<SoundEvent> MACE_SMASH_GROUND = registerSound("item.mace.smash_ground");
    public static final RegistrySupplier<SoundEvent> MACE_SMASH_GROUND_HEAVY = registerSound("item.mace.smash_ground_heavy");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_CREATOR = registerSound("music.disc.creator");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_CREATOR_MUSIC_BOX = registerSound("music.disc.creator_music_box");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_PRECIPICE = registerSound("music.disc.precipice");

    private static RegistrySupplier<SoundEvent> registerSound(String name) {
        return ModRegistries.SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TrickyTrialsBackport.NAMESPACE, name)));
    }

    public static void register() {
        ModRegistries.SOUNDS.register();
    }
}
