package sketches;

import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

import java.util.Collections;

public class Contours extends Scene {
	@Mod(min = 0, max = 500, defaultValue = 0)
	public float noiseAmplitude = 0;

	@Mod(min = 0, max = 10, defaultValue = 2)
	public float noiseResolution = 2;

	@Mod(min = -10, max = 10, defaultValue = 1)
	public float noiseXSpeed = 1;

	@Mod(min = -10, max = 10, defaultValue = 1)
	public float noiseYSpeed = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float oscX = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float oscXT = 1;

	@Mod(min = 0, max = 100, defaultValue = 50)
	public float oscXAmp = 50;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float oscY = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float oscYT = 1;

	@Mod(min = 0, max = 50, defaultValue = 50)
	public float oscYAmp = 50;

	@Mod(min = -.05f, max = 0.05f, defaultValue = 0.01f)
	public float updateSpeed = 0.01f;

	@Mod(min = -.05f, max = 0.05f, defaultValue = 0)
	public float rotationSpeed = 0;

	@Mod(min = 1, max = 100, defaultValue = 50)
	public int gridSize = 50;

	@Mod(min = 5, max = 100, defaultValue = 20)
	public float cellSize = 20;

	@Mod(min = 2, max = 50, defaultValue = 5)
	public float heightIncrements = 5;

	@Mod(min = 1, max = 1000, defaultValue = 200)
	public float heightRange = 200;

	@Mod(min = 1, max = 20, defaultValue = 2)
	public float thickness = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float spacing = 1;

	@Mod(min = 0, max = 255, defaultValue = 50)
	public int colorRange = 50;

	private float timeStep = 0;
	private float rotation = 0;

	public static void main(String[] args) {
		main("sketches.Contours");
	}

	@Override
	public void doSetup() {
		new OscRemoteControl(this, 12001);
	}

	@Override
	protected void drawCanvas(PGraphics canvas, float mouseX, float mouseY) {
		float[][] heightMap = produceGrid(timeStep, gridSize, gridSize);

		// canvas.translate(WIDTH / 2, HEIGHT / 2);
		canvas.scale(2);
		canvas.rotateX(PI / 3);
		canvas.rotateZ(rotation);
		canvas.translate(-gridSize * cellSize / 2, -gridSize * cellSize / 2);

		canvas.noFill();
		canvas.stroke(255);
		canvas.strokeWeight(thickness);
		// canvas.colorMode(PConstants.HSB);

		for (float i = 0; i < heightRange; i += heightIncrements) {
			float sampleHeight = i - heightRange/2;
			canvas.stroke(i / heightRange * colorRange, 255, 255);
			drawGridPlaneIntersection(heightMap, sampleHeight, sampleHeight * spacing, cellSize, canvas);
		}

		timeStep += updateSpeed;
		rotation += rotationSpeed;
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}

	float[][] produceGrid(float t, int rows, int cols) {
		float[][] grid = new float[rows][cols];
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				float x = (float) r / rows * PI;
				float y = (float) c / cols * PI;
				grid[r][c] = (float) (noise(x * noiseResolution + t * noiseXSpeed, y * noiseResolution + t * noiseYSpeed) * noiseAmplitude
										+ (
											Math.sin(x * oscX + t * oscXT) * oscXAmp +
											Math.sin(y * oscY + t * oscYT) * oscYAmp
								)) - 100;
			}
		}
		return grid;
	}

	void drawGrid(float[][] heightMap, float gridSize, PGraphics canvas) {
		for (int r = 0; r < heightMap.length - 1; r++) {
			for (int c = 0; c < heightMap[r].length - 1; c++) {
				PVector p1 = new PVector(c * gridSize, r * gridSize, heightMap[r][c]);
				PVector p2 = new PVector((c + 1) * gridSize, r * gridSize, heightMap[r][c + 1]);
				PVector p3 = new PVector((c + 1) * gridSize, (r + 1) * gridSize, heightMap[r + 1][c + 1]);
				PVector p4 = new PVector(c * gridSize, (r + 1) * gridSize, heightMap[r + 1][c]);
				PVector m = average(p1, p2, p3, p4);
				vpoint(m, canvas);
			}
		}
	}

	void drawGridPlaneIntersection(float[][] heightMap, float planeHeight, float drawHeight, float gridSize, PGraphics canvas) {
		for (int r = 0; r < heightMap.length - 1; r++) {
			for (int c = 0; c < heightMap[r].length - 1; c++) {
				PVector p1 = new PVector(c * gridSize, r * gridSize, heightMap[r][c]);
				PVector p2 = new PVector((c + 1) * gridSize, r * gridSize, heightMap[r][c + 1]);
				PVector p3 = new PVector((c + 1) * gridSize, (r + 1) * gridSize, heightMap[r + 1][c + 1]);
				PVector p4 = new PVector(c * gridSize, (r + 1) * gridSize, heightMap[r + 1][c]);
				drawRectPlaneIntersection(p1, p2, p3, p4, planeHeight, drawHeight, canvas);
			}
		}
	}

	PVector average(PVector p1, PVector p2, PVector p3, PVector p4) {
		PVector m = new PVector();
		m.add(p1);
		m.add(p2);
		m.add(p3);
		m.add(p4);
		m.div(4);
		return m;
	}

	void drawRectPlaneIntersection(PVector p1, PVector p2, PVector p3, PVector p4, float planeHeight, float drawHeight, PGraphics canvas) {
		PVector m = average(p1, p2, p3, p4);
		drawTrianglePlaneIntersection(p1, p2, m, planeHeight, drawHeight, canvas);
		drawTrianglePlaneIntersection(p2, p3, m, planeHeight, drawHeight,canvas);
		drawTrianglePlaneIntersection(p3, p4, m, planeHeight, drawHeight,canvas);
		drawTrianglePlaneIntersection(p4, p1, m, planeHeight, drawHeight,canvas);
	}

	void drawTrianglePlaneIntersection(PVector p1, PVector p2, PVector p3, float planeHeight, float drawHeight, PGraphics canvas) {
		List<PVector> intersections = new ArrayList();
		intersections.add(intersection(p1, p2, planeHeight, drawHeight));
		intersections.add(intersection(p2, p3, planeHeight, drawHeight));
		intersections.add(intersection(p1, p3, planeHeight, drawHeight));
		intersections.removeAll(Collections.singleton(null));
		if (intersections.size() == 2) {
			PVector l1 = intersections.get(0);
			PVector l2 = intersections.get(1);
			vline(l1, l2, canvas);
		}
	}

	PVector intersection(PVector p1, PVector p2, float planeHeight, float drawHeight) {
		float t = (planeHeight - p1.z) / (p2.z - p1.z);
		float x = (p2.x - p1.x) * t + p1.x;
		float y = (p2.y - p1.y) * t + p1.y;
		if (t < 0 || t > 1) {
			return null;
		} else {
			return new PVector(x, y, drawHeight);
		}
	}

	PVector l(float t, PVector p1, PVector p2) {
		return PVector.add(PVector.mult(PVector.sub(p2, p1), t), p1);
	}

	void vline(PVector p1, PVector p2, PGraphics canvas) {
		canvas.line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	void vpoint(PVector p1, PGraphics canvas) {
		canvas.point(p1.x, p1.y, p1.z);
	}
}
