package scenes;

import boids.*;
import boids.behaviors.FlockBehavior;
import boids.behaviors.FollowPathBehavior;
import boids.behaviors.MagnetBehavior;
import boids.behaviors.WiggleBehavior;
import boids.renderers.*;
import emitter.Emitter;
import emitter.LineEmitter;
import emitter.PointEmitter;
import processing.core.PVector;

public class Worms extends Scene {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;

	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;

	@Override
	public void doSetup() {
		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);

		debugRenderer = new DebugFlockRenderer(flock, canvas);
		flockRenderer = new BoidFlockRenderer(flock, canvas, WormBoidRenderer.class);

		FlockBehavior flockingBehavior = new FlockBehavior(0.5f, 50, 20, 2, 1, 0.5f);
		flock.addBehavior(flockingBehavior);

		WiggleBehavior wiggleBehavior = new WiggleBehavior(1, 10);
		flock.addBehavior(wiggleBehavior);

		magnets = new MagnetBehavior(-10, 300, 5, 5);
		flock.addBehavior(magnets);

		path = new FollowPathBehavior(100, 0.5f);
		flock.addBehavior(path);

//		LineEmitter e1 = new LineEmitter(100, 100, WIDTH - 100, 100, 0.5f);
//		e1.setInitialVelocity(0, 2);
//		flock.addEmitter(e1);
//
//		LineEmitter e2 = new LineEmitter(100, HEIGHT - 101, WIDTH - 100, HEIGHT - 101, 0.5f);
//		e2.setInitialVelocity(0, -2);
//		flock.addEmitter(e2);

		PointEmitter e = new PointEmitter(WIDTH / 2, 200, 1);
		e.setInitialVelocity(0, 1);
		flock.addEmitter(e);
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		flock.step(elapsedMillis);

		debugRenderer.render();
		flockRenderer.render();
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		// magnets.addMagnet(mouseX, mouseY);
		path.addPoint(mouseX, mouseY);
	}

	public void keyPressed() {
		magnets.setRepelling(!magnets.isRepelling());
	}
}
