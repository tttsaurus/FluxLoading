package com.tttsaurus.fluxloading.core.accessor;

import com.tttsaurus.fluxloading.core.function.Func_1Param;
import com.tttsaurus.fluxloading.core.util.AccessorUnreflector;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;

public final class TessellatorAccessor
{
    private static boolean init = false;

    private static Func_1Param<WorldVertexBufferUploader, Tessellator> vboUploaderGetter;

    @SuppressWarnings("all")
    private static void init()
    {
        if (init) return;

        vboUploaderGetter = (Func_1Param<WorldVertexBufferUploader, Tessellator>)AccessorUnreflector.getDeclaredFieldGetter(
                Tessellator.class,
                "vboUploader",
                "field_178182_b");

        init = true;
    }

    public static WorldVertexBufferUploader getVboUploader(Tessellator obj)
    {
        init();
        return vboUploaderGetter.invoke(obj);
    }
}
