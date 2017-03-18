package common;

import java.io.Serializable;

public interface ScalarField extends Serializable {
	float at(float x, float y, float t);
}
