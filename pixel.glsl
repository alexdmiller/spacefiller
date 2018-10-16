varying float fragHeight;
varying float fragWidth;
varying vec4 vertColor;
uniform float spacing;//size of the grid
// uniform float gwidth;//grid lines'width in pixels


void main()
{
    float f = fract(fragHeight * spacing);
    float df = fwidth(fragHeight * spacing);
    float g = smoothstep(df * 1.0, df * 2.0, f);
    gl_FragColor = vec4(1-g, 1-g, 1-g, 1.0) * vertColor;
}

//void main()
//{
//    float f  = abs(fract (fragHeight * gsize)-0.5);
//    float df = fwidth(fragHeight * gsize);
//    float g = smoothstep(-gwidth*df,gwidth*df , f);
//    gl_FragColor = vec4(2.0 - g, 2.0 - g, 2.0 - g, 1.0);// * gl_Color;
//}