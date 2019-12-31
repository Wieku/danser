#version 330

uniform sampler2DArray tex;
uniform int points;

in vec2 tex_coord;
in float index;
in float lengthScale;
in vec4 col_tint;

out vec4 color;

void main() {
    vec4 in_color = texture(tex, vec3(tex_coord, 0));
	color = in_color * col_tint * vec4(1.0, 1.0, 1.0, 1-smoothstep(points*lengthScale / 3.0, points*lengthScale, index));
    //color.rgb *= color.a;
}