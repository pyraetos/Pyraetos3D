#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexCoord;
layout (location=2) in vec3 vertexNormal;

uniform mat4 lightMV;
uniform mat4 ortho;

void main()
{
	gl_Position = ortho * lightMV * vec4(position, 1.0);
}