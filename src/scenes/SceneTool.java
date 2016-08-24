package scenes;

import processing.core.PGraphics;

public interface SceneTool {
	void render(PGraphics graphics);
	void mousePressed(float mouseX, float mouseY);
	void keyDown(char key);
}
