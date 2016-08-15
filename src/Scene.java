import codeanticode.syphon.SyphonServer;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Scene extends PApplet implements OscEventListener {
	// TODO: set up size logic.
	// TODO: set up syphon server. decide if subclass is allowed to draw
	// to both syphon canvas and local canvas.
	private SyphonServer server;
	private Map<String, Field> modulationTargets;

	protected PGraphics local;
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
		size(500, 500, P3D);
		PJOGL.profile = 1;
	}

	public final void setup() {
		local = createGraphics(width, height);
		remote = createGraphics(width, height);
		server = new SyphonServer(this, "my server");

		doSetup();
	}

	protected abstract void doSetup();

	public final void draw() {
		local.beginDraw();
		remote.beginDraw();

		doDraw();

		local.endDraw();
		remote.endDraw();

		getGraphics().image(local, 0, 0);
		server.sendImage(remote);
	}

	protected abstract void doDraw();

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
}
