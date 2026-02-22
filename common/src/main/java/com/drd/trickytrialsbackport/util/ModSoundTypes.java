package com.drd.trickytrialsbackport.util;

import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.world.level.block.SoundType;

public class ModSoundTypes {
    public static SoundType HEAVY_CORE = new SoundType(
            1f,
            1f,
            ModSounds.HEAVY_CORE_BREAK.get(),
            ModSounds.HEAVY_CORE_STEP.get(),
            ModSounds.HEAVY_CORE_PLACE.get(),
            ModSounds.HEAVY_CORE_HIT.get(),
            ModSounds.HEAVY_CORE_FALL.get()
    );
    public static SoundType POLISHED_TUFF = new SoundType(
            1f,
            1f,
            ModSounds.POLISHED_TUFF_BREAK.get(),
            ModSounds.POLISHED_TUFF_STEP.get(),
            ModSounds.POLISHED_TUFF_PLACE.get(),
            ModSounds.POLISHED_TUFF_HIT.get(),
            ModSounds.POLISHED_TUFF_FALL.get()
    );
    public static SoundType TUFF_BRICKS = new SoundType(
            1f,
            1f,
            ModSounds.TUFF_BRICKS_BREAK.get(),
            ModSounds.TUFF_BRICKS_STEP.get(),
            ModSounds.TUFF_BRICKS_PLACE.get(),
            ModSounds.TUFF_BRICKS_HIT.get(),
            ModSounds.TUFF_BRICKS_FALL.get()
    );

    public static void register() {
    }
}
