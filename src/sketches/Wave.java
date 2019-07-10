package sketches;

import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;

public class Wave extends Scene {
	private static final float WAVE_INCREMENTS = 100;

	@Mod(min = 0, max = 1)
	public float waveSize = 1;

	@Mod(min = 0, max = 40, defaultValue = 5)
	public int numLines = 5;

	private float velocity = 0;
	private int note = 0;

	public static void main(String[] args) {
		main("sketches.Wave");
	}

	private float step;

	@Override
	public void doSetup() {

		//new OscRemoteControl(this, 12000);
	}

	@Mod(address = "/Velocity1")
	public void velocityMessage(int num) {
		if (num > 0) {
			velocity = num;
		}
	}

	@Mod(address = "/Note1")
	public void noteMessage(int num) {
		note = num;
	}

	private float function(float x, float t, float a) {
		return (float) (Math.sin(x * a + t) * 500);
	}

	private float envelope(float x) {
		return (float) (Math.sin(x /2));
	}

	private void drawWave(PGraphics graphics, float t, float a) {
		graphics.beginShape();
		for (float theta = 0; theta < 2 * Math.PI; theta += Math.PI / WAVE_INCREMENTS) {
			float x = map(theta, 0, (float) (Math.PI * 2), 0, WIDTH - 10);
			float y = function(theta, t, note) * envelope(theta) * (velocity / 100f);
			graphics.vertex(x, y);
		}
		graphics.noFill();
		graphics.stroke(255);
		graphics.endShape();
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		step += note / 1000f;

		if (velocity > 0) {
			velocity -= 2;
		}

		graphics.pushMatrix();
		graphics.translate(0, HEIGHT / 2);
		for (int i = 0; i < numLines; i++) {
			drawWave(graphics, step, i);
		}
		graphics.popMatrix();
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}
}
