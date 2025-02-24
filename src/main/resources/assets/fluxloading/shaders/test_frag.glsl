#version 330 core

uniform sampler2D screenTexture;
uniform float percentage;

in vec2 TexCoords;
out vec4 FragColor;

void main()
{
    vec4 texColor = texture(screenTexture, TexCoords);
    vec3 color = texColor.rgb;
    float a = texColor.a;

    if (a < 0.1)
        FragColor = texColor;
    else
        FragColor = vec4(color, 1.0);

    if (percentage != 0.0)
        FragColor = vec4(FragColor.rgb, FragColor.a * (1.0 - percentage));
}
