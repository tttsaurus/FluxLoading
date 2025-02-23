#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec2 aTexCoord;

out vec2 TexCoords;

void main()
{
    vec2 vertices[3] = vec2[3](vec2(-1, -1), vec2(3, -1), vec2(-1, 3));

    gl_Position = vec4(vertices[gl_VertexID], 0, 1);

    TexCoords = vec2(0.5 * gl_Position.x, -0.5 * gl_Position.y) + vec2(0.5);
    //TexCoords = 0.5 * gl_Position.xy + vec2(0.5);
}
