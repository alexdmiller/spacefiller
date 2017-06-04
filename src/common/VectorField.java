package common;

import processing.core.PVector;

import java.io.Serializable;

public interface VectorField extends Serializable {
	PVector at(float x, float y, float z, float t);
}
