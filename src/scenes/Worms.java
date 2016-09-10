package scenes;

import boids.*;
import boids.behaviors.*;
import boids.renderers.*;
import boids.tools.*;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;

import java.io.*;

public class Worms extends Scene implements EntityEventListener {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private static final char FLIP_MAGNETS_KEY = 'm';

	@Mod
	public FlockBehavior flockingBehavior;

	@Mod
	public FlowFieldBehavior flowField;

	@Mod
	public Flock flock;

	@Mod
	public FollowPathBehavior path;

	@Mod
	public EmitBehavior emitters;

	@Mod
	public WiggleBehavior wiggleBehavior;

	private MagnetBehavior magnets;

	private DebugFlockRenderer debugRenderer;
	private BoidFlockRenderer flockRenderer;
	private int currentSaveIndex = 1;

	@Override
	public void doSetup() {
		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);
		flock.addEntityEventListener(this);

		debugRenderer = new DebugFlockRenderer(flock);
		flockRenderer = new BoidFlockRenderer(flock, WormBoidRenderer.class);

		flockingBehavior = new FlockBehavior();
		flock.addBehavior(flockingBehavior);

		wiggleBehavior = new WiggleBehavior();
		flock.addBehavior(wiggleBehavior);

		magnets = new MagnetBehavior(10);
		flock.addBehavior(magnets);

		path = new FollowPathBehavior(1);
		flock.addBehavior(path);


		emitters = new EmitBehavior();
		flock.addBehavior(emitters);

		flowField = new FlowFieldBehavior();
		flock.addBehavior(flowField);

		addSceneTool(new PointEmitterTool(flock));
		addSceneTool(new LineEmitterTool(flock));
		addSceneTool(new PathTool(flock));
		addSceneTool(new MagnetTool(flock));
		addSceneTool(new FlowFieldTool(flock));

		loadSave(currentSaveIndex);

		new OscSceneModulator(this, 12000);
	}

	@Mod(min = 0, max = 9)
	public void setSave(int save) {
		loadSave(save);
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		flock.step();
		flockRenderer.render(graphics);
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {
		debugRenderer.render(graphics);

		graphics.stroke(255);
		graphics.textSize(24);
		graphics.text(currentSaveIndex, 200, 100);
	}

	public void doKeyPressed() {
		try {
			loadSave(Integer.parseInt(String.valueOf(key)));
		} catch (NumberFormatException e) { }
		if (key == FLIP_MAGNETS_KEY) {
			magnets.setForceMultiplier(magnets.getForceMultiplier() * -1);
		}
	}

	private void loadSave(int currentSaveIndex) {
		this.currentSaveIndex = currentSaveIndex;
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
