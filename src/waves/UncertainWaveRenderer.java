package waves;

import processing.core.PGraphics;
import sketches.UncertainWave;

public abstract class UncertainWaveRenderer {
	protected UncertainWave scene;

	public UncertainWaveRenderer(UncertainWave scene) {
		this.scene = scene;
	}
	public abstract void render(PGraphics canvas);
}
