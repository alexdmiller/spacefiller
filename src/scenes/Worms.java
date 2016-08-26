package scenes;

import boids.*;
import boids.behaviors.*;
import boids.emitter.LineEmitter;
import boids.renderers.*;
import boids.emitter.PointEmitter;
import boids.tools.LineEmitterTool;
import boids.tools.MagnetTool;
import boids.tools.PathTool;
import boids.tools.PointEmitterTool;
import processing.core.PGraphics;

public class Worms extends Scene {
	public static void main(String[] args) {
		main("scenes.Worms");
	}


	public static final char FLIP_MAGNETS_KEY = 'm';
	public static final char CLEAR_KEY = 'c';

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;
	private EmitBehavior emitters;
	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;

	@Override
	public void doSetup() {
		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);

		debugRenderer = new DebugFlockRenderer(flock);
		flockRenderer = new BoidFlockRenderer(flock, WormBoidRenderer.class);

		FlockBehavior flockingBehavior = new FlockBehavior(0.5f, 50, 50, 20, 5, 1, 0.2f);
		flock.addBehavior(flockingBehavior);

		WiggleBehavior wiggleBehavior = new WiggleBehavior(1f, 10);
		flock.addBehavior(wiggleBehavior);

		magnets = new MagnetBehavior(300, 5);
		flock.addBehavior(magnets);

		path = new FollowPathBehavior(50, 10);
		flock.addBehavior(path);

		emitters = new EmitBehavior();
		flock.addBehavior(emitters);

		addSceneTool(new PointEmitterTool(emitters));
		addSceneTool(new LineEmitterTool(emitters));
		addSceneTool(new PathTool(path));
		addSceneTool(new MagnetTool(magnets));
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		flock.step();
		flockRenderer.render(graphics);
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {
		debugRenderer.render(graphics);
	}

	public void doKeyPressed() {
		if (key == FLIP_MAGNETS_KEY) {
			magnets.setForceMultiplier(magnets.getForceMultiplier() * -1);
		} else if (key == CLEAR_KEY) {
			flock.clearBoids();
			emitters.clearEmitters();
			path.clearPoints();
			magnets.clearMagnets();
		}

	}
}
