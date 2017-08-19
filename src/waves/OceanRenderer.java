package waves;

import particles.Particle;
import modulation.Mod;
import processing.core.PGraphics;
import sketches.UncertainWave;

import java.util.List;

/**
 * Created by miller on 9/29/16.
 */
public class OceanRenderer extends UncertainWaveRenderer {
	@Mod(min = 0, max = 0.3f, defaultValue = 0.1f)
	public float colorRotateSpeed = 0.1f;

	private float step;

	public OceanRenderer(UncertainWave scene) {
		super(scene);
	}

	@Override
	public void render(PGraphics graphics) {
		for (int j = 0; j < scene.waves.size(); j++) {
			UncertainWave.Wave wave = scene.waves.get(j);
			graphics.pushMatrix();

			float y = j * UncertainWave.HEIGHT / UncertainWave.NUM_WAVES;

			graphics.translate(0, y);

			graphics.fill(
					(float) Math.sin(y) * 20,
					(float) Math.sin(y / 90f + step) * 100 + 50,
					(float) Math.sin(y / 100f + step) * 100 + 200);
			graphics.noStroke();

			for (UncertainWave.ParticleGroup group : wave) {
				graphics.beginShape();

				List<Particle> particles = group.particles;

				for (int i = 0; i < particles.size(); i++) {
					Particle p = particles.get(i);
					graphics.curveVertex(p.position.x, p.position.y);
				}

				graphics.curveVertex(UncertainWave.WIDTH, UncertainWave.HEIGHT);
				graphics.curveVertex(0, UncertainWave.HEIGHT);
				graphics.curveVertex(0, UncertainWave.HEIGHT);
				graphics.endShape();
			}

			graphics.popMatrix();
		}

		step += colorRotateSpeed;
	}
}
