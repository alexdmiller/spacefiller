package scenes;

import codeanticode.syphon.SyphonServer;
import lusio.SceneGenerator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PJOGL;

import java.util.ArrayList;
import java.util.List;

public class Lusio extends PApplet {
	private static PApplet instance;

	public static void main(String[] args) {
		main("scenes.Lusio");
	}

	private SyphonServer server;
	private PGraphics canvas;
	private List<SceneGenerator> generators;
	
	public Lusio() {
		Lusio.instance = this;
		generators = new ArrayList<>();
	}

	public void settings() {
		size(1920, 1080, P3D);
		PJOGL.profile = 1;
	}

	public final void setup() {
		canvas = createGraphics(1920, 1080, P3D);
		server = new SyphonServer(this, this.getClass().getName());
	}
	
	public final void draw() {
		canvas.beginDraw();
		
		canvas.background(0);
		canvas.translate(width / 2, height / 2);
		canvas.noFill();
		canvas.stroke(255);
		canvas.box(100);
		
		image(canvas, 0, 0);
		canvas.endDraw();
	}
}
