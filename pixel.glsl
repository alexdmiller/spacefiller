varying float fragHeight;
varying float fragWidth;
varying vec4 vertColor;
uniform float spacing;//size of the grid
// uniform float gwidth;//grid lines'width in pixels

varying vec3 k;

void main()
{
    vec3 f  = fract (k * 10.0);
    vec3 df = fwidth(k * 10.0);

    vec3 g = smoothstep(df * 1.0, df * 2.0, f);

    float c = g.y;

    gl_FragColor = vec4(1-c, 1-c, 1-c, 1.0);
}


//void main()
//{
//    float f = fract(fragHeight * 100);
//    float df = fwidth(fragHeight * 100);
//    float g = smoothstep(df * 1.0, df * 2.0, f);
//    gl_FragColor = vec4(1-g, 1-g, 1-g, 1.0) * vertColor;
//
////    gl_FragColor = vec4(1, 0, 1, 1);
//}
//
//void main()
//{
//    float f  = abs(fract (fragHeight * gsize)-0.5);
//    float df = fwidth(fragHeight * gsize);
//    float g = smoothstep(-gwidth*df,gwidth*df , f);
//    gl_FragColor = vec4(2.0 - g, 2.0 - g, 2.0 - g, 1.0);// * gl_Color;
//}