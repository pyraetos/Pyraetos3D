#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 texCoord;

uniform mat4 proj;
uniform mat4 modelView;

void main()
{
	gl_Position = proj * modelView * vec4(position, 1.0);
    texCoord = inTexCoord;
}