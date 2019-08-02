package lusio;

import processing.serial.Serial;
import spacefiller.color.ColorProvider;
import spacefiller.color.TwoColorProvider;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Textfield;
import spacefiller.graph.renderer.BasicGraphRenderer;
import spacefiller.graph.Graph;
import spacefiller.graph.Node;
import lightcube.Lightcube;
import lusio.scenes.*;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PJOGL;
import scene.SceneApplet;
import spacefiller.remote.SerialStringRemoteControl;
import spacefiller.remote.signal.Gate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lusio extends SceneApplet implements ColorProvider {
  public static Lusio instance;

  private static String lightcubeSerialPort;
  private static String platformSerialPort;

  public static void main(String[] args) {
    if (args.length == 2) {
      lightcubeSerialPort = args[0];
      platformSerialPort = args[1];
      println("lightcube port = " + lightcubeSerialPort);
      println("platform port = " + platformSerialPort);
      main("lusio.Lusio");
    } else {
      println("usage:");
      println("java -Djava.library.path=\"linux64\" -jar algoplex1.jar [lightcube port] [platform port]");
      println();
      println("available ports:");
      for (String port : Serial.list()) {
        println(port);
      }
    }

  }

  private Map<String, Graph> graphs;
  private int selectedGraphIndex;
  private List<String> graphNames;
  private ControlP5 controlP5;
  private Textfield graphNameField;
  private Node selectedNode;
  private boolean creatingEdge;
  private boolean graphsVisible = false;
  private Lightcube lightcube;
  private Platform platform;
  private boolean modeSwitchFlag = false;

  public static int WIDTH = 1920;
  public static int HEIGHT = 1080;

  private TwoColorProvider twoColorProvider;
  private PImage logoImage;  // Declare variable "a" of type PImage

  public Lusio() {
    Lusio.instance = this;
  }

  public void settings() {
    fullScreen(P3D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    graphs = new HashMap<>();
    graphNames = new ArrayList<>();
    lightcube = Lightcube.wireless(lightcubeSerialPort);
    platform = new Platform(platformSerialPort, 115200);
    platform.onCubePlaced(slot -> switchScene(slot));

    loadGraphs();

    PGraphics canvas = createGraphics(1920, 1080, P3D);
    canvas.smooth();
    setCanvas(canvas);

    LusioScene[] lusioScenes = new LusioScene[] {
        new NestedCubeScene(),
        new ThreeDeeFlockScene(),

        new FluidScene(),
        new NoiseCircle(),
        new NoiseSpace(),
        new FancyParticles(),
        new FlockScene(),
        new CubeScene(),
        new TriangleScene(),
        new MillerLineScene(),
        new ContourScene(),
    };

    for (int i = 0; i < lusioScenes.length; i++) {
      lusioScenes[i].setGraphs(graphs);
      lusioScenes[i].setCube(lightcube);
    }

    addAllScenes(lusioScenes);

    controlP5 = new ControlP5(this);
    controlP5.hide();

    controlP5.addButton("New Graph")
        .setId(1)
        .setPosition(200, 20)
        .setSize(100, 20);

    graphNameField = controlP5.addTextfield("graphName")
        .setPosition(350,20)
        .setSize(200,20)
        .setFocus(true)
        .setColor(color(255,0,0));

    DropdownList dropdownList = controlP5.addDropdownList("Scene")
        .setId(2)
        .setPosition(20,20)
        .addItem("Scene One", 0)
        .addItem("Scene Two", 1)
        .close();

    controlP5.addButton("Delete Graph")
        .setId(3)
        .setPosition(600, 20)
        .setSize(100, 20);

    twoColorProvider = new TwoColorProvider(0xFFFFFFFF, 0x00000000, 3);

    switchScene(0);
    // super.setup();

    logoImage = loadImage("logo-2.png");

  }

  @Override
  public void switchScene(int sceneIndex) {
    super.switchScene(sceneIndex);
    lightcube.setMode(sceneIndex);
  }

  @Override
  public final void draw() {
    background(0);
    canvas.beginDraw();

    if (lightcube.getMode() == 1) {
      if (!modeSwitchFlag) {
        modeSwitchFlag = true;
        canvas.background(0, 0, 0);
      } else {
        canvas.background(255 - red(lightcube.getColor()), 255 - red(lightcube.getColor()), 255 - red(lightcube.getColor()));
      }
    } else {
      canvas.background(0);
      modeSwitchFlag = false;
    }

    canvas.ortho();
    super.draw();

    lightcube.update();

    canvas.noFill();
    canvas.stroke(255);

    if (selectedNode != null && creatingEdge) {
      canvas.strokeWeight(1);
      canvas.color(255);
      canvas.line(selectedNode.position.x, selectedNode.position.y, mouseX, mouseY);
    }


    if (currentScene != null) {
      if (lightcube.readTransitionScene()) {
        lightcube.flipOrientation();
        gotoNextScene();
      }
    }

    if (graphsVisible) {
      BasicGraphRenderer renderer = new BasicGraphRenderer(1);
      for (int i = 0; i < graphNames.size(); i++) {
        Graph g = graphs.get(graphNames.get(i));
        if (i == selectedGraphIndex) {
          renderer.setColor(255);
        } else {
          renderer.setColor(100);
        }
        renderer.render(canvas, g);
      }
    }

    //lightcube.drawDebug(canvas, 200, 100);
    canvas.endDraw();
    drawFlipGuide(200, 200, 200);

    image(canvas, 0, 0);
    image(logoImage, WIDTH - logoImage.width - 30, HEIGHT - logoImage.height - 20);


    if (!lightcube.isActive()) {
      fill(255, 0, 0);
      text("CUBE DISCONNECTED", 20, 20);

      if (frameCount % 60 == 0) {
        lightcube.connect();
      }
    }

    if (!platform.isActive()) {
      fill(255, 0, 0);
      text("PLATFORM DISCONNECTED", 20, 40);
    }

  }

  public Graph selectedGraph() {
    return graphs.get(graphNames.get(selectedGraphIndex));
  }

  private void drawFlipGuide(float x, float y, float radius) {
    canvas.translate(x, y);
    canvas.stroke(255);
    canvas.strokeWeight(5);
    canvas.ellipse(0, 0, radius, radius);
    canvas.noStroke();
    if (lightcube.getMode() == 1) {
      canvas.fill(lightcube.getColor());
    } else {
      canvas.fill(255);
    }
    canvas.arc(0, 0, radius, radius, 0, lightcube.getCounter() * PI * 2);
  }

  public void controlEvent(ControlEvent event) {
    if (event.getId() == 1) {
      Graph g = new Graph();
      graphs.put(graphNameField.getText(), g);
      graphNames.add(graphNameField.getText());
      selectedGraphIndex = graphNames.size() - 1;
      saveGraphs();
    } else if (event.getId() == 2) {
      switchScene((int) event.getController().getValue());
    } else if (event.getId() == 3) {
      graphs.remove(graphNames.get(selectedGraphIndex));
      graphNames.remove(selectedGraphIndex);
      selectedGraphIndex = 0;
      saveGraphs();
    }
  }

  public final void keyPressed() {
    if (keyCode == CONTROL) {
      if (controlP5.isVisible()) {
        controlP5.hide();
      } else {
        controlP5.show();
      }
    }

    if (key == 'U') {
      selectedGraph().getNodes().remove(selectedGraph().getNodes().size() - 1);
    }

    if (keyCode == ALT) {
      graphsVisible = !graphsVisible;
    }

    if (keyCode == RIGHT) {
      selectedGraphIndex = (selectedGraphIndex + 1) % graphs.size();
    }

    if (keyCode == DOWN) {
      gotoNextScene();
    }

    if (key == ' ') {
      lightcube.flipOrientation();
    }
  }

  public final void mousePressed() {
    if (controlP5.isVisible() || !graphsVisible) {
      return;
    }

    selectedNode = null;
    creatingEdge = false;

    if (keyPressed && keyCode == SHIFT) {
      creatingEdge = true;
    }

    if (selectedGraph() != null) {
      // Is the click near a node that already exists? If so, select the node.
      for (Node n : selectedGraph().getNodes()) {
        if (PVector.dist(n.position, new PVector(mouseX, mouseY)) < 20) {
          selectedNode = n;
        }
      }

      if (selectedNode == null && !creatingEdge) {
        selectedNode = selectedGraph().createNode(mouseX, mouseY);
      }
    }
  }

  public final void mouseReleased() {
    if (selectedNode != null) {
      if (creatingEdge) {
        for (Node n : selectedGraph().getNodes()) {
          if (PVector.dist(n.position, new PVector(mouseX, mouseY)) < 20) {
            selectedGraph().createEdge(selectedNode, n);
          }
        }
      }
      selectedNode = null;
    }
    saveGraphs();
  }

  public final void mouseDragged() {
    if (selectedNode != null) {
      if (creatingEdge) {
        // do nothing?
      } else {
        selectedNode.position.set(mouseX, mouseY);
      }
    }
    saveGraphs();
  }

  private void saveGraphs() {
    try {
      FileOutputStream fileOut =
          new FileOutputStream("lusio-graphs.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(graphs);
      out.close();
      fileOut.close();
    } catch (IOException i) {
      i.printStackTrace();
    }
  }

  private void loadGraphs() {
    try {
      FileInputStream fileIn = new FileInputStream("lusio-graphs.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      graphs = (Map<String, Graph>) in.readObject();
      graphNames = new ArrayList<>(graphs.keySet());
      in.close();
      fileIn.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException i) {
      i.printStackTrace();
    } catch (ClassNotFoundException c) {
      c.printStackTrace();
    }
  }

  @Override
  public int getColor(int index) {
    twoColorProvider.setColor2(this.lightcube.getColor());
    return twoColorProvider.getColor(index);
  }
}