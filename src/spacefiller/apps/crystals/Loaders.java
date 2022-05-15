package spacefiller.apps.crystals;

import spacefiller.crystals.engine.EngineState;
import spacefiller.crystals.engine.Kernel;
import spacefiller.crystals.engine.RenderPromise;
import processing.core.PApplet;
import processing.core.PImage;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Loaders {
  public static final int NUM_KERNELS = 12;
  public static final int NUM_KERNEL_PACKS = 24;
  public static final int KERNEL_SIZE = 7;
  public static final int NUM_STATES = 8 * 5;

  public static final String DATA_DIRECTORY = "data";
  public static final String KERNEL_DIRECTORY = DATA_DIRECTORY + File.separator + "kernels";
  public static final String SAVE_DIRECTORY = DATA_DIRECTORY + File.separator + "saves";
  public static final String SCREENSHOT_DIRECTORY = DATA_DIRECTORY + File.separator + "screenshots";

  public static void saveKernels(Kernel[][] kernels) throws IOException {
    for (int pack = 0; pack < kernels.length; pack++) {
      for (int k = 0; k < kernels[pack].length; k++) {
        Files.createDirectories(Paths.get(KERNEL_DIRECTORY + File.separator + pack));
        Kernel kernel = kernels[pack][k];
        FileWriter writer = new FileWriter(
            KERNEL_DIRECTORY + File.separator + pack + File.separator + k + ".kernel");
        writer.write(kernel.getSize() + "\n");
        for (int row = 0; row < kernel.getSize(); row++) {
          writer.write("" + kernel.getCell(0, row));
          for (int col = 1; col < kernel.getSize(); col++) {
            writer.write("," + kernel.getCell(col, row));
          }
          writer.write("\n");
        }
        float[] thresholds = kernel.getThresholds();
        writer.write(thresholds[0] + "," + thresholds[1] + "," + thresholds[2] + "\n");
        writer.write("" + kernel.getFrameskips() + "\n");
        writer.close();
      }
    }
  }

  public static Kernel[][] loadKernels(PApplet applet) throws IOException {
    Kernel[][] kernels = new Kernel[NUM_KERNEL_PACKS][NUM_KERNELS];

    for (Kernel[] pack : kernels) {
      for (int i = 0; i < pack.length; i++) {
        pack[i] = new Kernel(KERNEL_SIZE, applet);
      }
    }

    File directoryPath = new File(KERNEL_DIRECTORY);
    for (File kernelPackDirectory : directoryPath.listFiles()) {
      if (kernelPackDirectory.getName().equals(".DS_Store")) {
        continue;
      }

      for (File kernelFile : kernelPackDirectory.listFiles()) {
        if (kernelFile.getName().equals(".DS_Store")) {
          continue;
        }

        BufferedReader br = new BufferedReader(new FileReader(kernelFile));
        String sizeString = br.readLine();
        int size = Integer.parseInt(sizeString);
        float[] matrix = new float[size * size];
        for (int i = 0; i < size; i++) {
          String line = br.readLine();
          String[] splitLine = line.split(",");
          for (int j = 0; j < size; j++) {
            matrix[i * size + j] = Float.parseFloat(splitLine[j]);
          }
        }

        int kernelPackIndex = Integer.parseInt(kernelPackDirectory.getName());
        int kernelIndex = Integer.parseInt(kernelFile.getName().split("\\.")[0]);

        kernels[kernelPackIndex][kernelIndex].setMatrix(matrix);

        String thresholdString = br.readLine();
        String[] splitThreshold = thresholdString.split(",");
        kernels[kernelPackIndex][kernelIndex].setThresholds(
            Float.parseFloat(splitThreshold[0]),
            Float.parseFloat(splitThreshold[1]),
            Float.parseFloat(splitThreshold[2])
        );

        String frameskipsString = br.readLine();
        kernels[kernelPackIndex][kernelIndex].setFrameskips(
            Integer.parseInt(frameskipsString));
      }
    }

    return kernels;
  }

  public static void saveEngineStates(EngineState[] states) throws IOException {
    for (int i = 0; i < states.length; i++) {
      Files.createDirectories(Paths.get(SAVE_DIRECTORY));
      EngineState state = states[i];
      FileOutputStream fileOut =
          new FileOutputStream(SAVE_DIRECTORY + File.separator + i + ".ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(state);
      out.close();
      fileOut.close();

      if (state.preview != null && state.preview.image != null) {
        state.preview.image.save(SCREENSHOT_DIRECTORY + File.separator + i + ".png");
      }
    }
  }

  public static EngineState[] loadEngineStates() throws IOException, ClassNotFoundException {
    Files.createDirectories(Paths.get(SAVE_DIRECTORY));

    File directoryPath = new File(SAVE_DIRECTORY);
    EngineState[] states = new EngineState[NUM_STATES];

    for (int i = 0; i < states.length; i++) {
      states[i] = new EngineState();
    }

    for (File engineStateFile : directoryPath.listFiles()) {
      if (engineStateFile.getName().equals(".DS_Store")) {
        continue;
      }

      FileInputStream inputStream = new FileInputStream(engineStateFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

      int stateIndex = Integer.parseInt(engineStateFile.getName().split("\\.")[0]);
      states[stateIndex] = (EngineState) objectInputStream.readObject();

      objectInputStream.close();
      inputStream.close();

      BufferedImage img;
      try {
        img = ImageIO.read(new File(SCREENSHOT_DIRECTORY + File.separator + stateIndex + ".png"));
        states[stateIndex].preview = new RenderPromise(new PImage(img));
      } catch (IIOException e) {

      }
    }

    return states;
  }
}
