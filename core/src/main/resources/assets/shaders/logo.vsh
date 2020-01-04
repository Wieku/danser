#version 330

in vec3 in_position;
in vec2 in_tex_coord;

uniform mat4 proj;

out vec2 tex_coord;

void main() {
    gl_Position = proj * vec4(in_position, 1.0);
    tex_coord = in_tex_coord;
}