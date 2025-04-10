package com.tttsaurus.fluxloading.render.shader;

import com.tttsaurus.fluxloading.util.RlReaderUtils;

public final class ShaderLoader {

    public static Shader load(String rl, Shader.ShaderType shaderType) {
        String raw = RlReaderUtils.read(rl, true);
        if (raw.isEmpty()) return null;

        return new Shader(rl, raw, shaderType);
    }
}
