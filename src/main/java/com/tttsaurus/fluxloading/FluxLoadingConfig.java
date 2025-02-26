package com.tttsaurus.fluxloading;

import net.minecraftforge.common.config.Configuration;

public class FluxLoadingConfig
{
    public static float WAIT_CHUNK_BUILD_COEFFICIENT;
    public static float EXTRA_WAIT_TIME;
    public static float FADE_OUT_DURATION;

    public static boolean ENABLE_WAVING_EFFECT;
    public static boolean ENABLE_DISSOLVING_EFFECT;

    public static Configuration CONFIG;

    public static void loadConfig()
    {
        try
        {
            CONFIG.load();

            WAIT_CHUNK_BUILD_COEFFICIENT = CONFIG.getFloat("Wait Chunk Build Coefficient", "general", 1f, 0f, 1f, "Theoretically, FluxLoading will wait until all chunks finished building if \"Wait Chunk Build Coefficient\" equaled 1.0\nSet it to lower numbers even 0.0 if you stuck at the loading screen");
            EXTRA_WAIT_TIME = CONFIG.getFloat("Extra Wait Time", "general", 0.5f, 0.1f, 10f, "Extra wait time before fade out animation");
            FADE_OUT_DURATION = CONFIG.getFloat("Fade Out Duration", "general", 1.0f, 0.5f, 10f, "The actual fade out time may feel shorter than this due to the exponentially decaying fade out function");

            ENABLE_WAVING_EFFECT = CONFIG.getBoolean("Enable Waving Effect", "shader", false, "An fade out option");
            ENABLE_DISSOLVING_EFFECT = CONFIG.getBoolean("Enable Dissolving Effect", "shader", false, "An fade out option");
        }
        catch (Exception ignored) { }
        finally
        {
            if (CONFIG.hasChanged()) CONFIG.save();
        }
    }
}
