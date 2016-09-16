package scenes;

import boids.*;
import boids.behaviors.*;
import boids.renderers.*;
import boids.tools.*;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worms extends Scene implements EntityEventListener {
	private static final char NEXT_RENDERER_KEY = 'n';
	private static final char FLIP_MAGNETS_KEY = 'm';
	private static final char SAVE_KEY = 's';

	private static final int SAVE_CATEGORY_COUNT = 10;
	private static final int SAVE_PER_CATEGORY = 10;

	public static void main(String[] args) {
		main("scenes.Worms");
	}

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

	@Mod(min = 0, max = 50, defaultValue = 1)
	public float strokeWidth = 1;

	@Mod
	public MeshLikeFlockRenderer meshLikeFlockRenderer;

	private MagnetBehavior magnets;

	private DebugFlockRenderer debugRenderer;
	private FlockRenderer[] flockRenderers;
	private int currentRendererIndex = 0;
	private int currentSaveIndex = 0;
	private int currentSaveCategoryIndex = 0;
	private byte[][][] fileCache;

	@Override
	public void doSetup() {
		fileCache = new byte[SAVE_CATEGORY_COUNT][SAVE_PER_CATEGORY][];

		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);
		flock.addEntityEventListener(this);

		debugRenderer = new DebugFlockRenderer(flock);

		meshLikeFlockRenderer = new MeshLikeFlockRenderer(flock);

		flockRenderers = new FlockRenderer[] {
				new BoidFlockRenderer(flock, PointBoidRenderer.class),
				new BoidFlockRenderer(flock, WormBoidRenderer.class),
				new MeshFlockRenderer(flock),
				new VoronoiFlockRenderer(flock),
				meshLikeFlockRenderer
		};

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

		fillCacheFromFiles();

		loadSave();

		new OscSceneModulator(this, 12000);

	}

	@Mod(min = 0, max = 9)
	public void setSaveCategory(int categoryIndex) {
		this.currentSaveCategoryIndex = categoryIndex;
		loadSave();
	}

	@Mod(min = 0, max = 9)
	public void setSaveIndex(int saveIndex) {
		this.currentSaveIndex = saveIndex;
		loadSave();
	}

	@Mod(min = 0, max = 4)
	public void setRenderer(int renderer) {
		renderer = Math.min(Math.max(renderer, 0), flockRenderers.length-1);
		flockRenderers[currentRendererIndex].clear();
		currentRendererIndex = renderer;
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		flock.step();

		graphics.strokeWeight(strokeWidth);
		graphics.stroke(255);
		flockRenderers[currentRendererIndex].render(graphics);
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
			setSaveIndex(Integer.parseInt(String.valueOf(key)));
		} catch (NumberFormatException e) { }
		if (key == FLIP_MAGNETS_KEY) {
			magnets.setForceMultiplier(magnets.getForceMultiplier() * -1);
		} else if (key == NEXT_RENDERER_KEY) {
			setRenderer((currentRendererIndex + 1) % flockRenderers.length);
		} else if (key == SAVE_KEY) {
			flushCacheToFiles();
		}
	}

	private void loadSave() {
		Flock save;
		try {
			if (currentSaveIndex < fileCache.length) {
				byte[] bytes = fileCache[currentSaveCategoryIndex][currentSaveIndex];
				if (bytes != null) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					save = (Flock) objectInputStream.readObject();
					objectInputStream.close();
					flock.copyEntitiesFrom(save);
				} else {
					flock.clearEntities();
				}
			} else {
				flock.clearEntities();
			}
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
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(flock);
			objectOutputStream.close();

			fileCache[currentSaveCategoryIndex][currentSaveIndex] = outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillCacheFromFiles() {
		Path dir = Paths.get("./");
		Pattern pattern = Pattern.compile("([0-9]*)-([0-9]*).ser");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.ser")) {
			for (Path filePath : stream) {
				Matcher matcher = pattern.matcher(filePath.getFileName().toString());
				matcher.find();
				int categoryIndex = Integer.parseInt(matcher.group(1));
				int saveIndex = Integer.parseInt(matcher.group(2));
				fileCache[categoryIndex][saveIndex] = Files.readAllBytes(filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void flushCacheToFiles() {
		for (int categoryIndex = 0; categoryIndex < fileCache.length; categoryIndex++) {
			byte[][] category = fileCache[categoryIndex];
			for (int saveIndex = 0; saveIndex < category.length; saveIndex++) {
				byte[] save = category[saveIndex];
				if (save != null) {
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(categoryIndex + "-" + saveIndex + ".ser");
						fileOutputStream.write(save);
						fileOutputStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
