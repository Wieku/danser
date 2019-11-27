#version 330

in vec4 col_tint;
in vec3 tex_coord;
in vec4 tex_bounds;
in float additive;

uniform sampler2DArray tex;

out vec4 color;

const float radius = 0.000001f;

vec2 max3(vec2 a, vec2 b, vec2 c)
{
	return max(max(a, b), c);
}

float distanceFromRoundedRect()
{
	vec2 topLeftOffset = tex_bounds.xy - tex_coord.xy;
	vec2 bottomRightOffset = tex_coord.xy - tex_bounds.zw;

	vec2 distanceFromShrunkRect = max3(vec2(0.0), bottomRightOffset + vec2(radius), topLeftOffset + vec2(radius));
	return length(distanceFromShrunkRect);
}

void main()
{
	float dist = radius == 0.0 ? 0.0 : distanceFromRoundedRect();
    vec4 in_color = texture(tex, tex_coord);
	color = in_color*col_tint;

	float radiusCorrection = max(0.0, 1.0/4096 - radius);
	if (dist > radius - 1.0/4096 + radiusCorrection)
	color.a *= max(0.0, radius - dist + radiusCorrection);

	color.rgb *= color.a;
	color.a *= additive;
}