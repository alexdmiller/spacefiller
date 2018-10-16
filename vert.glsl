uniform mat4 transform;
uniform mat3 normalMatrix;
uniform vec3 lightNormal;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute float height;
attribute float width;

varying vec4 vertColor;
varying vec3 vertNormal;
varying vec3 vertLightDir;

varying float fragHeight;
varying float fragWidth;

void main() {
  gl_Position = transform * position;
  vertColor = color;
  vertNormal = normalize(normalMatrix * normal);
  vertLightDir = -lightNormal;
  // k = gl_Position.xyz;
  fragHeight = height;
  fragWidth = width;
}