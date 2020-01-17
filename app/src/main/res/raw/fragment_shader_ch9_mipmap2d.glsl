#version 300 es
precision mediump float;
uniform sampler2D s_texture;
in vec2 v_texCoord;
out vec4 o_FragColor;
void main(){
    o_FragColor = texture(s_texture, v_texCoord);
}
