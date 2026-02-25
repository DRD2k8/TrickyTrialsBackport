package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.function.Supplier;

public class ModPaintings {
    public static Supplier<PaintingVariant> MEDITATIVE;
    public static Supplier<PaintingVariant> PRAIRIE_RIDE;
    public static Supplier<PaintingVariant> BAROQUE;
    public static Supplier<PaintingVariant> HUMBLE;
    public static Supplier<PaintingVariant> UNPACKED;
    public static Supplier<PaintingVariant> BOUQUET;
    public static Supplier<PaintingVariant> CAVEBIRD;
    public static Supplier<PaintingVariant> COTAN;
    public static Supplier<PaintingVariant> ENDBOSS;
    public static Supplier<PaintingVariant> FERN;
    public static Supplier<PaintingVariant> OWLEMONS;
    public static Supplier<PaintingVariant> SUNFLOWERS;
    public static Supplier<PaintingVariant> TIDES;
    public static Supplier<PaintingVariant> BACKYARD;
    public static Supplier<PaintingVariant> POND;
    public static Supplier<PaintingVariant> CHANGING;
    public static Supplier<PaintingVariant> FINDING;
    public static Supplier<PaintingVariant> LOWMIST;
    public static Supplier<PaintingVariant> PASSAGE;
    public static Supplier<PaintingVariant> ORB;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        MEDITATIVE = helper.registerAuto(Registries.PAINTING_VARIANT, "meditative", () -> new PaintingVariant(16, 16));
        PRAIRIE_RIDE = helper.registerAuto(Registries.PAINTING_VARIANT, "prairie_ride", () -> new PaintingVariant(16, 32));
        BAROQUE = helper.registerAuto(Registries.PAINTING_VARIANT, "baroque", () -> new PaintingVariant(32, 32));
        HUMBLE = helper.registerAuto(Registries.PAINTING_VARIANT, "humble", () -> new PaintingVariant(32, 32));
        UNPACKED = helper.registerAuto(Registries.PAINTING_VARIANT, "unpacked", () -> new PaintingVariant(64, 64));
        BOUQUET = helper.registerAuto(Registries.PAINTING_VARIANT, "bouquet", () -> new PaintingVariant(48, 48));
        CAVEBIRD = helper.registerAuto(Registries.PAINTING_VARIANT, "cavebird", () -> new PaintingVariant(48, 48));
        COTAN = helper.registerAuto(Registries.PAINTING_VARIANT, "cotan", () -> new PaintingVariant(48, 48));
        ENDBOSS = helper.registerAuto(Registries.PAINTING_VARIANT, "endboss", () -> new PaintingVariant(48, 48));
        FERN = helper.registerAuto(Registries.PAINTING_VARIANT, "fern", () -> new PaintingVariant(48, 48));
        OWLEMONS = helper.registerAuto(Registries.PAINTING_VARIANT, "owlemons", () -> new PaintingVariant(48, 48));
        SUNFLOWERS = helper.registerAuto(Registries.PAINTING_VARIANT, "sunflowers", () -> new PaintingVariant(48, 48));
        TIDES = helper.registerAuto(Registries.PAINTING_VARIANT, "tides", () -> new PaintingVariant(48, 48));
        BACKYARD = helper.registerAuto(Registries.PAINTING_VARIANT, "backyard", () -> new PaintingVariant(48, 64));
        POND = helper.registerAuto(Registries.PAINTING_VARIANT, "pond", () -> new PaintingVariant(48, 64));
        CHANGING = helper.registerAuto(Registries.PAINTING_VARIANT, "changing", () -> new PaintingVariant(64, 32));
        FINDING = helper.registerAuto(Registries.PAINTING_VARIANT, "finding", () -> new PaintingVariant(64, 32));
        LOWMIST = helper.registerAuto(Registries.PAINTING_VARIANT, "lowmist", () -> new PaintingVariant(64, 32));
        PASSAGE = helper.registerAuto(Registries.PAINTING_VARIANT, "passage", () -> new PaintingVariant(64, 32));
        ORB = helper.registerAuto(Registries.PAINTING_VARIANT, "orb", () -> new PaintingVariant(64, 64));
    }
}
