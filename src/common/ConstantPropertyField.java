package common;

import modulation.Mod;

public class ConstantPropertyField implements ScalarField {
	public static ScalarField with(float constant) {
		return new ConstantPropertyField(constant);
	}

	@Mod(min = 0, max = 10, defaultValue = 3)
	public float value;

	ConstantPropertyField(float value) {
		this.value = value;
	}

	@Override
	public float at(float x, float y, float z, float t) {
		return value;
	}
}
