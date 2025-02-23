#version 330 core

uniform sampler2D screenTexture;

in vec2 TexCoords;
out vec4 FragColor;

void main()
{
    vec4 texColor = texture(screenTexture, TexCoords);
    FragColor = texColor;
}
