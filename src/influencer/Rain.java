package influencer;

public class Rain extends InfluencerScene {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new Rain());
  }

  private int rows = 5;
  private int cols = 20;
  private float crossHairSize = 10;

  @Override
  public void draw() {
    background(0);
    pushMatrix();


    translate(sin(frameCount / 100f) * 100, 0);
    stroke(255);

    float[] notes = decayedNotes.getArray();
    for (int i = 0; i < notes.length; i++) {
      int c = (i * 17) % (cols*rows);
      int x = c % cols;
      int y = c / cols;

      pushMatrix();
      noFill();
      translate(x * ((float) width / cols), y * ((float) height / rows), 0);
      strokeWeight(1);
      line(-crossHairSize, 0, crossHairSize, 0);
      line(0, -crossHairSize, 0, crossHairSize);

      popMatrix();

      strokeWeight(3);
      if (notes[i] > 0) {
        fill(255 * notes[i]);
        pushMatrix();
        translate(x * ((float) width / cols), y * ((float) height / rows) - notes[i] * 100, -100);
        rect(0, 0, ((float) width / cols), ((float) height / rows));
        popMatrix();
      }
    }
    popMatrix();

    //camera();
  }
}
