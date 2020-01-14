precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D s_texture;
void main()
{
    vec4 c = texture2D(s_texture, v_texCoord);
    if (c.r >= 0.254){
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (c.r >= 0.098) {
        gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else {
        discard;
    }
}
