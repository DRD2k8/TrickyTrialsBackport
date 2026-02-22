package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {
    public static Supplier<SoundEvent> HEAVY_CORE_BREAK;
    public static Supplier<SoundEvent> HEAVY_CORE_STEP;
    public static Supplier<SoundEvent> HEAVY_CORE_FALL;
    public static Supplier<SoundEvent> HEAVY_CORE_PLACE;
    public static Supplier<SoundEvent> HEAVY_CORE_HIT;
    public static Supplier<SoundEvent> POLISHED_TUFF_BREAK;
    public static Supplier<SoundEvent> POLISHED_TUFF_STEP;
    public static Supplier<SoundEvent> POLISHED_TUFF_FALL;
    public static Supplier<SoundEvent> POLISHED_TUFF_PLACE;
    public static Supplier<SoundEvent> POLISHED_TUFF_HIT;
    public static Supplier<SoundEvent> TUFF_BRICKS_BREAK;
    public static Supplier<SoundEvent> TUFF_BRICKS_STEP;
    public static Supplier<SoundEvent> TUFF_BRICKS_FALL;
    public static Supplier<SoundEvent> TUFF_BRICKS_PLACE;
    public static Supplier<SoundEvent> TUFF_BRICKS_HIT;
    public static Supplier<SoundEvent> WIND_CHARGE_THROW;
    public static Supplier<SoundEvent> WIND_CHARGE_BURST;
    public static Supplier<SoundEvent> MACE_SMASH_AIR;
    public static Supplier<SoundEvent> MACE_SMASH_GROUND;
    public static Supplier<SoundEvent> MACE_SMASH_GROUND_HEAVY;
    public static Supplier<SoundEvent> MUSIC_DISC_CREATOR;
    public static Supplier<SoundEvent> MUSIC_DISC_CREATOR_MUSIC_BOX;
    public static Supplier<SoundEvent> MUSIC_DISC_PRECIPICE;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        HEAVY_CORE_BREAK = registerSound(helper, "block.heavy_core.break");
        HEAVY_CORE_STEP = registerSound(helper, "block.heavy_core.step");
        HEAVY_CORE_FALL = registerSound(helper, "block.heavy_core.fall");
        HEAVY_CORE_PLACE = registerSound(helper, "block.heavy_core.place");
        HEAVY_CORE_HIT = registerSound(helper, "block.heavy_core.hit");
        POLISHED_TUFF_BREAK = registerSound(helper, "block.polished_tuff.break");
        POLISHED_TUFF_STEP = registerSound(helper, "block.polished_tuff.step");
        POLISHED_TUFF_FALL = registerSound(helper, "block.polished_tuff.fall");
        POLISHED_TUFF_PLACE = registerSound(helper, "block.polished_tuff.place");
        POLISHED_TUFF_HIT = registerSound(helper, "block.polished_tuff.hit");
        TUFF_BRICKS_BREAK = registerSound(helper, "block.tuff_bricks.break");
        TUFF_BRICKS_STEP = registerSound(helper, "block.tuff_bricks.step");
        TUFF_BRICKS_FALL = registerSound(helper, "block.tuff_bricks.fall");
        TUFF_BRICKS_PLACE = registerSound(helper, "block.tuff_bricks.place");
        TUFF_BRICKS_HIT = registerSound(helper, "block.tuff_bricks.hit");
        WIND_CHARGE_THROW = registerSound(helper, "entity.wind_charge.throw");
        WIND_CHARGE_BURST = registerSound(helper, "entity.wind_charge.wind_burst");
        MACE_SMASH_AIR = registerSound(helper, "item.mace.smash_air");
        MACE_SMASH_GROUND = registerSound(helper, "item.mace.smash_ground");
        MACE_SMASH_GROUND_HEAVY = registerSound(helper, "item.mace.smash_ground_heavy");
        MUSIC_DISC_CREATOR = registerSound(helper, "music.disc.creator");
        MUSIC_DISC_CREATOR_MUSIC_BOX = registerSound(helper, "music.disc.creator_music_box");
        MUSIC_DISC_PRECIPICE = registerSound(helper, "music.disc.precipice");
    }

    private static Supplier<SoundEvent> registerSound(RegistryHelper helper, String name) {
        ResourceLocation id = new ResourceLocation(TrickyTrialsBackport.NAMESPACE, name);
        return helper.registerAuto(Registries.SOUND_EVENT, name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
