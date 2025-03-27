#version 120

attribute vec3 vertexPosition;
varying vec2 TexCoords;

void main()
{
    gl_Position = vec4(vertexPosition, 1);
    TexCoords = vec2(0.5 * gl_Position.x, -0.5 * gl_Position.y) + vec2(0.5);
}
