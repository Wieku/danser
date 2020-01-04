#version 330

uniform sampler2DArray tex;
uniform float highlight;
uniform float animation;

in vec2 tex_coord;

out vec4 color;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

void main() {
    vec4 in_color = texture(tex, vec3(tex_coord, 0));

    vec3 hsv = rgb2hsv(in_color.rgb);

    color = vec4(vec3(hsv.r < highlight ? (hsv.r < animation ? 0.4f : 1f ) : 0f), (hsv.r < highlight ? 0.8f : 0f)*in_color.a);

}

