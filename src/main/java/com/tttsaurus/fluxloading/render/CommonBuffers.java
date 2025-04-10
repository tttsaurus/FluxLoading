package com.tttsaurus.fluxloading.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public final class CommonBuffers {

    public static final IntBuffer intBuffer = ByteBuffer.allocateDirect(16 << 2)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer();
}
