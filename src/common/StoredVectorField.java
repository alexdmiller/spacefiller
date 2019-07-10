package common;

import processing.core.PApplet;
import spacefiller.remote.Mod;
import particles.Bounds;
import processing.core.PVector;

public class StoredVectorField implements VectorField {
	@Mod(min = 0, max = 0.1f)
	public float noiseScale = 0.1f;

	private PVector[] field;
	private Bounds bounds;
	private int cellSize;
	private PVector origin;

	public StoredVectorField(Bounds bounds, int cellSize) {
		this.bounds = bounds;
		this.cellSize = cellSize;

		if (bounds.getDepth() == 0) {
			field = new PVector[(int) (bounds.getWidth() / cellSize * bounds.getHeight() / cellSize)];
		} else {
			field = new PVector[(int) (bounds.getWidth() / cellSize * bounds.getHeight() / cellSize * bounds.getDepth() / cellSize)];
		}

		for (int i = 0; i < field.length; i++) {
			field[i] = new PVector(0, 0, 0);
		}

		this.origin = new PVector(bounds.getWidth() / 2, bounds.getHeight() / 2, bounds.getDepth() / 2);
	}

	public void setOrigin(PVector origin) {
		this.origin = origin;
	}

	@Override
	public PVector at(float x, float y, float z, float t) {
		int cellX = (int) (x + origin.x) / cellSize;
		int cellY = (int) (y + origin.y) / cellSize;
		int cellZ = (int) (z + origin.z) / cellSize;
		return getCell(cellX, cellY, cellZ);
	}

	public PVector getCell(int cellX, int cellY, int cellZ) {
		int i = cellZ * (int) (bounds.getHeight() / cellSize * bounds.getWidth() / cellSize) + cellY * (int) bounds.getWidth() / cellSize + cellX;
		if (i < field.length && i > 0) {
			return field[i];
		} else {
			return new PVector(0, 0, 0);
		}
	}

	public int getGridWidth() {
		return (int) bounds.getWidth() / cellSize;
	}

	public int getGridHeight() {
		return (int) bounds.getHeight() / cellSize;
	}

	public int getGridDepth() {
		return (int) bounds.getDepth() / cellSize;
	}

	public void zero() {
		for (int i = 0; i < field.length; i++) {
			field[i].set(0, 0, 0);
		}
	}

	public void set(StoredVectorField other) {
		for (int i = 0; i < this.field.length; i++) {
			field[i].set(other.field[i]);
		}
	}

	public int getCellSize() {
		return this.cellSize;
	}

	@Mod
	public void randomizeFlowField() {
		// TODO: why does this depend on Worms?
//		float seed = (float) Math.random() * 100;
//		for (float x = 0; x <= getGridWidth(); x++) {
//			for (float y = 0; y <= getGridHeight(); y++) {
//				float theta = (float) (Worms.getInstance().noise(x, y, seed) * 6 * Math.PI);
//				PVector f = new PVector(
//						(float) Math.cos(theta) * 40,
//						(float) Math.sin(theta) * 40);
//
//				for (float z = 0; z <= getGridDepth(); z++) {
//					getCell((int) x, (int) y, (int) z).set(f);
//				}
//			}
//		}

//		float shift = 0;
//		float noiseScale = 0.5f;
//		for (float x = 0; x <= getGridWidth(); x++) {
//			for (float y = 0; y <= getGridHeight(); y++) {
//				for (float z = 0; z <= getGridDepth(); z++) {
//					float theta = x * 10 + y * 10 + z * 10; // (float) (Scene.getInstance().noise(x * noiseScale, y * noiseScale, z * noiseScale) * 3.141592653589793D * 2.0D);
//
////					float vz = -1f; //(float) (Scene.getInstance().noise(x * noiseScale + shift, y * noiseScale + shift, z * noiseScale + shift) * 2.0D - 1.0D);
////					float angle = (float) (Scene.getInstance().noise(x * noiseScale, y * noiseScale, z * noiseScale) * 3.141592653589793D * 2.0D);
//
////					float vz = (float) (Math.random() * 2.0D - 1.0D);
////					float angle = (float) (Math.random() * 3.141592653589793D * 2.0D);
//
//					PVector f = new PVector(
//							(float) Math.cos(theta),
//							(float) Math.sin(theta),
//							(float) Math.sin(theta)
//					);
//					// System.out.println(f + ", " + angle);
//
//					f.setMag(100);
//					getCell((int) x, (int) y, (int) z).set(f);
//				}
//			}
//		}
	}
}
