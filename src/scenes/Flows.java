package scenes;

import common.Particle;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;
import processing.core.PVector;

public class Flows extends Scene {
	public static void main(String[] args) {
		main("scenes.Flows");
	}

	@Mod(min = 0, max = 20)
	public float flowForce = 20;

	@Mod(min = 10, max = 1000)
	public float noiseScale = 500f;

	@Mod(min = -0.05f, max = 0.05f)
	public float noiseSpeed1 = 0.01f;

	@Mod(min = -0.05f, max = 0.05f)
	public float noiseSpeed2 = 0.01f;

	@Mod(min = 10, max = 100)
	public float lineLength = 100;

	@Mod(min = 0, max = 100)
	public float scrollSpeed = 1;

	@Mod(min = 0, max = 20)
	public float fallSpeed = 10;

	@Mod(min = -1, max = 1)
	public float lineSparsity = 0.6f;

	@Mod(min = 1, max = 10)
	public float lineThickness = 2;

	@Mod(min = 0, max = 1)
	public float interpolation = 0f;

	@Mod(min = 0, max = 500)
	public float circleRadius = 100;

	@Mod(min = 1, max = 1000)
	public int numPoints = 50;

	@Mod(min = 0, max = 0.01f)
	public float scrambleSpeed = 0.01f;

	float timeStep;
	float scramble = 0;
	float noise1Pos = 0;
	float noise2Pos = 0;

	@Override
	public void doSetup() {
		new OscSceneModulator(this, 8888);
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		timeStep += 0.01;
		scramble += scrambleSpeed;
		noise1Pos += noiseSpeed1;
		noise2Pos += noiseSpeed2;

		graphics.background(0);
		graphics.stroke(255);
		graphics.strokeWeight(lineThickness);

		for (int j = 0; j < numPoints; j++) {
			PVector p = PVector.add(
					PVector.mult(position3(j), interpolation),
					PVector.mult(position2(j), (1 - interpolation)));

			for (int i = 0; i < lineLength; i++) {

				float oldX = p.x;
				float oldY = p.y;
				PVector v = getFlow(p.x, p.y);

				p.x += v.x;
				p.y += v.y + fallSpeed;

				if (Math.sin(i + (noise((float) j) * 100.0) + timeStep * scrollSpeed) - lineSparsity < 0) {
					graphics.line(oldX, oldY, p.x, p.y);
				}
			}
		}
	}

	PVector getFlow(float x, float y) {
		float angle = noise(x / noiseScale, y / noiseScale - noise1Pos, noise2Pos) * PI * 6;
		return PVector.fromAngle(angle).setMag(flowForce);
	}

	PVector position1(int i) {
		return new PVector(((float) WIDTH / numPoints) * i - WIDTH / 2, -HEIGHT / 2);
	}

	PVector position2(int i) {
		float theta = 2 * PI * (float) i / numPoints + timeStep;
		PVector p = new PVector(
				cos(theta) * circleRadius,
				sin(theta) * circleRadius
		);
		return p;
	}

	PVector position3(int i) {
		PVector p = new PVector(
				Scene.getInstance().noise(i, 0, scramble) * WIDTH - WIDTH / 2,
				Scene.getInstance().noise(i, 1, scramble) * HEIGHT - HEIGHT / 2
		);
		return p;
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}
}
