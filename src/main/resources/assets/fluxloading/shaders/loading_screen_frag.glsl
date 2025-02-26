#version 330 core

uniform sampler2D screenTexture;
uniform float percentage;

in vec2 TexCoords;
out vec4 FragColor;

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
    vec4 texColor = texture(screenTexture, TexCoords);
    vec3 color = texColor.rgb;
    float a = texColor.a;

    if (a < 0.1)
        FragColor = texColor;
    else
        FragColor = vec4(color, 1.0);

    float dissolveThreshold = noise(TexCoords * 7.0);
    if (dissolveThreshold < percentage)
    {
        FragColor.a -= 0.3;
        FragColor.a = (FragColor.a < 0.0) ? 0.0 : FragColor.a;
    }

    if (percentage != 0.0)
        FragColor.a = FragColor.a * (1.0 - percentage);
}
