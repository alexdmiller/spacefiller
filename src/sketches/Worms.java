package sketches;

import boids.*;
import boids.behaviors.*;
import boids.renderers.*;
import boids.tools.*;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import spacefiller.remote.VDMXWriter;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worms extends Scene implements EntityEventListener {
	private static final char NEXT_RENDERER_KEY = 'n';
	private static final char FLIP_MAGNETS_KEY = 'm';
	private static final char SAVE_KEY = 's';

	private static final int SAVE_CATEGORY_COUNT = 10;
	private static final int SAVE_PER_CATEGORY = 10;

	public static void main(String[] args) {
		main("sketches.Worms");
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

	@Mod
	public ContourSpaceFlockRenderer contourSpaceFlockRenderer;

	private MagnetBehavior magnets;

	private DebugFlockRenderer debugRenderer;
	private FlockRenderer[] flockRenderers;
	private int currentRendererIndex = 0;
	private int currentSaveIndex = 0;
	private int currentSaveCategoryIndex = 0;
	private byte[][][] fileCache;

	@Override
	public void doSetup() {
		set2D();

		fileCache = new byte[SAVE_CATEGORY_COUNT][SAVE_PER_CATEGORY][];

		flock = new Flock((float) WIDTH, (float) HEIGHT, 0);
		flock.addEntityEventListener(this);

		debugRenderer = new DebugFlockRenderer(flock);
		contourSpaceFlockRenderer = new ContourSpaceFlockRenderer(flock, WIDTH, HEIGHT, 50);

		meshLikeFlockRenderer = new MeshLikeFlockRenderer(flock);

		flockRenderers = new FlockRenderer[] {
				contourSpaceFlockRenderer,
				new BoidFlockRenderer(flock, WormBoidRenderer.class),
				new MeshFlockRenderer(flock),
				new VoronoiFlockRenderer(flock),
				meshLikeFlockRenderer,
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

		OscRemoteControl remote = new OscRemoteControl(12002);
		remote.autoRoute(this);
		System.out.println(remote.availableAddresses());
		// VDMXWriter.exportVDMXJson("worms", remote.getTargetNodes(), remote.getPort());
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

	@Mod
	public void setSaveIndex1() {
		setSaveIndex(0);
	}

	@Mod
	public void setSaveIndex2() {
		setSaveIndex(1);
	}

	@Mod
	public void setSaveIndex3() {
		setSaveIndex(2);
	}

	@Mod
	public void setSaveIndex4() {
		setSaveIndex(3);
	}

	@Mod
	public void setSaveIndex5() {
		setSaveIndex(4);
	}

	@Mod
	public void setSaveIndex6() {
		setSaveIndex(5);
	}

	@Mod
	public void setSaveIndex7() {
		setSaveIndex(6);
	}

	@Mod
	public void setSaveIndex8() {
		setSaveIndex(7);
	}

	@Mod(min = 0, max = 4)
	public void setRenderer(float renderer) {
		renderer = Math.min(Math.max(renderer, 0), flockRenderers.length - 1);
		flockRenderers[currentRendererIndex].clear();
		currentRendererIndex = (int) Math.floor(renderer);
	}

	@Mod
	public void setRenderer1() {
		setRenderer(0);
	}

	@Mod
	public void setRenderer2() {
		setRenderer(1);
	}

	@Mod
	public void setRenderer3() {
		setRenderer(2);
	}

	@Mod
	public void setRenderer4() {
		setRenderer(3);
	}

	@Mod
	public void setRenderer5() {
		setRenderer(4);
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
		graphics.stroke(255);
		graphics.textSize(24);
		graphics.text(currentSaveCategoryIndex + " - " + currentSaveIndex, 200, 100);

		debugRenderer.render(graphics);
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
				if (matcher.find()) {
					int categoryIndex = Integer.parseInt(matcher.group(1));
					int saveIndex = Integer.parseInt(matcher.group(2));
					fileCache[categoryIndex][saveIndex] = Files.readAllBytes(filePath);
				}
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
