package common;

import modulation.Mod;
import processing.core.PVector;
import scenes.Scene;

import java.awt.*;
import java.io.Serializable;

public class StoredVectorField implements VectorField {
	private static final float NOISE_SCALE = 2;

	private PVector[] field;
	private Rectangle bounds;
	private int cellSize;

	public StoredVectorField(Rectangle bounds, int cellSize) {
		this.bounds = bounds;
		this.cellSize = cellSize;
		field = new PVector[(bounds.width * bounds.height) / cellSize];
		for (int i = 0; i < field.length; i++) {
			field[i] = new PVector(0, 0);
		}
	}

	@Override
	public PVector at(float x, float y, float t) {
		int cellX = (int) (x - bounds.x) / cellSize;
		int cellY = (int) (y - bounds.y) / cellSize;
		return getCell(cellX, cellY);
	}

	public PVector getCell(int cellX, int cellY) {
		int i = cellX * bounds.width / cellSize + cellY;
		if (i < field.length && i > 0) {
			return field[cellY * bounds.width / cellSize + cellX];
		} else {
			return new PVector(0, 0);
		}
	}

	public int getGridWidth() {
		return bounds.width / cellSize;
	}

	public int getGridHeight() {
		return bounds.height / cellSize;
	}

	public void zero() {
		for (int i = 0; i < field.length; i++) {
			field[i].set(0, 0);
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
		float shift = (float) Math.random() * 10;
		for (int x = 0; x < getGridWidth(); x++) {
			for (int y = 0; y < getGridHeight(); y++) {
				float r = Scene.getInstance().noise(
						(float) x / getGridWidth() * NOISE_SCALE,
						(float) y / getGridHeight() * NOISE_SCALE,
						shift);
				float theta = (float) (r * Math.PI * 8);
				PVector f = PVector.fromAngle(theta);
				f.setMag(10);
				getCell(x, y).set(f);
			}
		}
	}
}
