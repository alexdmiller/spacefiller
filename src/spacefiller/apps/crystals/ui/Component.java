package spacefiller.apps.crystals.ui;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

public abstract class Component {
  protected PVector position;
  protected PVector mouse;
  protected PVector lastMouseClick;
  protected PVector lastMouse;
  protected boolean mouseDown;
  protected PApplet applet;

  public Component(PApplet applet) {
    this.applet = applet;
    this.position = new PVector();
    this.mouse = new PVector();
    this.lastMouse = new PVector();
  }

  public final void draw() {
    applet.pushMatrix();
    applet.translate(position.x, position.y);
    doDraw();
    applet.popMatrix();

    lastMouse.set(mouse);
  };

  protected abstract void doDraw();
  public abstract float getWidth();
  public abstract float getHeight();

  public final void setMousePosition(PVector mouse) {
    this.mouse.set(mouse);
  }

  public boolean mouseInside() {
    return mouse.x > 0 &&
        mouse.x < getWidth() &&
        mouse.y > 0 &&
        mouse.y < getHeight();
  }

  public void mousePressed(PVector mouse) {
    mouseDown = true;
    lastMouseClick = mouse.copy();
  }

  public void mouseReleased(PVector mouse) {
    mouseDown = false;
  }

  public void keyPressed(KeyEvent event) {

  }

  public void keyReleased(KeyEvent event) {

  }

  public final PVector getPosition() {
    return position;
  }

  public final void setPosition(PVector position) {
    this.position = position;
  }

  public final void setPosition(float x, float y) {
    this.position.set(x, y);
  }
}
