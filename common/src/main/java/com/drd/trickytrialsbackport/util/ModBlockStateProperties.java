package com.drd.trickytrialsbackport.util;

import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerState;
import com.drd.trickytrialsbackport.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModBlockStateProperties {
    public static final BooleanProperty CRAFTING = BooleanProperty.create("crafting");
    public static final BooleanProperty OMINOUS = BooleanProperty.create("ominous");
    public static final EnumProperty<TrialSpawnerState> TRIAL_SPAWNER_STATE = EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);
    public static final EnumProperty<VaultState> VAULT_STATE = EnumProperty.create("vault_state", VaultState.class);
}
