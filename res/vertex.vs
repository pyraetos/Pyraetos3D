#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexCoord;
layout (location=2) in vec3 inNormal;

out vec2 texCoord;
out vec3 normal;
out vec3 vertex;
out vec4 lightMVVertex;

uniform mat4 proj;
uniform mat4 modelView;
uniform mat4 lightMV;
uniform mat4 ortho;

void main()
{
	vec4 vertex4 = modelView * vec4(position, 1.0);
    gl_Position = proj * vertex4;
    texCoord = inTexCoord;
    normal = normalize(modelView * vec4(inNormal, 0.0)).xyz;
    vertex = vertex4.xyz;
    lightMVVertex = ortho * lightMV * vec4(position, 1.0);
}