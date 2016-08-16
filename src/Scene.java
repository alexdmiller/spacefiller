import codeanticode.syphon.SyphonServer;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;

import java.awt.geom.PathIterator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class Scene extends PApplet implements OscEventListener {
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final float LOCAL_WINDOW_SCALE = 0.5f;

	// TODO: set up size logic.
	// TODO: set up syphon server. decide if subclass is allowed to draw
	// to both syphon canvas and local canvas.
	private SyphonServer server;
	private Map<String, Field> modulationTargets;

	protected PGraphics remote;

	protected OscP5 oscP5;

	public Scene() {
		modulationTargets = new HashMap<>();

		for (Field field : getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Parameter.class)) {
				Parameter parameter = field.getAnnotation(Parameter.class);
				modulationTargets.put(parameter.pattern(), field);
			}
		}

		oscP5 = new OscP5(this, 12000);
		oscP5.addListener(this);
	}

	public void settings() {
		size((int) (WIDTH * LOCAL_WINDOW_SCALE), (int) (HEIGHT * LOCAL_WINDOW_SCALE), P3D);
		PJOGL.profile = 1;
	}

	public final void setup() {
		remote = createGraphics(WIDTH, HEIGHT);
		server = new SyphonServer(this, "my server");
		frameRate(60);
		doSetup();
	}

	protected abstract void doSetup();

	public final void draw() {
		remote.beginDraw();
		doDraw(mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
		remote.endDraw();

		getGraphics().image(remote, 0, 0, width, height);

		getGraphics().textSize(24);
		getGraphics().text(frameRate, 10, 30);

		server.sendImage(remote);
	}

	protected abstract void doDraw(float mouseX, float mouseY);

	public final void mousePressed() {
		doMousePressed(mouseX / LOCAL_WINDOW_SCALE, mouseY / LOCAL_WINDOW_SCALE);
	}

	protected abstract void doMousePressed(float mouseX, float mouseY);

	@Override
	public void oscEvent(OscMessage oscMessage) {
		Field target = modulationTargets.get(oscMessage.addrPattern());
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
	@interface Parameter {
		// TODO: use variable name by default
		String pattern();
	}

	public static void main(String[] args) {
		PApplet.main("MySketch");
	}
}
