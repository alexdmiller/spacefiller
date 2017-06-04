package scenes;

import boids.renderers.*;
import com.google.common.collect.Iterables;
import common.Particle;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;
import processing.core.PVector;
import waves.LineRenderer;
import waves.OceanRenderer;
import waves.UncertainWaveRenderer;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UncertainWave extends Scene {
	public static final int PARTICLES_PER_GROUP = 20;
	public static final int NUM_WAVES = 50;

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

	@Mod(min = 0, max = 1/100f, defaultValue = 0)
	public float osc1X = 0;

	@Mod(min = 0, max = 5, defaultValue = 0)
	public float osc1T = 0;

	@Mod(min = 0, max = 200, defaultValue = 0)
	public float osc1Amp = 1000;

	@Mod(min = 0, max = 1/100f, defaultValue = 0)
	public float osc2X = 1 / 600;

	@Mod(min = 0, max = 5, defaultValue = 0)
	public float osc2T = 0;

	@Mod(min = 0, max = 200, defaultValue = 0)
	public float osc2Amp = 0;

	@Mod(min = 0, max = 1/100f, defaultValue = 0)
	public float osc3Y = 0;

	@Mod(min = 0, max = 1, defaultValue = 0)
	public float osc3T = 0;

	@Mod(min = 0, max = 10, defaultValue = 0)
	public float osc3Amp = 0;

	@Mod(min = 0, max = 1/100f, defaultValue = 0)
	public float osc4Y = 0;

	@Mod(min = 0, max = 1, defaultValue = 0)
	public float osc4T = 0;

	@Mod(min = 0, max = 10, defaultValue = 0)
	public float osc4Amp = 0;

	public float repelThreshold = 40;

	public float repelWeight = 0.001f;

	private float step;
	public List<Wave> waves;

	@Mod
	public LineRenderer lineRenderer;

	@Mod
	public OceanRenderer oceanRenderer;

	private UncertainWaveRenderer[] renderers;

	public int currentRendererIndex = 0;

	@Mod
	public void applyJitterVertical() {
		applyJitter((float) Math.PI / 2);
	}

	@Mod
	public void applyJitterHorizontal() {
		applyJitter(0);
	}

	@Mod(min = 0, max = 2)
	public void setRenderer(int renderer) {
		renderer = Math.min(Math.max(renderer, 0), renderers.length - 1);
		currentRendererIndex = renderer;
	}

	private List<Particle> getAllParticles() {
		List<Particle> result = new ArrayList<>();
		for (Wave wave : waves) {
			for (ParticleGroup group : wave) {
				result.addAll(group.particles);
			}
		}
		return result;
	}

	@Override
	public void doSetup() {
		waves = new ArrayList<>();
		for (int i = 0; i < NUM_WAVES; i++) {
			waves.add(new Wave(WIDTH, PARTICLES_PER_GROUP, 1));
		}

		lineRenderer = new LineRenderer(this);
		oceanRenderer = new OceanRenderer(this);

		renderers = new UncertainWaveRenderer[] {
				lineRenderer,
				oceanRenderer
		};

		new OscSceneModulator(this, 12000);
	}

	private float waveFunction(float x, float y, float t) {
		return (float) (
				Math.sin(x * osc1X + t * osc1T + Math.sin(y * osc3Y + t * osc3T) * osc3Amp) * osc1Amp +
				Math.sin(x * osc2X * 100f + t * osc2T) * osc2Amp +
						Math.sin(y * osc4Y + t * osc4T) * osc4Amp
		);
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
		graphics.scale(1.2f);
		graphics.translate(-100, -100);

		List<Particle> allParticles = getAllParticles();
		for (int i = 0; i < allParticles.size(); i++) {
			Particle p = allParticles.get(i);

			for (int k = i + 1; k < allParticles.size(); k++) {
				Particle p2 = allParticles.get(k);
				PVector particleDelta = PVector.sub(p.position, p2.position);
				if (particleDelta.mag() < repelThreshold) {
					float forceMagnitude = 1 / Math.max(0.00001f, particleDelta.mag());
					PVector repelForce = particleDelta.copy().setMag(forceMagnitude).mult(repelWeight);

					p.applyForce(repelForce);
					p2.applyForce(PVector.mult(repelForce, -1));
				}
			}
		}

		for (int j = 0; j < waves.size(); j++) {
			Wave wave = waves.get(j);
			float y = j * UncertainWave.HEIGHT / UncertainWave.NUM_WAVES;

			for (ParticleGroup group : wave) {
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
					p.flushForces(-1);
					p.velocity.limit(maxParticleSpeed);

					p.update();
				}
			}
		}

		step += waveSpeed;

		renderers[currentRendererIndex].render(graphics);
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {
		applyJitterHorizontal();
	}

	public class ParticleGroup implements Iterable<Particle> {
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

	public class Wave implements Iterable<ParticleGroup> {
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
