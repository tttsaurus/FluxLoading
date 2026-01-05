package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.chunk.FluxLoadingChunkGate;
import com.tttsaurus.fluxloading.core.chunk.FluxLoadingChunkSource;
import com.tttsaurus.fluxloading.core.fsm.FluxLoadingFSM;
import com.tttsaurus.fluxloading.core.fsm.FluxLoadingPhase;
import com.tttsaurus.fluxloading.core.network.FluxLoadingNetwork;
import com.tttsaurus.fluxloading.core.player_freeze.FluxLoadingClientMovementLock;
import com.tttsaurus.fluxloading.core.player_freeze.FluxLoadingServerMovementLock;
import com.tttsaurus.fluxloading.core.render.RenderUtils;
import com.tttsaurus.fluxloading.core.render.Texture2D;
import com.tttsaurus.fluxloading.core.timing.FluxLoadingTimeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public final class FluxLoadingManager
{
    private FluxLoadingManager() { }

    static final FluxLoadingFSM FSM = new FluxLoadingFSM();
    private static final FluxLoadingOverlayRenderer OVERLAY = new FluxLoadingOverlayRenderer();
    private static final FluxLoadingTimeline TIMELINE = new FluxLoadingTimeline();
    private static final FluxLoadingChunkGate CHUNKS = new FluxLoadingChunkGate();
    private static final FluxLoadingClientMovementLock CLIENT_LOCK = new FluxLoadingClientMovementLock();
    public static final FluxLoadingServerMovementLock SERVER_LOCK = new FluxLoadingServerMovementLock();

    private static boolean active = false;

    private static boolean screenshotToggle = false;
    private static BufferedImage screenshot = null;

    private static Texture2D texture = null;

    private static boolean forceLoadingTitle = false;
    private static boolean disableVanillaTexts = false;

    /**
     * Only be accessed by {@link FluxLoadingAPI#isActive()}.
     */
    static boolean isActive()
    {
        return active;
    }

    public static FluxLoadingPhase getPhase()
    {
        return FSM.getPhase();
    }

    public static boolean isDisableVanillaTexts()
    {
        return disableVanillaTexts;
    }

    public static boolean isMovementLocked()
    {
        return CLIENT_LOCK.isLocked();
    }

    public static boolean shouldAllowSetIngameFocus()
    {
        // MinecraftMixin#setIngameFocus wants to allow focus during fading out OR after finished
        return !active || FSM.isDuring(FluxLoadingPhase.FADING_OUT) || FSM.isDuring(FluxLoadingPhase.FINISHED);
    }

    public static boolean shouldBlockKeyboardTick()
    {
        return CLIENT_LOCK.isLocked();
    }

    public static boolean isForceLoadingTitle()
    {
        return forceLoadingTitle;
    }

    public static void consumeForceLoadingTitle()
    {
        forceLoadingTitle = false;
    }

    // entry point

    public static void prepareScreenshot()
    {
        screenshotToggle = true;
    }

    public static void beginFluxLoading(
            String folderName,
            boolean instantlyPoppedUpLoadingTitle,
            boolean disableVanillaTextsFlag,
            boolean waitChunksToLoad,
            double fadeInDurationSeconds,
            double extraWaitSeconds,
            double fadeOutDurationSeconds)
    {
        tryReadFromLocal(folderName);

        if (texture == null)
        {
            active = false;
            FluxLoading.LOGGER.info("No screenshot found. Abort flux loading process.");
            return;
        }

        FluxLoading.LOGGER.info("Screenshot found. Start flux loading process.");

        disableVanillaTexts = disableVanillaTextsFlag;
        forceLoadingTitle = instantlyPoppedUpLoadingTitle;

        active = true;

        FSM.start();
        CHUNKS.reset(waitChunksToLoad);
        CLIENT_LOCK.reset();

        TIMELINE.reset();
        TIMELINE.startFadeIn(fadeInDurationSeconds);
        TIMELINE.configureFadeOut(extraWaitSeconds, fadeOutDurationSeconds);

        FluxLoadingAPI.tickNum = 0;

        for (Runnable r : FluxLoadingAPI.fluxLoadingStartListeners) r.run();

        FluxLoadingAPI.stopWatch = new org.apache.commons.lang3.time.StopWatch();
        FluxLoadingAPI.stopWatch.start();
    }

    public static void abortFluxLoading()
    {
        if (!active) return;

        finishAndCleanup();
    }

    public static void onChunkCompileTaskProcessed(FluxLoadingChunkSource source)
    {
        if (!active) return;

        // only meaningful after fading-in ends and we are in DEFAULT_WORLD_LOADING
        if (FSM.isDuring(FluxLoadingPhase.FADING_IN)) return;
        if (!FSM.isDuring(FluxLoadingPhase.DEFAULT_WORLD_LOADING) && !FSM.isDuring(FluxLoadingPhase.EXTRA_CHUNK_LOADING)) return;

        FluxLoadingChunkGate.Decision decision = CHUNKS.onChunkCompiled(source);

        if (decision == FluxLoadingChunkGate.Decision.DECIDE_WAIT_CHUNKS)
        {
            FSM.markDefaultWorldLoadingFinished();
            FSM.decideExtraChunkLoading(true);
            return;
        }

        if (decision == FluxLoadingChunkGate.Decision.DECIDE_SKIP_WAIT)
        {
            FSM.markDefaultWorldLoadingFinished();
            FSM.decideExtraChunkLoading(false);
            TIMELINE.startFadeOutSequence();
            return;
        }

        if (decision == FluxLoadingChunkGate.Decision.EXTRA_CHUNK_LOADING_FINISHED)
        {
            FSM.markExtraChunkLoadingFinished();
            TIMELINE.startFadeOutSequence();
        }
    }

    public static void renderAndTick()
    {
        if (!active) return;

        RenderUtils.storeCommonGlStates();

        CLIENT_LOCK.ensureLocked();

        FluxLoadingTimeline.UpdateResult timeline = TIMELINE.update();

        if (FSM.isDuring(FluxLoadingPhase.FADING_IN) && timeline.fadeInFinished)
        {
            FSM.markFadingInFinished();
        }

        if (FSM.isDuring(FluxLoadingPhase.EXTRA_WAIT) && timeline.extraWaitFinished)
        {
            FSM.markExtraWaitFinished();

            Minecraft.getMinecraft().setIngameFocus();
            ShaderResources.setShaderFadingState(false);
        }

        if (FSM.isDuring(FluxLoadingPhase.FADING_OUT) && timeline.fadeOutFinished)
        {
            FSM.markFadingOutFinished();
        }

        OVERLAY.render(texture, timeline.setPercentage, timeline.percentage);

        if (FSM.isDuring(FluxLoadingPhase.EXTRA_CHUNK_LOADING))
        {
            renderChunkLoadingTextIfEnabled();
        }

        tickInternal();

        if (FSM.isDuring(FluxLoadingPhase.FINISHED))
        {
            finishAndCleanup();
        }

        RenderUtils.restoreCommonGlStates();
    }

    private static void tickInternal()
    {
        for (Runnable r : FluxLoadingAPI.fluxLoadingTickListeners) r.run();

        FluxLoadingAPI.tickNum++;
    }

    private static void renderChunkLoadingTextIfEnabled()
    {
        if (!CHUNKS.isChunkLoadingTitleEnabled()) return;
        if (!CHUNKS.isTargetCalculated()) return;

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        String i18nText = net.minecraft.client.resources.I18n.format("fluxloading.loading_wait");
        float width = RenderUtils.fontRenderer.getStringWidth(i18nText);

        int yOffset = CHUNKS.isChunkLoadingPercentageEnabled() ? -10 : 0;

        RenderUtils.renderText(i18nText,
                (resolution.getScaledWidth() - width) / 2f,
                (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2f + yOffset,
                1,
                Color.WHITE.getRGB(),
                true);

        if (CHUNKS.isChunkLoadingPercentageEnabled() && CHUNKS.getTargetChunkNum() != 0)
        {
            String text = String.format("%d/%d, %.1f",
                    CHUNKS.getChunkLoadedNum(),
                    CHUNKS.getTargetChunkNum(),
                    ((float)CHUNKS.getChunkLoadedNum() / (float)CHUNKS.getTargetChunkNum()) * 100f) + "%";

            width = RenderUtils.fontRenderer.getStringWidth(text);

            RenderUtils.renderText(text,
                    (resolution.getScaledWidth() - width) / 2f,
                    (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2f + 10,
                    1,
                    Color.WHITE.getRGB(),
                    true);
        }
    }

    private static void finishAndCleanup()
    {
        if (!active) return;

        active = false;

        if (texture != null)
        {
            texture.dispose();
            texture = null;
        }

        ShaderResources.resetShader();

        if (CLIENT_LOCK.isLocked())
        {
            FluxLoadingNetwork.requestPlayerLock(false);
            CLIENT_LOCK.reset();
        }

        if (FluxLoadingAPI.stopWatch != null)
        {
            FluxLoadingAPI.stopWatch.stop();

            double timeMs = FluxLoadingAPI.stopWatch.getNanoTime() / 1e6d;
            FluxLoading.LOGGER.info("Finished world flux loading process. Time taken: " + timeMs + " ms. Tick count: " + FluxLoadingAPI.tickNum);
        }

        for (Runnable r : FluxLoadingAPI.fluxLoadingEndListeners) r.run();
    }

    public static void trySaveToLocal()
    {
        net.minecraft.server.integrated.IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        if (server != null)
        {
            File worldSaveDir = new File("saves/" + server.getFolderName());
            if (screenshot != null)
            {
                RenderUtils.createPng(worldSaveDir, "last_screenshot", screenshot);
            }
        }
    }

    public static void tryReadFromLocal(String folderName)
    {
        File file = new File("saves/" + folderName + "/last_screenshot.png");
        if (file.exists())
        {
            Texture2D tex = RenderUtils.readPng(file);
            if (tex != null)
            {
                updateTexture(tex);
            }
        }
    }

    private static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static void captureScreenshotIfRequested()
    {
        if (!screenshotToggle) return;

        screenshotToggle = false;

        Minecraft mc = Minecraft.getMinecraft();
        screenshot = net.minecraft.util.ScreenShotHelper.createScreenshot(mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
    }

    public static void applyClientTickLock()
    {
        CLIENT_LOCK.applyClientTickLock();
    }

    public static void setChunkLoadingTitle(boolean enabled)
    {
        CHUNKS.setChunkLoadingTitleEnabled(enabled);
    }

    public static void setChunkLoadingPercentage(boolean enabled)
    {
        CHUNKS.setChunkLoadingPercentageEnabled(enabled);
    }

    public static void setChunkRayCastTestRayDis(int dis)
    {
        CHUNKS.setChunkRayCastTestRayDis(dis);
    }
}
