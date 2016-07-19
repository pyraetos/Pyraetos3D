#version 330

/*UNUSED CURRENTLY
struct PointLight
{
	vec3 color;
	vec3 position;//Must be in viewspace
	float intensity;
	float specularPower;
	Attenuation att;
	float attconstant;
	float attlinear;
	float attexponent;
};
*/

struct Material
{
	vec3 color;
	int useColor;
	//float reflectance;
};

in vec2 texCoord;
in vec3 normal;
in vec3 vertex;
in vec4 lightMVVertex;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform int useShadows;
uniform vec3 ambientLight;
uniform Material material;
//uniform PointLight pointLight;
uniform vec3 cameraPosition;
uniform vec3 directionalDir;
uniform vec3 fogColor;
uniform float fogDensity;

//Not using point light right now
vec4 light()
{
	/*
	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);
	
	vec3 lightPos = pl.position;
	
	//Diffuse
	vec3 lightDirection = lightPos - pos;
	float diffusion = max(dot(norm, lightDirection), 0.0);
	diffuseColor = vec4(pl.color, 1.0) * pl.intensity * diffusion;

	//Specular
	vec3 cameraDirection = normalize(-pos);
	vec3 reflectedLight = normalize(reflect(-lightDirection, norm));
	float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
	specularFactor = pow(specularFactor, pl.specularPower);
	specularColor = pl.intensity * specularFactor * material.reflectance * vec4(pl.color, 1.0);

	//Attenuation
	float d = length(lightDirection);
	float attenuation = pl.att.constant + pl.att.linear * d + pl.att.exponent * d * d;
	*/
	
	//Directional
	float dirAmt = max(dot(normal, normalize(directionalDir)), 0.0);
	vec4 dirColor = vec4(1.3, 1.3, 1.3, 1.0) * dirAmt;
	
	return /*(diffuseColor + specularColor) / attenuation +*/ dirColor;
}

float fog(){
	float d = length(vertex);
	float base = d * fogDensity;
    float fogFactor = 1.0 / exp(base * base);
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    return fogFactor;
}

float shadow(){
	float shadowFactor = 0.0;
	vec3 texCoords = lightMVVertex.xyz * 0.5 + 0.5;
	if(texCoords.z > 1.0){
		return 1.0;
	}
	float bias = 0.001f * tan(acos(dot(normal, directionalDir)));
    vec2 inc = 1.0 / textureSize(shadowMap, 0);
    for(int x = -2; x <= 2; x++)
    {
    	for(int y = -2; y <= 2; y++)
    	{
    		float depth = texture(shadowMap, texCoords.xy + vec2(x, y) * inc).r;
    		shadowFactor += texCoords.z - bias > depth ? 1.0 : 0.0;
    	}
    }
    return 1f - shadowFactor / 25f;
}

/*float ssao(){
	vec3 dmappos = texture(depthMap, ssTexCoord).xyz;
	
	float occFactor = 0f;
	
}*/

void main()
{
	vec4 color;
	if(material.useColor == 1){
   		color = vec4(material.color, 1);
   	}else{
   		color = texture(texture_sampler, texCoord);
   	}
   	float shadow = useShadows == 1 ? shadow() : 1f;
    vec4 shadowedLightColor = shadow <= 0.01f ? vec4(0f, 0f, 0f, 0f) : light() * shadow;
    vec4 totalLight = vec4(ambientLight, 1.0) + shadowedLightColor;
   	
   	float fogFactor = fog();
   	fragColor = (1.0 - fogFactor) * vec4(fogColor, 1.0) + fogFactor * color * totalLight;
}