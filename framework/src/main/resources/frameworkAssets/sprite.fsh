#version 330

in vec4 col_tint;
in vec3 tex_coord;
in vec2 maskCoord;
in float additive;

uniform sampler2DArray tex;

out vec4 color;

uniform vec4 g_MaskRect;
uniform float g_CornerRadius;
uniform bool g_UseMask;
uniform float g_MaskBlendRange;

vec2 max3(vec2 a, vec2 b, vec2 c)
{
	return max(max(a, b), c);
}

float distanceFromRoundedRect()
{
	vec2 topLeftOffset = g_MaskRect.xy - maskCoord;
	vec2 bottomRightOffset = maskCoord - g_MaskRect.zw;

	vec2 distanceFromShrunkRect = max3(vec2(0.0), bottomRightOffset + vec2(g_CornerRadius), topLeftOffset + vec2(g_CornerRadius));
	return length(distanceFromShrunkRect);
}

void main()
{
	float dist = g_UseMask ? distanceFromRoundedRect() : 0.0;
    vec4 in_color = texture(tex, tex_coord);
	color = in_color*col_tint;

	float radiusCorrection = g_CornerRadius <= 0.0 ? g_MaskBlendRange : max(0.0, g_MaskBlendRange - g_CornerRadius);
	color.a *= min(1.0, (g_CornerRadius + radiusCorrection - dist) / g_MaskBlendRange );

	color.rgb *= color.a;
	color.a *= additive;
}