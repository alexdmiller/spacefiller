package scenes;

import codeanticode.syphon.SyphonServer;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.opengl.PJOGL;

import javax.json.*;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: how to handle ports?
// TODO: how to handle scenes?

public class Scene extends PApplet implements OscEventListener {
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final float LOCAL_WINDOW_SCALE = 0.5f;
	public static final char PLAY_KEY = ' ';
	public static final char NEXT_TOOL_KEY = ',';
	public static final char PREV_TOOL_KEY = '.';

	private OscP5 oscP5;
	private float lastTime;
	private float elapsedMillis;
	private SyphonServer server;
	private Map<String, Field> modulationTargets;
	private PGraphics canvas;
	private boolean playing;
	private int currentToolIndex;
	private List<SceneTool> tools;

	public Scene() {
		modulationTargets = new HashMap<>();

		for (Field field : getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(ModulationTarget.class)) {
				modulationTargets.put("/" + this.getClass().getName() + "/" + field.getName(), field);
			}
		}

		int port = 12000;
		exportVDMXJson(port);

		oscP5 = new OscP5(this, port);
		oscP5.addListener(this);

		currentToolIndex = 0;
		tools = new ArrayList<>();
	}

	protected void addSceneTool(SceneTool tool) {
		tools.add(tool);
	}

	private void exportVDMXJson(int port) {
		JsonArrayBuilder classes = Json.createArrayBuilder();
		JsonArrayBuilder keys = Json.createArrayBuilder();
		JsonObjectBuilder uiBuilder = Json.createObjectBuilder();

		for (String address : modulationTargets.keySet()) {
			Field field = modulationTargets.get(address);
			ModulationTarget modulationTarget = field.getAnnotation(ModulationTarget.class);

			classes.add("Slider");
			keys.add(field.getName());

			try {
				uiBuilder.add(field.getName(), Json.createObjectBuilder()
					.add("minRange", modulationTarget.min())
					.add("maxRange", modulationTarget.max())
					.add("val", (float) field.get(this))
					.add("label", field.getName())
					.add("publishNorm", false)
					.add("sendEnabled", true)
					.add("sndrs", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
							.add("msgAddress", address)
							.add("normalizeFlag", false)
							.add("floatInvertFlag", false)
							.add("outputPort", port)
							.add("boolInvertFlag", false)
							.add("boolThreshVal", 0.1)
							.add("highIntVal", 100)
							.add("outputIPAddress", "127.0.0.1")
							.add("outputLabel", "localhost:" + port)
							.add("dataSenderType", 2)
							.add("lowIntVal", 0)
							.add("enabled", true)
							.add("intInvertFlag", false)
							.add("senderType", 4))));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		JsonObject layout = Json.createObjectBuilder()
			.add("classArray", classes)
			.add("keyArray", keys)
			.add("uiBuilder", uiBuilder)
			.build();

		File file = new File("test.json");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		JsonWriter writer = Json.createWriter(out);
		writer.write(layout);
	}

	public void settings() {
		size((int) (WIDTH * LOCAL_WINDOW_SCALE), (int) (HEIGHT * LOCAL_WINDOW_SCALE), P3D);
		PJOGL.profile = 1;
	}

	public final void setup() {
		canvas = createGraphics(WIDTH, HEIGHT, P3D);
		server = new SyphonServer(this, "my server");
		frameRate(60);
		doSetup();
	}

	protected void doSetup() {}

	public final void draw() {
		background(0);

		float currentTime = millis();
		elapsedMillis = currentTime - lastTime;

		if (playing) {
			canvas.beginDraw();
			canvas.background(0);
			drawCanvas(canvas, mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
			canvas.endDraw();
		}

		image(canvas, 0, 0, width, height);

		pushMatrix();
		scale(LOCAL_WINDOW_SCALE);
		tools.get(currentToolIndex).render(getGraphics(), mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE, mousePressed);
		drawControlPanel(getGraphics(), mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
		popMatrix();

		textSize(24);
		fill(255);
		text(frameRate, 10, 30);
		text(tools.get(currentToolIndex).toString(), 10, height - 20);

		pushMatrix();
		translate(width / 2, height - 20);
		noStroke();
		if (playing) {
			fill(0, 255, 0);
			triangle(-10, -10, 10, 0, -10, 10);
		} else {
			fill(255, 0, 0);
			rectMode(CORNER);
			rect(-10, -10, 20, 20);
		}
		popMatrix();

		server.sendImage(canvas);

		lastTime = currentTime;
	}

	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {}
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {}

	public final void mousePressed() {
		tools.get(currentToolIndex).mousePressed(mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
	}

	public final void keyPressed() {
		if (key == PLAY_KEY) {
			playing = !playing;
		} else if (key == NEXT_TOOL_KEY) {
			currentToolIndex = (currentToolIndex + 1) % tools.size();
		} else if (key == PREV_TOOL_KEY) {
			currentToolIndex = (currentToolIndex - 1) % tools.size();
		}

		tools.get(currentToolIndex).keyDown(key);

		doKeyPressed();
	}

	protected void doKeyPressed() {}

	@Override
	public void oscEvent(OscMessage oscMessage) {
		Field target = modulationTargets.get(oscMessage.addrPattern());
		if (target == null) {
			System.out.println("Just received unknown modulation target: " + oscMessage.addrPattern());
			return;
		}

		try {
			// TODO: probably want to handle ints and booleans?
			target.setFloat(this, oscMessage.get(0).floatValue());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void oscStatus(OscStatus oscStatus) {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface ModulationTarget {
		float min() default 0;
		float max() default 1;
	}
}
