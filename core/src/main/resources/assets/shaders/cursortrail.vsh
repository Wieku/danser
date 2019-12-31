#version 330

in vec3 in_position;
in vec2 in_mid;
in vec2 in_tex_coord;
in vec4 in_color;
in float in_scale;
in float in_lengthScale;
//in float in_index;

uniform mat4 proj;
uniform int points;
uniform float endScale;

out vec2 tex_coord;
out float index;
out float lengthScale;
out vec4 col_tint;

void main() {
    gl_Position = proj * vec4(in_position * in_scale * (endScale + (1.0 - endScale) * (gl_InstanceID % points) / (points*in_lengthScale)) + vec3(in_mid, 0), 1.0);
    tex_coord = in_tex_coord;
	index = points-1.0-gl_InstanceID%points;
    col_tint = in_color;
    lengthScale = in_lengthScale;
}