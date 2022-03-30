package spacefiller.apps.tests;

import processing.core.PApplet;

public class TestApp extends PApplet {
    public static void main(String[] args) {
        PApplet.main("spacefiller.apps.tests.TestApp");
    }

    public void settings() {
        size(500, 500);
    }

    public void setup() {
    }

    public void draw() {
        background(0);
        stroke(255);
        line(0, 0, width, height);
    }
}
