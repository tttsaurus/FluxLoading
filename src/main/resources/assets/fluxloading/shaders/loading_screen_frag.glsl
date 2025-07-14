#version 120

uniform sampler2D screenTexture;
uniform float percentage;
uniform vec2 resolution;

uniform bool enableDissolving;
uniform bool enableWaving;
uniform bool enableDarkOverlay;
uniform bool enable3x3Blur;
uniform bool enable5x5Blur;
uniform bool enableKawaseBlur;
uniform float targetBlurStrength;

varying vec2 TexCoords;

float hash(vec2 p)
{
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main()
{
    vec2 coords = TexCoords;

    // waving
    if (enableWaving && percentage > 0.0)
    {
        vec2 center = vec2(0.5, 0.5);
        vec2 dir = TexCoords - center;
        float dist = length(dir);
        if (dist > 0.0)
        {
            float waveStrength = (percentage < 0.5) ? percentage * 0.05 : (1 - percentage) * 0.05;
            waveStrength *= (1.0 - dist);
            if (waveStrength > 0.0)
            {
                float wave = sin(dist * 30.0 + percentage * 10.0) * waveStrength;
                coords = TexCoords + normalize(dir) * wave;
                coords.x = clamp(coords.x, 0.0, 1.0);
                coords.y = clamp(coords.y, 0.0, 1.0);
            }
        }
    }

    vec4 texColor = texture2D(screenTexture, coords);

    float blurStrength = 0.0;
    if (percentage < 1.0)
        blurStrength = targetBlurStrength * (1.0 - percentage);

    // blur
    if (enable3x3Blur && blurStrength > 0.0)
    {
        texColor = vec4(0.0);
        float kernel[9];
        vec2 offset[9];

        float px = blurStrength / resolution.x;
        float py = blurStrength / resolution.y;

        kernel[0] = 1.0; kernel[1] = 2.0; kernel[2] = 1.0;
        kernel[3] = 2.0; kernel[4] = 4.0; kernel[5] = 2.0;
        kernel[6] = 1.0; kernel[7] = 2.0; kernel[8] = 1.0;

        offset[0] = vec2(-px, -py);  offset[1] = vec2(0.0, -py); offset[2] = vec2(px, -py);
        offset[3] = vec2(-px, 0.0);  offset[4] = vec2(0.0, 0.0); offset[5] = vec2(px, 0.0);
        offset[6] = vec2(-px, py);   offset[7] = vec2(0.0, py);  offset[8] = vec2(px, py);

        float totalWeight = 0.0;
        for (int i = 0; i < 9; ++i)
        {
            vec2 sampleCoord = coords + offset[i];
            sampleCoord = clamp(sampleCoord, 0.0, 1.0);
            texColor += texture2D(screenTexture, sampleCoord) * kernel[i];
            totalWeight += kernel[i];
        }
        texColor /= totalWeight;
    }
    if (enable5x5Blur && blurStrength > 0.0)
    {
        texColor = vec4(0.0);

        float kernel[25];
        vec2 offset[25];

        float px = blurStrength / resolution.x;
        float py = blurStrength / resolution.y;

        kernel[ 0] = 1.0;  kernel[ 1] =  4.0;  kernel[ 2] =  7.0;  kernel[ 3] =  4.0;  kernel[ 4] = 1.0;
        kernel[ 5] = 4.0;  kernel[ 6] = 16.0;  kernel[ 7] = 26.0;  kernel[ 8] = 16.0;  kernel[ 9] = 4.0;
        kernel[10] = 7.0;  kernel[11] = 26.0;  kernel[12] = 41.0;  kernel[13] = 26.0;  kernel[14] = 7.0;
        kernel[15] = 4.0;  kernel[16] = 16.0;  kernel[17] = 26.0;  kernel[18] = 16.0;  kernel[19] = 4.0;
        kernel[20] = 1.0;  kernel[21] =  4.0;  kernel[22] =  7.0;  kernel[23] =  4.0;  kernel[24] = 1.0;

        float totalWeight = 0.0;

        for (int i = 0; i < 5; ++i)
        {
            for (int j = 0; j < 5; ++j)
            {
                int index = i * 5 + j;
                float x = float(j - 2) * px;
                float y = float(i - 2) * py;
                offset[index] = vec2(x, y);
                totalWeight += kernel[index];
            }
        }

        for (int i = 0; i < 25; ++i)
        {
            vec2 sampleCoord = coords + offset[i];
            sampleCoord = clamp(sampleCoord, 0.0, 1.0);
            texColor += texture2D(screenTexture, sampleCoord) * kernel[i];
        }

        texColor /= totalWeight;
    }
    if (enableKawaseBlur && blurStrength > 0.0)
    {
        texColor = vec4(0.0);

        int sampleCount = 8;
        vec2 offset[8];

        offset[0] = vec2(-1.0, -1.0);
        offset[1] = vec2( 1.0, -1.0);
        offset[2] = vec2(-1.0,  1.0);
        offset[3] = vec2( 1.0,  1.0);
        offset[4] = vec2( 0.0, -1.0);
        offset[5] = vec2(-1.0,  0.0);
        offset[6] = vec2( 1.0,  0.0);
        offset[7] = vec2( 0.0,  1.0);

        float px = blurStrength / resolution.x;
        float py = blurStrength / resolution.y;

        for (int i = 0; i < sampleCount; ++i)
        {
            vec2 sampleCoord = coords + offset[i] * vec2(px, py);
            sampleCoord = clamp(sampleCoord, 0.0, 1.0);
            texColor += texture2D(screenTexture, sampleCoord);
        }

        texColor += texture2D(screenTexture, coords);
        texColor /= float(sampleCount + 1);
    }

    vec3 color = texColor.rgb;
    float a = texColor.a;

    if (a < 0.1)
        gl_FragColor = texColor;
    else
        gl_FragColor = vec4(color, 1.0);

    // dissolving
    if (enableDissolving)
    {
        float dissolveThreshold = noise(TexCoords * 7.0);
        if (dissolveThreshold < percentage)
        {
            gl_FragColor.a -= 0.3;
            gl_FragColor.a = (gl_FragColor.a < 0.0) ? 0.0 : gl_FragColor.a;
        }
    }

    if (enableDarkOverlay)
        if (percentage < 1.0)
            gl_FragColor.rgb *= (1.0 - (1.0 - percentage) * 0.4);

    if (percentage > 0.0)
        gl_FragColor.a *= (1.0 - percentage);
}
