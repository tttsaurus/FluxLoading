#version 130

uniform sampler2D screenTexture;
uniform float percentage;

uniform bool enableDissolving;
uniform bool enableWaving;
uniform bool enableDarkOverlay;

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

    vec4 texColor = texture(screenTexture, coords);
    vec3 color = texColor.rgb;
    float a = texColor.a;

    if (a < 0.1)
        gl_FragColor = texColor;
    else
        gl_FragColor = vec4(color, 1.0);

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
        gl_FragColor.rgb *= 0.6;

    if (percentage > 0.0)
        gl_FragColor.a *= (1.0 - percentage);
}
