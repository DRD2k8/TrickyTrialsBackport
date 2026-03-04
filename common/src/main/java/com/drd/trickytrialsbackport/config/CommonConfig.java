package com.drd.trickytrialsbackport.config;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Config keys
    private static final String KEY_REPEATABLE_VAULT = "repeatableVault";

    // Default values
    private static final boolean DEFAULT_REPEATABLE_VAULT = false;

    // Runtime values
    private static boolean repeatableVault = DEFAULT_REPEATABLE_VAULT;

    private static Path configPath;

    public static void init(Path configDir) {
        configPath = configDir.resolve(TrickyTrialsBackport.MOD_ID + ".json");
        load();
    }

    public static boolean isRepeatableVault() {
        return repeatableVault;
    }

    public static void load() {
        if (!Files.exists(configPath)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            repeatableVault = json.has(KEY_REPEATABLE_VAULT)
                    ? json.get(KEY_REPEATABLE_VAULT).getAsBoolean()
                    : DEFAULT_REPEATABLE_VAULT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject json = new JsonObject();
        json.addProperty("_comment", "Keep it as false if you want the vault to be like vanilla. Change it to true if you want the vault to be repeatable.");
        json.addProperty(KEY_REPEATABLE_VAULT, repeatableVault);

        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(json, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
