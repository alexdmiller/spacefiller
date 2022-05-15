package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;
import processing.core.PImage;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Image extends Animator.SimpleAnimator {
  private PImage image;

  public Image(String path) {
    BufferedImage img;
    try {
      img = ImageIO.read(new File(path));
      image = new PImage(img);
    } catch (IIOException e) {

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setup() {
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.image(image, graphics.width / 2 - image.width / 2 * 2, graphics.height / 2 - image.height / 2 * 2, image.width * 2, image.height * 2);
  }
}
