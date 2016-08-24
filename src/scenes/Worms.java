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
import boids.tools.MagnetTool;
import boids.tools.PathTool;
import boids.tools.PointEmitterTool;
import processing.core.PGraphics;

public class Worms extends Scene {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	public static final char NEXT_TOOL_KEY = ',';
	public static final char PREV_TOOL_KEY = '.';
	public static final char FLIP_MAGNETS_KEY = 'm';
	public static final char CLEAR_KEY = 'c';

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;
	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;
	// TODO: refactor tools up into Scene
	private int currentToolIndex;
	private SceneTool[] tools;

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

		currentToolIndex = 0;

		tools = new SceneTool[] {
			new PointEmitterTool(flock),
			new LineEmitterTool(flock),
			new PathTool(path),
			new MagnetTool(magnets)
		};
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		flock.step();
		flockRenderer.render(graphics);
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {
		tools[currentToolIndex].render(graphics);

		debugRenderer.render(graphics);
		graphics.pushMatrix();
		graphics.translate(mouseX, mouseY);
		graphics.stroke(255);
		graphics.strokeWeight(2);
		graphics.line(-10, 0, 10, 0);
		graphics.line(0, -10, 0, 10);
		graphics.popMatrix();
		graphics.textSize(40);
		graphics.fill(0, 255, 255);
		graphics.text(tools[currentToolIndex].toString(), 10, HEIGHT - 20);
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		tools[currentToolIndex].mousePressed(mouseX, mouseY);
	}

	public void doKeyPressed() {
		if (key == NEXT_TOOL_KEY) {
			currentToolIndex = (currentToolIndex + 1) % tools.length;
		} else if (key == PREV_TOOL_KEY) {
			currentToolIndex = (currentToolIndex - 1) % tools.length;
		} else if (key == FLIP_MAGNETS_KEY) {
			magnets.setForceMultiplier(magnets.getForceMultiplier() * -1);
		} else if (key == CLEAR_KEY) {
			flock.clearBoids();
			flock.clearEmitters();
			path.clearPoints();
			magnets.clearMagnets();
		}

		tools[currentToolIndex].keyDown(key);
	}
}
