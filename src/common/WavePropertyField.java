package common;

public class WavePropertyField implements ScalarField {
	@Override
	public float at(float x, float y, float t) {
		return (float) ((Math.sin(x / 50 + t / 10) * 3 + 3) + (Math.sin(y / 50 + t / 10) * 3 + 3));
	}
}
