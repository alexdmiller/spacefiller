package sketches;

import particles.Particle;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Web extends Scene {
	private static int NUM_PARTICLES = 1000;

	public static void main(String[] args) {
		main("sketches.Web");
	}

	@Mod(min = 0, max = 500, defaultValue = 100)
	public float repelThreshold = 100;

	@Mod(min = 0, max = 500, defaultValue = 200)
	public float racistRepelThreshold = 200;

	@Mod(min = 0, max = 500, defaultValue = 300)
	public float attractionThreshold = 300;

	@Mod(min = 0, max = 500, defaultValue = 200)
	public float drawThreshold = 300;

	@Mod(min = 0, max = 10, defaultValue = 0.5f)
	public float attractionWeight = 0.5f;

	@Mod(min = 0, max = 10, defaultValue = 10f)
	public float repelWeight = 10f;

	@Mod(min = 0.9f, max = 1, defaultValue = 0.995f)
	public float friction = 0.995f;

	@Mod(min = -.01f, max = 0.01f, defaultValue = 0.005f)
	public float rotationSpeed = 0.005f;

	@Mod(min = 0, max = 20, defaultValue = 1)
	public float thickness = 1;

	@Mod(min = 0, max = 20, defaultValue = 5)
	public float pointSize = 5;

	public float innerRadius = 100;
	public float outerRadius = 2000;

	private List<Particle> particles;
	private float rotation;
	private Color[] colors = new Color[] {
			new Color(255, 188, 151),
			new Color(133, 255, 248),
			new Color(226, 181, 255) };

	@Override
	public void doSetup() {
		new OscRemoteControl(this, 12003);
		particles = new ArrayList<>();

		for (int i = 0; i < NUM_PARTICLES; i++) {
			particles.add(new Particle(
					(float) Math.random() * WIDTH - WIDTH / 2,
					(float) Math.random() * HEIGHT - HEIGHT / 2,
					(float) Math.random() * WIDTH - WIDTH / 2,
					colors[(int) Math.floor(Math.random() * colors.length)]));
		}
	}

	@Mod
	public void switchAlliances() {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).color = colors[(int) Math.floor(Math.random() * colors.length)];
		}
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		graphics.noSmooth();
		graphics.translate(WIDTH / 2, HEIGHT / 2, WIDTH / 2);
		graphics.rotateY(rotation);

		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);

			for (int j = i + 1; j < particles.size(); j++) {
				Particle p2 = particles.get(j);
				PVector delta = PVector.sub(p.position, p2.position);
				if (delta.mag() < attractionThreshold && p.color.equals(p2.color)) {
					float forceMagnitude = 1 / delta.mag();
					PVector attractionForce = delta.copy().setMag(forceMagnitude).mult(attractionWeight);
					p.applyForce(PVector.mult(attractionForce, -1));
					p2.applyForce(attractionForce);
				}

				if ((p.color.equals(p2.color) && delta.mag() < repelThreshold) || (!p.color.equals(p2.color) &&delta.mag() < racistRepelThreshold)) {
					float forceMagnitude = 1 / delta.mag();
					PVector repelForce = delta.copy().setMag(forceMagnitude).mult(repelWeight);
					p.applyForce(repelForce);
					p2.applyForce(PVector.mult(repelForce, -1));
				}

				if (delta.mag() < drawThreshold) {
					if (p.color.equals(p2.color)) {
						graphics.stroke(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), map(delta.mag(), 0, drawThreshold, 255, 0));
						graphics.strokeWeight(map(delta.mag(), 0, drawThreshold, thickness, 0));
						graphics.line(
								p.position.x, p.position.y, p.position.z,
								p2.position.x, p2.position.y, p2.position.z);
					}
				}
			}

			graphics.stroke(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 200);
			graphics.strokeWeight(pointSize);
			graphics.point(p.position.x, p.position.y, p.position.z);

			PVector distFromCenter = PVector.sub(new PVector(0, 0), p.position);

			if (distFromCenter.mag() > outerRadius) {
				PVector copy = distFromCenter.copy();
				p.applyForce(PVector.mult(copy.normalize(), 0.1f));
			}

			if (distFromCenter.mag() < innerRadius) {
				PVector copy = distFromCenter.copy();
				p.applyForce(PVector.mult(copy.normalize(), -0.1f));
			}

			p.flushForces(-1);
			p.applyFriction(friction);
			p.update();
		}
		rotation += rotationSpeed;
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}
}
