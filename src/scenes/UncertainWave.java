package scenes;

import com.google.common.collect.Iterables;
import common.Particle;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UncertainWave extends Scene {
	private static final int PARTICLES_PER_GROUP = 20;
	private static final int NUM_WAVES = 20;

	public static void main(String[] args) {
		main("scenes.UncertainWave");
	}

	@Mod(min = -1, max = 1, defaultValue = 0.05f)
	public float waveSpeed = 0.05f;

	@Mod(min = 0, max = 100, defaultValue = 20)
	public float maxParticleSpeed = 20;

	@Mod(min = 0, max = 200, defaultValue = 100)
	public float maxJitterForce = 100;

	@Mod(min = 0, max = 100, defaultValue = 50)
	public float springDivisor = 50;

	@Mod(min = 0, max = 1, defaultValue = 0.97f)
	public float decay = 0.97f;

	@Mod(min = 0, max = 1/100f, defaultValue = 1/500f)
	public float osc1X = 1 / 500;

	@Mod(min = 0, max = 5, defaultValue = 1)
	public float osc1T = 1;

	@Mod(min = 0, max = 200, defaultValue = 100)
	public float osc1Amp = 100;

	@Mod(min = 0, max = 1/100f, defaultValue = 1/ 600f)
	public float osc2X = 1 / 600;

	@Mod(min = 0, max = 5, defaultValue = 1)
	public float osc2T = 1;

	@Mod(min = 0, max = 200, defaultValue = 100)
	public float osc2Amp = 100;

	@Mod(min = 0, max = 1/100f, defaultValue = 1/100f)
	public float osc3Y = 1 / 100;

	@Mod(min = 0, max = 1, defaultValue = 1/10f)
	public float osc3T = 1 / 10;

	@Mod(min = 0, max = 10, defaultValue = 3)
	public float osc3Amp = 3;

	private float step;
	private List<Wave> waves;

	@Mod
	public void applyJitterVertical() {
		applyJitter((float) Math.PI / 2);
	}

	@Mod
	public void applyJitterHorizontal() {
		applyJitter(0);
	}

	@Override
	public void doSetup() {
		waves = new ArrayList<>();
		for (int i = 0; i < NUM_WAVES; i++) {
			waves.add(new Wave(WIDTH, PARTICLES_PER_GROUP, 1));
		}

		new OscSceneModulator(this, 12000);
	}

	private float waveFunction(float x, float y, float t) {
		return (float) (
				(
						Math.sin(x * osc1X + t * osc1T) * osc1Amp +
						Math.sin(x * osc2X + t * osc2T) * osc2Amp
				)
				* Math.sin(y * osc3Y + t * osc3T) * osc3Amp);
	}


	public void applyJitter(float angle) {
		for (Wave wave : waves) {
			for (ParticleGroup group : wave) {
				for (Particle p : group) {
					PVector jitterForce = PVector.fromAngle(angle);
					jitterForce.setMag((float) (Math.random() * maxJitterForce - maxJitterForce / 2));
					p.applyForce(jitterForce);
				}
			}
		}
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		for (int j = 0; j < waves.size(); j++) {
			Wave wave = waves.get(j);
			graphics.pushMatrix();

			float y = j * HEIGHT / NUM_WAVES;

			graphics.translate(0, y);

			graphics.fill(
					(float) Math.sin(y) * 20,
					(float) Math.sin(y / 90f + step) * 100 + 50,
					(float) Math.sin(y / 100f + step) * 100 + 200);
			graphics.noStroke();

			for (ParticleGroup group : wave) {
				graphics.beginShape();

				List<Particle> particles = group.particles;
				for (int i = 0; i < particles.size(); i++) {
					Particle p = particles.get(i);

					// Seek the true position of the wave
					float waveX = i * WIDTH / PARTICLES_PER_GROUP;
					float waveY = waveFunction(waveX, y, step);
					PVector target = new PVector(waveX, waveY);

					PVector delta = PVector.sub(target, p.position);
					delta.div(springDivisor);
					p.velocity.add(delta);
					p.applyFriction(decay);
					p.flushForces();
					p.velocity.limit(maxParticleSpeed);

					p.update();

					graphics.curveVertex(p.position.x, p.position.y);
				}
				graphics.curveVertex(WIDTH, HEIGHT);
				graphics.curveVertex(0, HEIGHT);
				graphics.curveVertex(0, HEIGHT);
				graphics.endShape();
			}

			graphics.popMatrix();
		}

		step += waveSpeed;
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {
		applyJitterHorizontal();
	}

	class ParticleGroup implements Iterable<Particle> {
		public List<Particle> particles;

		public ParticleGroup(float width, int numParticles) {
			particles = new ArrayList<>();

			for (float x = 0; x < width; x += width / numParticles) {
				particles.add(new Particle(x, 0));
			}
		}

		@Override
		public Iterator<Particle> iterator() {
			return particles.iterator();
		}
	}

	class Wave implements Iterable<ParticleGroup> {
		public List<ParticleGroup> groups;

		public Wave(float width, int numParticles, int numGroups) {
			this.groups = new ArrayList<>();

			for (int i = 0; i < numGroups; i++) {
				this.groups.add(new ParticleGroup(width, numParticles));
			}
		}

		@Override
		public Iterator<ParticleGroup> iterator() {
			return groups.iterator();
		}
	}
}
