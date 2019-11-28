#version 330

in vec4 col_tint;
in vec3 tex_coord;
in vec2 maskCoord;
in float additive;

uniform sampler2DArray tex;

out vec4 color;

uniform vec4 maskRect;
uniform float g_CornerRadius;

vec2 max3(vec2 a, vec2 b, vec2 c)
{
	return max(max(a, b), c);
}

float distanceFromRoundedRect()
{
	vec2 topLeftOffset = maskRect.xy - maskCoord;
	vec2 bottomRightOffset = maskCoord - maskRect.zw;

	vec2 distanceFromShrunkRect = max3(vec2(0.0), bottomRightOffset + vec2(g_CornerRadius), topLeftOffset + vec2(g_CornerRadius));
	return length(distanceFromShrunkRect);
}


void main()
{
	float dist = g_CornerRadius == 0.0 ? 0.0 : distanceFromRoundedRect();
    vec4 in_color = texture(tex, tex_coord);
	color = in_color*col_tint;

	float radiusCorrection = max(0.0, 1.0 - g_CornerRadius);
	if (dist > g_CornerRadius - 1.0 + radiusCorrection)
	color.a *= max(0.0, g_CornerRadius - dist + radiusCorrection);

	color.rgb *= color.a;
	color.a *= additive;
}