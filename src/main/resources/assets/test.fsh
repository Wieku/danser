#version 330

//uniform sampler2DArray tex;
uniform vec4 col;

//in vec2 tex_coord;
out vec4 color;

void main()
{
    color = col;//*texture(tex, vec3(tex_coord, 0));
}