package com.tttsaurus.fluxloading;

import net.minecraftforge.common.config.Configuration;

public class FluxLoadingConfig
{
    public static float FADE_IN_DURATION;
    public static float EXTRA_WAIT_TIME;
    public static float FADE_OUT_DURATION;
    public static boolean WAIT_CHUNKS_TO_LOAD;
    public static boolean INSTANTLY_POPPED_UP_LOADING_TITLE;
    public static boolean DISABLE_ALL_VANILLA_LOADING_TEXTS;
    public static boolean CHUNK_LOADING_INDICATOR;
    public static boolean CHUNK_LOADING_PERCENTAGE;
    public static int CHUNK_ESTIMATION_RAY_DISTANCE;
    public static boolean DEBUG;

    public static boolean ENABLE_IGI_INTEGRATION;
    public static boolean REGISTER_FLUXLOADING_MVVM;
    public static String[] MVVM_TO_DISPLAY_WHILE_LOADING;

    public static boolean ENABLE_FADEOUT_WAVING_EFFECT;
    public static boolean ENABLE_FADEOUT_DISSOLVING_EFFECT;
    public static boolean ENABLE_FADEIN_WAVING_EFFECT;
    public static boolean ENABLE_FADEIN_DISSOLVING_EFFECT;
    public static boolean ENABLE_DARK_OVERLAY;
    public static boolean ENABLE_BLUR;
    public static String BLUR_ALGORITHM;
    public static float BLUR_STRENGTH;

    public static Configuration CONFIG;

    public static void loadConfig()
    {
        try
        {
            CONFIG.load();

            DEBUG = CONFIG.getBoolean("Debug", "general.debug", false, "Display player frustum rays while estimating visible chunk number");

            EXTRA_WAIT_TIME = CONFIG.getFloat("Extra Wait Time", "general.timing", 0.5f, 0.1f, 10f, "Extra wait time after waiting chunks to load\nIt's the final delay before the fade-out animation");
            FADE_OUT_DURATION = CONFIG.getFloat("Fade Out Duration", "general.timing", 1.0f, 0.5f, 10f, "The actual fade-out time may feel shorter than this due to the exponentially decaying fade-out function");
            FADE_IN_DURATION = CONFIG.getFloat("Fade In Duration", "general.timing", 0.5f, 0.0f, 10f, "The actual fade-in time may feel shorter than this due to the exponentially decaying fade-in function");

            WAIT_CHUNKS_TO_LOAD = CONFIG.getBoolean("Wait Chunks to Load", "general.chunk", true, "Estimate visible chunks and wait them to load before fading out");
            CHUNK_LOADING_INDICATOR = CONFIG.getBoolean("Chunk Loading Indicator", "general.chunk", false, "Display a title while waiting chunks to load");
            CHUNK_LOADING_PERCENTAGE = CONFIG.getBoolean("Chunk Loading Percentage", "general.chunk", false, "Add another line to show loading percentage");
            CHUNK_ESTIMATION_RAY_DISTANCE = CONFIG.getInt("Visible Chunk Estimation Ray Distance", "general.chunk", 512, 100, 10000, "The distance of rays for visible chunk estimation test");

            INSTANTLY_POPPED_UP_LOADING_TITLE = CONFIG.getBoolean("Instantly Popped Up Loading Title", "general.misc", true, "Vanilla \"Loading world\" title has a lag, and this option forces that title to pop up immediately");
            DISABLE_ALL_VANILLA_LOADING_TEXTS = CONFIG.getBoolean("Disable All Vanilla Loading Texts", "general.misc", false, "Whether to disable all vanilla texts during world loading\nIt also affects \"Instantly Popped Up Loading Title\"");

            ENABLE_FADEOUT_WAVING_EFFECT = CONFIG.getBoolean("Fade Out - Enable Waving Effect", "general.shader", false, "A fade-out option");
            ENABLE_FADEOUT_DISSOLVING_EFFECT = CONFIG.getBoolean("Fade Out - Enable Dissolving Effect", "general.shader", false, "A fade-out option");
            ENABLE_FADEIN_WAVING_EFFECT = CONFIG.getBoolean("Fade In - Enable Waving Effect", "general.shader", false, "A fade-in option");
            ENABLE_FADEIN_DISSOLVING_EFFECT = CONFIG.getBoolean("Fade In - Enable Dissolving Effect", "general.shader", false, "A fade-in option");
            ENABLE_DARK_OVERLAY = CONFIG.getBoolean("Enable Dark Overlay", "general.shader", false, "An overlay on the screenshot");
            ENABLE_BLUR = CONFIG.getBoolean("Enable Blur", "general.shader.blur", false, "Apply blur on the screenshot");
            BLUR_ALGORITHM = CONFIG.getString("Blur Algorithm", "general.shader.blur", "kawase_blur", "Valid values: 3x3_gaussian_blur, 5x5_gaussian_blur, kawase_blur");
            BLUR_STRENGTH = CONFIG.getFloat("Blur Strength", "general.shader.blur", 1f, 0.5f, 2f, "Different blur algorithms produce very different visual results");

            ENABLE_IGI_INTEGRATION = CONFIG.getBoolean("Enable In-Game Info Reborn Integration", "integrating.igi", false, "Whether to enable the whole integration module");
            REGISTER_FLUXLOADING_MVVM = CONFIG.getBoolean("Register FluxLoading MVVM", "integrating.igi", true, "Whether to register FluxLoading MVVM as a default MVVM to use");
            MVVM_TO_DISPLAY_WHILE_LOADING = CONFIG.getStringList("MVVM to Display While Loading", "integrating.igi", new String[]{"fluxloading"}, "Input MVVM registry names here. You can use \"fluxloading\" if you enabled \"Register FluxLoading MVVM\"");
        }
        catch (Exception ignored) { }
        finally
        {
            if (CONFIG.hasChanged()) CONFIG.save();
        }
    }
}
