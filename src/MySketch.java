import processing.core.PApplet;
import processing.core.PGraphics;

public class MySketch extends Scene {
	@Scene.Parameter(pattern = "/thickness")
	float thickness;




	@Override
	public void doSetup() {
		System.out.println(thickness);
	}

	@Override
	public void doDraw(float mouseX, float mouseY) {
		remote.background(0);

		remote.fill(255);
		remote.ellipse(WIDTH / 2, HEIGHT / 2, thickness * 100, thickness * 100);
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {


	}
}
