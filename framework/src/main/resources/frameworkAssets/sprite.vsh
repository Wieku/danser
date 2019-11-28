#version 330

in vec3 in_position;
in vec3 in_tex_coord;
in vec4 in_color;
in float in_additive;

uniform mat4 proj;

out vec4 col_tint;
out vec3 tex_coord;
out vec2 maskCoord;
out float additive;
void main()
{
    maskCoord = in_position.xy;
    gl_Position = proj * vec4(in_position, 1);
    tex_coord = in_tex_coord;
    col_tint = in_color;
    additive = in_additive;
}