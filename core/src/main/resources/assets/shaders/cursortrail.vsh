#version 330

in vec3 in_position;
in vec2 in_mid;
in vec2 in_tex_coord;
//in float in_index;

uniform mat4 proj;
uniform float scale;
uniform float points;
uniform float endScale;

out vec2 tex_coord;
out float index;

void main() {
    gl_Position = proj * vec4(in_position * scale * (endScale + (1.0 - endScale) * (gl_InstanceID) / points) + vec3(in_mid, 0), 1.0);
    tex_coord = in_tex_coord;
	index = points-1.0-gl_InstanceID;
}