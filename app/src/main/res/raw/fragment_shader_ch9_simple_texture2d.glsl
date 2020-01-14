#version 300 es
precision mediump float;
uniform sampler2D s_texture;
in vec2 v_texCoord;
out vec4 o_fragColor;
void main()
{
    vec4 c = texture(s_texture, v_texCoord);
    if (c.r >= 0.254){
        o_fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (c.r >= 0.098) {
        o_fragColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else {
        discard;
    }
}
