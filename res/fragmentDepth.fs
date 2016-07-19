#version 330

uniform int isOrtho;

void main()
{
	gl_FragDepth = isOrtho == 1 ? gl_FragCoord.z : (gl_FragCoord.z - 0.97f) / 0.03f;
}