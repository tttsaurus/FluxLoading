package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.animation.SmoothDamp;
import com.tttsaurus.fluxloading.core.accessor.ChunkProviderClientAccessor;
import com.tttsaurus.fluxloading.core.network.FluxLoadingNetwork;
import com.tttsaurus.fluxloading.core.raycast.FrustumChunkRayCastHelper;
import com.tttsaurus.fluxloading.core.raycast.Ray;
import com.tttsaurus.fluxloading.core.render.CommonBuffers;
import com.tttsaurus.fluxloading.core.render.RenderUtils;
import com.tttsaurus.fluxloading.core.render.Texture2D;
import com.tttsaurus.fluxloading.core.render.shader.Shader;
import com.tttsaurus.fluxloading.core.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.core.render.shader.ShaderProgram;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.List;

@SuppressWarnings("all")
public final class FluxLoadingManager
{
    private static boolean active = false;

    // render
    private static ShaderProgram shaderProgram = null;
    private static FloatBuffer vertexBuffer;

    private static boolean screenshotToggle = false;
    private static boolean forceLoadingTitle = false;
    private static boolean disableVanillaTexts = false;
    private static Texture2D texture = null;
    private static BufferedImage screenshot = null;

    // movement lock
    private static boolean movementLocked = false;
    private static boolean lockPosFetched = false;
    private static double lockX, lockY, lockZ;

    // extra chunk loading
    private static boolean waitChunksToLoad = true;
    private static boolean finishChunkLoading = false;
    private static boolean countingChunkLoaded = false;
    private static int chunkLoadedNum = 0;
    private static int targetChunkNum = 0;
    private static boolean startCalcTargetChunkNum = false;
    private static boolean targetChunkNumCalculated = false;
    private static boolean chunkLoadingTitle = false;
    private static boolean chunkLoadingPercentage = false;
    private static int chunkRayCastTestRayDis = 512;

    // fade-out animation
    private static double extraWaitTime = 0.5d;
    private static double fadeOutDuration = 1.0d;
    private static StopWatch fadeOutStopWatch = null;
    private static SmoothDamp fadeOutSmoothDamp = null;
    private static double prevFadeOutTime = 0d;

    // fade-in animation
    private static boolean finishFadingIn = false;
    private static double fadeInDuration = 1.0d;
    private static StopWatch fadeInStopWatch = null;
    private static SmoothDamp fadeInSmoothDamp = null;
    private static double prevFadeInTime = 0d;

    // debug
    private static boolean debug = false;
    private static List<Ray> frustumRays = null;

    //<editor-fold desc="getters & setters">
    public static boolean isActive() { return active; }

    public static void setActive(boolean flag) { active = flag; }

    public static void prepareScreenshot() { screenshotToggle = true; }

    public static boolean isForceLoadingTitle() { return forceLoadingTitle; }

    public static void setForceLoadingTitle(boolean flag) { forceLoadingTitle = flag; }

    public static boolean isDisableVanillaTexts() { return disableVanillaTexts; }

    public static void setDisableVanillaTexts(boolean flag) { disableVanillaTexts = flag; }

    public static void setChunkLoadingTitle(boolean flag) { chunkLoadingTitle = flag; }

    public static void setChunkLoadingPercentage(boolean flag) { chunkLoadingPercentage = flag; }

    public static boolean isTextureAvailable() { return texture != null; }

    public static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static boolean isMovementLocked() { return movementLocked; }

    public static void resetMovementLocked()
    {
        movementLocked = false;
        lockPosFetched = false;
        lockX = 0;
        lockY = 0;
        lockZ = 0;
    }

    public static boolean isWaitChunksToLoad() { return waitChunksToLoad; }

    public static void setWaitChunksToLoad(boolean flag) { waitChunksToLoad = flag; }

    public static void setFinishChunkLoading(boolean flag) { finishChunkLoading = flag; }

    public static boolean isCountingChunkLoaded() { return countingChunkLoaded; }

    public static void setCountingChunkLoaded(boolean flag) { countingChunkLoaded = flag; }

    public static int getChunkLoadedNum() { return chunkLoadedNum; }

    public static void incrChunkLoadedNum() { chunkLoadedNum++; }

    public static void resetChunkLoadedNum() { chunkLoadedNum = 0; }

    public static boolean isStartCalcTargetChunkNum() { return startCalcTargetChunkNum; }

    public static void setStartCalcTargetChunkNum(boolean flag) { startCalcTargetChunkNum = flag; }

    public static boolean isTargetChunkNumCalculated() { return targetChunkNumCalculated; }

    public static void resetTargetChunkNumCalculated() { targetChunkNumCalculated = false; }

    public static int getTargetChunkNum() { return targetChunkNum; }

    public static void resetTargetChunkNum() { targetChunkNum = 0; }

    public static void setChunkRayCastTestRayDis(int dis) { chunkRayCastTestRayDis = dis; }

    public static void setExtraWaitTime(double time) { extraWaitTime = time; }

    public static void setFadeOutDuration(double time) { fadeOutDuration = time; }

    public static void startFadeOutTimer()
    {
        fadeOutStopWatch = new StopWatch();
        fadeOutStopWatch.start();
        fadeOutSmoothDamp = new SmoothDamp(0, 1, (float)fadeOutDuration);
        prevFadeOutTime = 0d;
    }

    public static void resetFadeOutTimer()
    {
        if (fadeOutStopWatch != null)
        {
            fadeOutStopWatch.stop();
            fadeOutStopWatch = null;
        }
    }

    public static double getFadeInDuration() { return fadeInDuration; }

    public static void setFadeInDuration(double duration) { fadeInDuration = duration; }

    public static void resetFinishFadingIn() { finishFadingIn = false; }

    public static void startFadeInTimer()
    {
        fadeInStopWatch = new StopWatch();
        fadeInStopWatch.start();
        fadeInSmoothDamp = new SmoothDamp(1, 0, (float)fadeOutDuration);
        prevFadeInTime = 0d;
    }

    public static void resetFadeInTimer()
    {
        if (fadeInStopWatch != null)
        {
            fadeInStopWatch.stop();
            fadeInStopWatch = null;
        }
    }

    public static void setDebug(boolean flag) { debug = flag; }
    //</editor-fold>

    public static void calcTargetChunkNum()
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            ChunkProviderClient chunkProvider = Minecraft.getMinecraft().world.getChunkProvider();
            Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

            Vec3d camPos = RenderUtils.getCameraPos();

            ClippingHelper frustumHelper = ClippingHelperImpl.getInstance();
            Frustum viewFrustum = new Frustum(frustumHelper);
            viewFrustum.setPosition(camPos.x, camPos.y, camPos.z);

            List<Chunk> visibleChunks = new ArrayList<>();
            for (Chunk chunk : loadedChunks.values())
            {
                int chunkX = chunk.x;
                int chunkZ = chunk.z;

                double minX = chunkX * 16;
                double minY = 0;
                double minZ = chunkZ * 16;

                double maxX = minX + 16;
                double maxY = 256;
                double maxZ = minZ + 16;

                AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

                if (viewFrustum.isBoundingBoxInFrustum(box))
                    if (!chunk.isEmpty())
                        visibleChunks.add(chunk);
            }

            FluxLoading.logger.info("Chunk count from ChunkProviderClient: " + loadedChunks.size());
            FluxLoading.logger.info("Visible chunks from player's perspective: " + visibleChunks.size());

            frustumRays = FrustumChunkRayCastHelper.getRaysFromFrustum(camPos, ClippingHelperImpl.getInstance(), 10, 10);
            targetChunkNum = FrustumChunkRayCastHelper.getChunkRayCastNum(frustumRays, visibleChunks, chunkRayCastTestRayDis);

            FluxLoading.logger.info("Visible chunks after frustum ray casting: " + targetChunkNum);

            targetChunkNumCalculated = true;
        });
    }

    //<editor-fold desc="save & read">
    public static void trySaveToLocal()
    {
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        if (server != null)
        {
            File worldSaveDir = new File("saves/" + server.getFolderName());
            if (screenshot != null)
                RenderUtils.createPng(
                        worldSaveDir,
                        "last_screenshot",
                        screenshot);
        }
    }

    public static void tryReadFromLocal(String folderName)
    {
        File screenshot = new File("saves/" + folderName + "/last_screenshot.png");
        if (screenshot.exists())
        {
            Texture2D texture = RenderUtils.readPng(screenshot);
            if (texture != null) updateTexture(texture);
        }
    }
    //</editor-fold>

    //<editor-fold desc="shader">
    private static void triggerShader()
    {
        GL20.glGetVertexAttrib(0, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED, CommonBuffers.INT_BUFFER_16);
        boolean enabled = CommonBuffers.INT_BUFFER_16.get(0) == GL11.GL_TRUE;

        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(0, 3, false, 0, vertexBuffer);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

        if (enabled)
            GL20.glEnableVertexAttribArray(0);
        else
            GL20.glDisableVertexAttribArray(0);
    }

    public static void initShader()
    {
        if (shaderProgram == null)
        {
            Shader vertex = ShaderLoader.load("fluxloading:shaders/loading_screen_vertex.glsl", Shader.ShaderType.VERTEX);
            Shader frag = ShaderLoader.load("fluxloading:shaders/loading_screen_frag.glsl", Shader.ShaderType.FRAGMENT);

            shaderProgram = new ShaderProgram(vertex, frag);
            shaderProgram.setup();

            FluxLoading.logger.info(shaderProgram.getSetupDebugReport());

            shaderProgram.use();
            shaderProgram.setUniform("screenTexture", 1);
            shaderProgram.setUniform("percentage", 0f);
            shaderProgram.setUniform("enableDissolving", false);
            shaderProgram.setUniform("enableWaving", false);
            shaderProgram.setUniform("enableDarkOverlay", FluxLoadingConfig.ENABLE_DARK_OVERLAY);
            shaderProgram.setUniform("enable3x3Blur", false);
            shaderProgram.setUniform("enable5x5Blur", false);
            shaderProgram.setUniform("enableKawaseBlur", false);
            shaderProgram.setUniform("targetBlurStrength", 1f);
            if (FluxLoadingConfig.ENABLE_BLUR)
            {
                switch (FluxLoadingConfig.BLUR_ALGORITHM)
                {
                    case "3x3_gaussian_blur" -> { shaderProgram.setUniform("enable3x3Blur", true); }
                    case "5x5_gaussian_blur" -> { shaderProgram.setUniform("enable5x5Blur", true); }
                    case "kawase_blur" -> { shaderProgram.setUniform("enableKawaseBlur", true); }
                }
                shaderProgram.setUniform("targetBlurStrength", FluxLoadingConfig.BLUR_STRENGTH);
            }
            shaderProgram.unuse();

            vertexBuffer = ByteBuffer.allocateDirect(9 * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            // vec2(-1, -1), vec2(3, -1), vec2(-1, 3)
            vertexBuffer.put(new float[]{-1, -1, 0, 3, -1, 0, -1, 3, 0}).flip();
        }
    }

    public static void resetShader()
    {
        if (shaderProgram != null)
        {
            shaderProgram.use();
            shaderProgram.setUniform("percentage", 1f);
            shaderProgram.unuse();
        }
    }

    public static void setShaderFadingState(boolean state)
    {
        if (shaderProgram != null)
        {
            shaderProgram.use();
            // fade-in
            if (state)
            {
                shaderProgram.setUniform("enableDissolving", FluxLoadingConfig.ENABLE_FADEIN_DISSOLVING_EFFECT);
                shaderProgram.setUniform("enableWaving", FluxLoadingConfig.ENABLE_FADEIN_WAVING_EFFECT);
            }
            // fade-out
            else
            {
                shaderProgram.setUniform("enableDissolving", FluxLoadingConfig.ENABLE_FADEOUT_DISSOLVING_EFFECT);
                shaderProgram.setUniform("enableWaving", FluxLoadingConfig.ENABLE_FADEOUT_WAVING_EFFECT);
            }
            shaderProgram.unuse();
        }
    }
    //</editor-fold>

    //<editor-fold desc="draw overlay">
    public static void drawOverlayDefaultWorldLoadingAndFadingInPhase()
    {
        if (!FluxLoadingAPI.duringFadingInPhase && !finishFadingIn)
        {
            FluxLoadingAPI.duringFadingInPhase = true;
            startFadeInTimer();
        }
        if (!FluxLoadingAPI.duringDefaultWorldLoadingPhase)
            FluxLoadingAPI.duringDefaultWorldLoadingPhase = true;

        boolean set = false;
        float percentage = 0f;
        if (!finishFadingIn)
        {
            double fadeInTime = fadeInStopWatch.getNanoTime() / 1E9d;
            if (fadeInTime >= fadeInDuration)
            {
                finishFadingIn = true;
                FluxLoadingAPI.duringFadingInPhase = false;
                resetFadeInTimer();
                percentage = 0f;
                set = true;
            }
            else
            {
                double delta = fadeInTime - prevFadeInTime;
                percentage = fadeInSmoothDamp.evaluate((float)delta);
                prevFadeInTime = fadeInTime;
                set = true;
            }
        }

        drawOverlay(set, percentage);
    }

    private static void drawOverlayFadingInPhase()
    {
        boolean set = false;
        float percentage = 0f;
        if (!finishFadingIn)
        {
            double fadeInTime = fadeInStopWatch.getNanoTime() / 1E9d;
            if (fadeInTime >= fadeInDuration)
            {
                finishFadingIn = true;
                FluxLoadingAPI.duringFadingInPhase = false;
                resetFadeInTimer();
                percentage = 0f;
                set = true;
            }
            else
            {
                double delta = fadeInTime - prevFadeInTime;
                percentage = fadeInSmoothDamp.evaluate((float)delta);
                prevFadeInTime = fadeInTime;
                set = true;
            }
        }

        drawOverlay(set, percentage);
    }

    private static void drawOverlayChunkLoadingPhase()
    {
        drawOverlay(false, 0f);
    }

    private static void drawOverlayWaitAndFadingOutPhase(double fadeOutTime)
    {
        boolean set = false;
        float percentage = 0f;
        if (fadeOutTime >= extraWaitTime)
        {
            double nowFadeOutTime = fadeOutTime - extraWaitTime;
            double delta = nowFadeOutTime - prevFadeOutTime;
            percentage = fadeOutSmoothDamp.evaluate((float)delta);
            prevFadeOutTime = nowFadeOutTime;
            set = true;
        }

        drawOverlay(set, percentage);
    }

    private static void drawOverlay(boolean setPercentage, float percentage)
    {
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.INT_BUFFER_16);
        int texUnit = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.INT_BUFFER_16);
        int texUnit1TextureID = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.bindTexture(texture.getGlTextureID());

        GlStateManager.setActiveTexture(texUnit);

        shaderProgram.use();

        if (setPercentage)
            shaderProgram.setUniform("percentage", percentage);

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        shaderProgram.setUniform("resolution",
                (float)resolution.getScaledWidth_double(),
                (float)resolution.getScaledHeight_double());

        triggerShader();
        shaderProgram.unuse();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.INT_BUFFER_16);
        texUnit = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.bindTexture(texUnit1TextureID);

        GlStateManager.setActiveTexture(texUnit);

        if (depthTest)
            GlStateManager.enableDepth();
        else
            GlStateManager.disableDepth();
        if (blend)
            GlStateManager.enableBlend();
        else
            GlStateManager.disableBlend();
    }
    //</editor-fold>

    public static void tick()
    {
        for (Runnable runnable: FluxLoadingAPI.fluxLoadingTickListeners)
            runnable.run();
        FluxLoadingAPI.tickNum++;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (active)
        {
            if (!FluxLoadingAPI.finishLoading)
                if (!movementLocked)
                    movementLocked = true;

            if (FluxLoadingAPI.duringDefaultWorldLoadingPhase)
                FluxLoadingAPI.duringDefaultWorldLoadingPhase = false;

            // usually finishFadingIn == true here
            if (!finishFadingIn)
                drawOverlayFadingInPhase();
            else
            {
                //<editor-fold desc="extra chunk loading phase">
                if (!finishChunkLoading)
                {
                    if (!FluxLoadingAPI.duringExtraChunkLoadingPhase)
                        FluxLoadingAPI.duringExtraChunkLoadingPhase = true;

                    drawOverlayChunkLoadingPhase();

                    if (chunkLoadingTitle && targetChunkNumCalculated)
                    {
                        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
                        String i18nText = I18n.format("fluxloading.loading_wait");
                        float width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                        RenderUtils.renderText(i18nText,
                                (resolution.getScaledWidth() - width) / 2,
                                (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2 + (chunkLoadingPercentage ? -10 : 0),
                                1, Color.WHITE.getRGB(), true);

                        if (chunkLoadingPercentage)
                        {
                            String text = String.format("%d/%d, %.1f", chunkLoadedNum, targetChunkNum, ((float) chunkLoadedNum / (float) targetChunkNum) * 100f) + "%";
                            width = RenderUtils.fontRenderer.getStringWidth(text);
                            RenderUtils.renderText(text,
                                    (resolution.getScaledWidth() - width) / 2,
                                    (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2 + 10,
                                    1, Color.WHITE.getRGB(), true);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="extra wait phase + fading out phase">
                if (fadeOutStopWatch != null)
                {
                    double fadeOutTime = fadeOutStopWatch.getNanoTime() / 1E9d;

                    if (!FluxLoadingAPI.duringExtraWaitPhase)
                    {
                        FluxLoadingAPI.duringExtraChunkLoadingPhase = false;
                        FluxLoadingAPI.duringExtraWaitPhase = true;
                    }

                    if (fadeOutTime >= extraWaitTime && !FluxLoadingAPI.duringFadingOutPhase)
                    {
                        FluxLoadingAPI.duringExtraWaitPhase = false;
                        FluxLoadingAPI.duringFadingOutPhase = true;

                        Minecraft.getMinecraft().setIngameFocus();

                        setShaderFadingState(false);
                    }

                    if (fadeOutTime >= fadeOutDuration + extraWaitTime)
                    {
                        resetFadeOutTimer();
                        texture.dispose();
                        resetShader();

                        FluxLoadingAPI.duringFadingInPhase = false;
                        FluxLoadingAPI.duringDefaultWorldLoadingPhase = false;
                        FluxLoadingAPI.duringExtraChunkLoadingPhase = false;
                        FluxLoadingAPI.duringExtraWaitPhase = false;
                        FluxLoadingAPI.duringFadingOutPhase = false;
                        FluxLoadingAPI.finishLoading = true;

                        if (movementLocked)
                        {
                            FluxLoadingNetwork.requestPlayerLock(false);
                            movementLocked = false;
                        }

                        FluxLoadingAPI.stopWatch.stop();
                        double timeMs = FluxLoadingAPI.stopWatch.getNanoTime() / 1e6d;

                        for (Runnable runnable: FluxLoadingAPI.fluxLoadingEndListeners)
                            runnable.run();

                        FluxLoading.logger.info("Finished world flux loading process. Time taken: " + timeMs + " ms. Tick count: " + FluxLoadingAPI.tickNum);

                        active = false;

                        return;
                    }

                    drawOverlayWaitAndFadingOutPhase(fadeOutTime);
                }
                //</editor-fold>
            }

            if (!FluxLoadingAPI.finishLoading) tick();
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if (screenshotToggle)
        {
            screenshotToggle = false;
            Minecraft minecraft = Minecraft.getMinecraft();
            screenshot = ScreenShotHelper.createScreenshot(minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
        }

        if (debug && frustumRays != null)
        {
            RenderUtils.storeCommonGlStates();
            for (Ray ray: frustumRays)
            {
                GlStateManager.pushMatrix();

                Vec3d camPos = RenderUtils.getWorldOffset();

                GlStateManager.translate(
                        (float)(-camPos.x + ray.pos.x),
                        (float)(-camPos.y + ray.pos.y),
                        (float)(-camPos.z + ray.pos.z));

                GlStateManager.disableCull();
                GlStateManager.enableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableBlend();

                GlStateManager.glLineWidth(3.0F);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
                buffer.pos(0, 0, 0).endVertex();
                buffer.pos(ray.dir.x * 5, ray.dir.y * 5, ray.dir.z * 5).endVertex();
                tessellator.draw();

                GlStateManager.popMatrix();
            }
            RenderUtils.restoreCommonGlStates();
        }
    }

    // client side lock
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null)
        {
            if (movementLocked)
            {
                EntityPlayerSP player = Minecraft.getMinecraft().player;

                if (!lockPosFetched)
                {
                    lockPosFetched = true;
                    lockX = player.posX;
                    lockY = player.posY;
                    lockZ = player.posZ;
                    FluxLoadingNetwork.requestPlayerLock(true);
                }

                player.movementInput.moveForward = 0;
                player.movementInput.moveStrafe = 0;
                player.movementInput.forwardKeyDown = false;
                player.movementInput.backKeyDown = false;
                player.movementInput.leftKeyDown = false;
                player.movementInput.rightKeyDown = false;
                player.movementInput.jump = false;
                player.movementInput.sneak = false;
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
                player.setPosition(lockX, lockY, lockZ);
            }
        }
    }

    // server side lock
    public static final Map<UUID, Vec3d> serverLockPos = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (!serverLockPos.isEmpty())
            {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

                List<UUID> outdated = new ArrayList<>();
                for (Map.Entry<UUID, Vec3d> entry: serverLockPos.entrySet())
                {
                    UUID uuid = entry.getKey();
                    EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(uuid);
                    if (player == null)
                    {
                        outdated.add(uuid);
                        continue;
                    }
                    Vec3d pos = entry.getValue();
                    player.connection.setPlayerLocation(pos.x, pos.y, pos.z, player.rotationYaw, player.rotationPitch);
                }
                for (UUID uuid: outdated)
                    serverLockPos.remove(uuid);
            }
        }
    }
}
