package scenes;

import boids.*;
import boids.behaviors.FlockBehavior;
import boids.behaviors.FollowPathBehavior;
import boids.behaviors.MagnetBehavior;
import boids.behaviors.WiggleBehavior;
import boids.emitter.LineEmitter;
import boids.renderers.*;
import boids.emitter.PointEmitter;
import boids.tools.LineEmitterTool;
import boids.tools.PathTool;
import boids.tools.PointEmitterTool;

public class Worms extends Scene {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	public static final char PLAY_KEY = 'p';
	public static final char EDIT_MODE_KEY = 'e';
	public static final char NEXT_TOOL_KEY = 'n';
	public static final char PREV_TOOL_KEY = 'p';
	public static final char CLEAR_KEY = 'c';

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;

	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;

	private int currentToolIndex;
	private SceneTool[] tools;

	private boolean editing = true;
	private boolean playing = false;

	@Override
	public void doSetup() {
		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);

		debugRenderer = new DebugFlockRenderer(flock, canvas);
		flockRenderer = new BoidFlockRenderer(flock, canvas, WormBoidRenderer.class);

		FlockBehavior flockingBehavior = new FlockBehavior(0.5f, 50, 50, 20, 5, 1, 0.2f);
		flock.addBehavior(flockingBehavior);

		WiggleBehavior wiggleBehavior = new WiggleBehavior(0.2f, 10);
		flock.addBehavior(wiggleBehavior);

		magnets = new MagnetBehavior(-10, 300, 5, 5);
		flock.addBehavior(magnets);

		path = new FollowPathBehavior(50, 1);
		flock.addBehavior(path);

//		LineEmitter e1 = new LineEmitter(100, 100, WIDTH - 100, 100, 0.5f);
//		e1.setInitialVelocity(0, 2);
//		flock.addEmitter(e1);
//
//		LineEmitter e2 = new LineEmitter(100, HEIGHT - 101, WIDTH - 100, HEIGHT - 101, 0.5f);
//		e2.setInitialVelocity(0, -2);
//		flock.addEmitter(e2);

//		PointEmitter e = new PointEmitter(WIDTH / 2, 200, 1);
//		e.setInitialVelocity(0, 1);
//		flock.addEmitter(e);

		currentToolIndex = 0;

		tools = new SceneTool[] {
			new PointEmitterTool(flock),
			new LineEmitterTool(flock),
			new PathTool(path)
		};
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		if (playing) {
			flock.step(elapsedMillis);
		}

		if (editing) {
			debugRenderer.render();
			canvas.pushMatrix();
			canvas.translate(mouseX, mouseY);
			canvas.stroke(255);
			canvas.strokeWeight(2);
			canvas.line(-10, 0, 10, 0);
			canvas.line(0, -10, 0, 10);
			canvas.popMatrix();
			canvas.text(tools[currentToolIndex].toString(), 10, HEIGHT - 20);
		}

		flockRenderer.render();
		tools[currentToolIndex].render();
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		tools[currentToolIndex].mousePressed(mouseX, mouseY);
	}

	public void keyPressed() {
		if (key == EDIT_MODE_KEY) {
			editing = !editing;
		} else if (key == PLAY_KEY) {
			playing = !playing;
		} else if (key == NEXT_TOOL_KEY) {
			currentToolIndex = (currentToolIndex + 1) % tools.length;
		} else if (key == PREV_TOOL_KEY) {
			currentToolIndex = (currentToolIndex - 1) % tools.length;
		} else if (key == CLEAR_KEY) {
			flock.clearBoids();
			flock.clearEmitters();
			path.clearPoints();
			magnets.clearMagnets();
		} if (editing) {
			tools[currentToolIndex].keyDown(key);
		}

		// magnets.setRepelling(!magnets.isRepelling());
	}
}
