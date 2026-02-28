package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.registry.ModItems;
import com.drd.trickytrialsbackport.util.ModBuiltInLootTables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange, ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
    public static final String TAG_NAME = "config";

    public static final VaultConfig DEFAULT = new VaultConfig(
            ModBuiltInLootTables.TRIAL_CHAMBERS_REWARD,
            4.0,
            4.5,
            new ItemStack(ModItems.TRIAL_KEY.get()),
            Optional.empty(),
            PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
            PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
    );

    public static final Codec<VaultConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC
                            .optionalFieldOf("loot_table")
                            .forGetter(cfg -> Optional.of(cfg.lootTable())),

                    Codec.DOUBLE
                            .optionalFieldOf("activation_range")
                            .forGetter(cfg -> Optional.of(cfg.activationRange())),

                    Codec.DOUBLE
                            .optionalFieldOf("deactivation_range")
                            .forGetter(cfg -> Optional.of(cfg.deactivationRange())),

                    ItemStack.CODEC
                            .optionalFieldOf("key_item")
                            .forGetter(cfg -> Optional.of(cfg.keyItem())),

                    ResourceLocation.CODEC
                            .optionalFieldOf("override_loot_table_to_display")
                            .forGetter(VaultConfig::overrideLootTableToDisplay)
            ).apply(instance, (lootOpt, actOpt, deactOpt, keyOpt, overrideOpt) ->
                    new VaultConfig(
                            lootOpt.orElse(DEFAULT.lootTable()),
                            actOpt.orElse(DEFAULT.activationRange()),
                            deactOpt.orElse(DEFAULT.deactivationRange()),
                            keyOpt.orElse(DEFAULT.keyItem()),
                            overrideOpt,
                            DEFAULT.playerDetector(),
                            DEFAULT.entitySelector()
                    )
            )
    );

    public VaultConfig(ResourceLocation lootTable,
                       double activationRange,
                       double deactivationRange,
                       ItemStack keyItem,
                       Optional<ResourceLocation> overrideLootTableToDisplay) {
        this(lootTable, activationRange, deactivationRange, keyItem,
                overrideLootTableToDisplay, DEFAULT.playerDetector(), DEFAULT.entitySelector());
    }

    private DataResult<VaultConfig> validate() {
        if (activationRange > deactivationRange) {
            return DataResult.error(() ->
                    "Activation range (" + activationRange + ") must be <= deactivation range (" + deactivationRange + ")");
        }
        return DataResult.success(this);
    }
}
