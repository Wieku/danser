#version 330

in vec2 in_position;
//in vec4 in_color;
//in vec2 in_tex_coord;

//out vec2 tex_coord;
//out vec4 i_color;
void main()
{
    gl_Position = vec4(in_position, 0, 1);
    //i_color = in_color;
    //tex_coord = in_tex_coord;
}