package sketches;

import common.color.SmoothColorTheme;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.remote.VDMXWriter;
import toxi.color.*;

import static java.lang.Math.PI;

public class Flows extends Scene {
  public static void main(String[] args) {
		main("sketches.Flows");
	}

	@Mod(min = 0, max = 20)
	public float flowForce = 2;

	@Mod(min = 200, max = 1000)
	public float noiseScale = 500;

	@Mod(min = -0.05f, max = 0.05f)
	public float noiseSpeed1 = 0.0f;

	@Mod(min = -0.05f, max = 0.05f)
	public float noiseSpeed2 = 0.0f;

	@Mod(min = 10, max = 50)
	public float lineLength = 20;

	@Mod(min = 0, max = 100)
	public float scrollSpeed = 1;

	@Mod(min = 0, max = 20)
	public float fallSpeed = 0;

	@Mod(min = -1, max = 1)
	public float lineSparsity = 1;

	@Mod(min = 1, max = 10)
	public float lineThickness = 4;

	@Mod(min = 0, max = 1)
	public float interpolation = 0f;

	@Mod(min = 0, max = 500)
	public float circleRadius = 100;

	@Mod(min = 1, max = 1000)
	public int numPoints = 50;

	@Mod(min = 0, max = 0.01f)
	public float scrambleSpeed = 0.01f;

	@Mod(min = 1, max = 20)
	public float colorSpread = 20;

  @Mod(min = 0, max = 6.283185307179586f)
	public float colorStart = 0;

  @Mod(min = 0, max = 0.1f)
  public float colorSpeed = 0;


  float timeStep;
	float scramble = 0;
	float noise1Pos = 0;
	float noise2Pos = 0;
  private float colorPos = 0;

  SmoothColorTheme colors;

	@Override
	public void doSetup() {
		OscRemoteControl remote = new OscRemoteControl(this, 12008);
		set2D();
		VDMXWriter.exportVDMXJson("flows", remote.getTargetMap(), remote.getPort());

		colors = new SmoothColorTheme(ColorRange.FRESH, 10, 100);
	}

	@Mod
  public void switchColors() {
	  colors = new SmoothColorTheme(ColorRange.BRIGHT, 10, 100);
  }

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		timeStep += 0.01;
		scramble += scrambleSpeed;
		noise1Pos += noiseSpeed1;
		noise2Pos += noiseSpeed2;
		colorPos += colorSpeed;

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
					graphics.stroke(colors.getColor(i / colorSpread + colorStart + colorPos).toARGB());
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
		int cellSize = 100;
		int cols = WIDTH / cellSize;
		float x = i % cols * cellSize - WIDTH / 2;
		float y = i / cols * cellSize - HEIGHT / 2;
		PVector p = new PVector(x, y);

		return p;
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}
}
