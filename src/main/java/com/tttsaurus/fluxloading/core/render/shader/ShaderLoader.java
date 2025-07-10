package com.tttsaurus.fluxloading.core.render.shader;

import com.tttsaurus.fluxloading.core.util.RlReaderUtils;

public final class ShaderLoader
{
    public static Shader load(String rl, Shader.ShaderType shaderType)
    {
        String raw = RlReaderUtils.read(rl, true);
        if (raw.isEmpty()) return null;

        return new Shader(rl, raw, shaderType);
    }
}
