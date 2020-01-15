#version 300 es
precision mediump float;
uniform samplerCube s_texture;
in vec3 v_normal;
out vec4 o_FragColor;
void main(){
    o_FragColor = texture(s_texture, v_normal);
}
