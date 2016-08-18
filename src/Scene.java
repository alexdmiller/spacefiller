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
import java.util.HashMap;
import java.util.Map;

// TODO: how to handle ports?
// TODO: how to handle scenes?

public abstract class Scene extends PApplet implements OscEventListener {
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final float LOCAL_WINDOW_SCALE = 0.5f;

	// to both syphon canvas and local canvas.
	private SyphonServer server;
	private Map<String, Field> modulationTargets;

	protected PGraphics canvas;

	protected OscP5 oscP5;

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

	protected abstract void doSetup();

	public final void draw() {
		canvas.beginDraw();
		doDraw(mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
		canvas.endDraw();

		getGraphics().image(canvas, 0, 0, width, height);

		getGraphics().textSize(24);
		getGraphics().text(frameRate, 10, 30);

		server.sendImage(canvas);
	}

	protected abstract void doDraw(float mouseX, float mouseY);

	public final void mousePressed() {
		doMousePressed(mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
	}

	protected abstract void doMousePressed(float mouseX, float mouseY);

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
