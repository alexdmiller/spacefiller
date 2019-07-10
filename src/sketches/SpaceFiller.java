package sketches;

import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;

public class SpaceFiller extends Scene {
	@Mod(min = 1, max = 100)
	public int stepX = 20;

	@Mod(min = 1, max = 100)
	public int stepY = 40;

	@Mod(min = 1, max = 100)
	public int CircSize = 30;

	@Mod(min = 1, max = 100)
	public int RectSize = 20;



	////////////////////
	//AlphaBet Lists//
	////////////////////
	int[] SX = {3, 3, 3, 2, 1, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 2, 1, 0, 0, 0};
	int[] SY = {2, 1, 0, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 5, 6, 7, 8, 8, 8, 8, 7, 6};
	int[] PX = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 2, 1};
	int[] PY = {8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 1, 2, 3, 3, 3};
	int[] AX = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 2};
	int[] AY = {8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 8, 7, 6, 5, 4, 3, 2, 1, 0, 3, 3};
	int[] CX = {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3};
	int[] CY = {0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
	int[] EX = {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 1, 2, 3};
	int[] EY = {0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 4, 4, 4};
	int[] FX = {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3};
	int[] FY = {0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 4, 4, 4};
	int[] IX = {0, 0, 0, 0, 0, 0, 0, 0, 1};
	int[] IY = {0, 2, 3, 4, 5, 6, 7, 8, 8};
	int[] LX = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3};
	int[] LY = {0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
	int[] RX = {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 1, 1, 2, 2, 3, 3, 3, 4};
	int[] RY = {0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 3, 3, 3, 2, 1, 4, 5, 5, 6, 6, 7, 8, 8};

	int[] XCheck = {};
	int[] YCheck = {};

	public static void main(String[] args) {
		main("sketches.SpaceFiller");
	}

	@Override
	public void doSetup() {

		//new OscRemoteControl(this, 8888);
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		XCheck = new int[0];
		YCheck = new int[0];

		appendCoord(SX.length, SX, SY, 3, 4);
		appendCoord(PX.length, PX, PY, 8, 4);
		appendCoord(AX.length, AX, AY, 13, 4);
		appendCoord(CX.length, CX, CY, 18, 4 );
		appendCoord(EX.length, EX, EY, 23, 4 );
		appendCoord(FX.length, FX, FY, 5, 14);
		appendCoord(IX.length, IX, IY, 10, 14);
		appendCoord(LX.length, LX, LY, 13, 14);
		appendCoord(LX.length, LX, LY, 18, 14);
		appendCoord(EX.length, EX, EY, 23, 14 );
		appendCoord(RX.length, RX, RY, 28, 14);

		graphics.translate(-width / 2, -height / 2);
		for(int j = 0; j < height; j += 5){
			graphics.stroke(0,255,0);
			graphics.strokeWeight(1);
			graphics.line(0,j, width, j);
		}
		graphics.noStroke();
		for (float x = 0 - stepX; x < width + stepX; x += stepX) {
			for (float y = 0 - stepY; y < height + stepY; y += stepY) {
				graphics.fill(random(150,225),0, random(150, 225));
				if (random(0, 100) > 90) {
					graphics.fill(random(0,255));
				}
				for (int k = 0; k < XCheck.length; k +=1) {
					if (XCheck[k] == x & YCheck[k] ==y) {
						graphics.fill(  random(200, 225), random(200, 225), 0);
					}
				}
				graphics.rect(RectSize + x, RectSize + y, RectSize, RectSize);
				graphics.ellipse(CircSize/2 + x, CircSize/2 + y, CircSize, CircSize);
			}
		}
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

	}

	public void doKeyPressed() {

	}

	void appendCoord(int len, int[] gridX, int[] gridY, int moveX, int moveY) {
		for (int i = 0; i < len; i+=1) {
			XCheck = append(XCheck, (gridX[i] + moveX) * stepX);
			YCheck = append(YCheck, (gridY[i] + moveY) * stepY);
		}
	}
}
