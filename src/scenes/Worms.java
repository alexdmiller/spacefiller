package scenes;

import boids.*;
import boids.behaviors.*;
import boids.renderers.*;
import boids.tools.LineEmitterTool;
import boids.tools.MagnetTool;
import boids.tools.PathTool;
import boids.tools.PointEmitterTool;
import processing.core.PGraphics;

import java.io.*;

public class Worms extends Scene implements EntityEventListener {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private static final char SAVE_KEY = 's';
	private static final char LOAD_KEY = 'l';
	private static final char FLIP_MAGNETS_KEY = 'm';
	private static final char CLEAR_KEY = 'c';

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;
	private EmitBehavior emitters;
	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;
	private int currentSaveIndex = 0;

	@Override
	public void doSetup() {
		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);
		flock.addEntityEventListener(this);

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

		addSceneTool(new PointEmitterTool(flock));
		addSceneTool(new LineEmitterTool(flock));
		addSceneTool(new PathTool(flock));
		addSceneTool(new MagnetTool(flock));
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
		try {
			currentSaveIndex = Integer.parseInt(String.valueOf(key));
			Flock save;
			try {
				FileInputStream fileInputStream = new FileInputStream("save" + currentSaveIndex + ".ser");
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				save = (Flock) objectInputStream.readObject();
				objectInputStream.close();
				flock.copyEntitiesFrom(save);
			} catch (FileNotFoundException e) {
				flock.clearEntities();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {

		}

		if (key == FLIP_MAGNETS_KEY) {
			magnets.setForceMultiplier(magnets.getForceMultiplier() * -1);
		} else if (key == CLEAR_KEY) {
			flock.clearEntities();
		}
	}

	public Flock getFlock() {
		return flock;
	}

	@Override
	public void entitiesUpdated() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("save" + currentSaveIndex + ".ser");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(flock);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
