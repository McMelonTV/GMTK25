#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_viewportRes;
uniform sampler2D u_texture;

varying vec2 v_texCoords;

float round(float f) {
    return ceil(f - 0.5);
}
vec2 round(vec2 v) {
    return vec2(round(v.x), round(v.y));
}

vec3 round(vec3 v) {
    return vec3(round(v.x), round(v.y), round(v.z));
}

vec4 round(vec4 v) {
    return vec4(round(v.x), round(v.y), round(v.z), round(v.w));
}



float random (vec2 st) {
    return fract(sin(dot(st.xy,
    vec2(12.9898,78.233)))
    * 43758.5453123);
}

float noise(vec2 x) {
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float a = random(p);
    float b = random(p+vec2(1.0,.0));
    float c = random(p+vec2(.0,1.));
    float d = random(p+vec2(1.0,1.));
    return mix(mix( a, b,f.x), mix( c, d,f.x),f.y);
}

const mat2 mtx = mat2( 0.80,  0.60, -0.60,  0.80 );
float fbm4(vec2 p) {
    float f = 0.0;

    f += 0.5000*(-1.0+2.0*noise(p)); p = mtx*p*2.02;
    f += 0.2500*(-1.0+2.0*noise(p)); p = mtx*p*2.03;
    f += 0.1250*(-1.0+2.0*noise(p)); p = mtx*p*2.01;
    f += 0.0625*(-1.0+2.0*noise(p));

    return f/0.9375;
}
vec2 fbm42(vec2 p) {
    return vec2( fbm4(p+vec2(1.0)), fbm4(p+vec2(6.2)) );
}
vec2 sampleAt(vec2 q, float speed) {
    float jacked_time = u_time*speed;
    const vec2 scale = vec2(0.11,0.13);
    q += 0.5*sin(scale*jacked_time + length(q)*0.5);
    vec2 o = fbm42(q);
    o += 0.01*sin(scale*jacked_time*length(o));
    return o;
}

vec3 rgb2hsv(vec3 rgb) {
    vec4 p = (rgb.g < rgb.b) ? vec4(rgb.bg, -1., 2. / 3.) : vec4(rgb.gb, 0., -1. / 3.);
    vec4 q = (rgb.r < p.x) ? vec4(p.xyw, rgb.r) : vec4(rgb.r, p.yzx);
    float c = q.x - min(q.w, q.y);
    float h = abs((q.w - q.y) / (6. * c + 1e-10) + q.z);
    vec3 hcv = vec3(h, c, q.x);
    float s = hcv.y / (hcv.z + 1e-10);
    return vec3(hcv.x, s, hcv.z);
}
vec3 hsv2rgb(vec3 c) {
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return c.z * mix(vec3(1.0), rgb, c.y);
}

vec3 invert(vec3 col) {
    return vec3(1.0 - col.r, 1.0 - col.g, 1.0 - col.b);
}

vec3 colA(vec2 uv) {
    float v = random(round(sampleAt(uv * 0.01, 1.0) + vec2(u_time * 0.1, u_time * 0.1) + uv * 0.01) * 0.00001);
    vec3 col = vec3(0.325, 0.071, 0.149);
    vec3 hsv = rgb2hsv(mix(col, invert(col), v) * 0.4);
    float h = mod((hsv.x + u_time * 0.03)*200.0, 200.0) / 200.0;
    return hsv2rgb(vec3(h, 0.3, hsv.z * 0.5));
}
vec3 colB(vec2 uv) {
    float v = random(round(sampleAt(uv * 0.01 + 0.53425, -1.0) - vec2(u_time * 0.1, u_time * 0.1) + uv * 0.01) * 0.00001);
    vec3 col = vec3(0.325, 0.071, 0.149);
    vec3 hsv = rgb2hsv(mix(col, invert(col), v) * 0.4);
    float h = mod((hsv.x + 0.1 + u_time * 0.03)*200.0, 200.0) / 200.0;
    return hsv2rgb(vec3(h, 0.3, hsv.z * 0.5));
}

void main() {
    vec2 target = u_viewportRes * 5.0;
    vec2 ratio = target / 2.0 / u_viewportRes;
    vec2 pos = floor(v_texCoords * target / ratio) * ratio;

    float x1 = floor((pos.x - u_time * 0.5) / 32.0);
    float y1 = floor((pos.y - u_time * 0.5) / 32.0);
    bool x2 = bool(mod(floor((pos.x + u_time * 0.5 * 20.0 * (mod(y1, 2.0) - 0.5) * 2.0) / 32.0), 2.0));
    bool y2 = bool(mod(floor((pos.y + u_time * 0.5 * 20.0 * (mod(x1, 2.0) - 0.5) * 2.0) / 32.0), 2.0));

    gl_FragColor = vec4(mix(colA(pos), colB(pos), float(x2 ^^ y2)), 1.0);
}
