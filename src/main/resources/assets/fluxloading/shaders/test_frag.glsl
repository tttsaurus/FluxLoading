#version 330 core

uniform sampler2D screenTexture;

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
}
