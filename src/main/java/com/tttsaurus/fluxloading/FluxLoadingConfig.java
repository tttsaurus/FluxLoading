package com.tttsaurus.fluxloading;

import net.minecraftforge.common.config.Configuration;

public class FluxLoadingConfig
{
    public static float WAIT_CHUNK_BUILD_COEFFICIENT;
    public static float EXTRA_WAIT_TIME;
    public static float FADE_OUT_DURATION;
    public static boolean INSTANTLY_POPPED_UP_LOADING_TITLE;
    public static boolean CHUNK_BUILDING_INDICATOR;

    public static boolean ENABLE_WAVING_EFFECT;
    public static boolean ENABLE_DISSOLVING_EFFECT;
    public static boolean ENABLE_DARK_OVERLAY;

    public static Configuration CONFIG;

    public static void loadConfig()
    {
        try
        {
            CONFIG.load();

            WAIT_CHUNK_BUILD_COEFFICIENT = CONFIG.getFloat("Wait Chunk Build Coefficient", "general", 0f, 0f, 2f, "FluxLoading estimates the number of chunks to build and waits until that stage\nYou can adjust this coefficient to modify the chunk loading wait time\nA value of 1.0 usually works fine\nOr you can set it to 0.0, which is disabling it, and use \"Extra Wait Time\" solely");
            EXTRA_WAIT_TIME = CONFIG.getFloat("Extra Wait Time", "general", 0.5f, 0.1f, 10f, "Extra wait time after waiting chunks to build\nIt's the final delay before the fade out animation");
            FADE_OUT_DURATION = CONFIG.getFloat("Fade Out Duration", "general", 1.0f, 0.5f, 10f, "The actual fade out time may feel shorter than this due to the exponentially decaying fade out function");
            INSTANTLY_POPPED_UP_LOADING_TITLE = CONFIG.getBoolean("Instantly Popped Up Loading Title", "general", true, "Vanilla \"Loading world\" title has a lag, and this option forces that title to pop up immediately");
            CHUNK_BUILDING_INDICATOR = CONFIG.getBoolean("Chunk Building Indicator", "general", false, "Display a title while waiting chunks to build");

            ENABLE_WAVING_EFFECT = CONFIG.getBoolean("Enable Waving Effect", "shader", false, "A fade out option");
            ENABLE_DISSOLVING_EFFECT = CONFIG.getBoolean("Enable Dissolving Effect", "shader", false, "A fade out option");
            ENABLE_DARK_OVERLAY = CONFIG.getBoolean("Enable Dark Overlay", "shader", false, "An overlay on the screenshot");
        }
        catch (Exception ignored) { }
        finally
        {
            if (CONFIG.hasChanged()) CONFIG.save();
        }
    }
}
