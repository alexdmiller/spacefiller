import processing.core.PApplet;

public class MySketch extends Scene {
	@Scene.Parameter(pattern = "/thickness")
	float thickness;

	public void doSetup() {
		System.out.println(thickness);
	}

	public void doDraw() {
		remote.background(0);
		remote.fill(255);
		remote.ellipse(width / 2, height / 2, thickness * 100, thickness * 100);
	}

	public static void main(String[] args) {
		PApplet.main("MySketch");
	}
}
