#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoords;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    float average = (color.r + color.g + color.b) / 3.0;
    vec3 rgb = vec3(average) + vec3(0.0, 77.0 / 255.0, 105.0 / 255.0);
    gl_FragColor = vec4(rgb, color.a);
}
