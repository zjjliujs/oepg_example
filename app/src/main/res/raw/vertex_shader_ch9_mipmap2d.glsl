#version 300 es
uniform float u_offset;
in vec4 a_position;
in vec2 a_texCoord;
out vec2 v_texCoord;
void main(){
    gl_Position = a_position;
    gl_Position.x += u_offset;
    v_texCoord = a_texCoord;
}
