package lusio;

import codeanticode.syphon.SyphonServer;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Textfield;
import graph.BasicGraphRenderer;
import graph.Graph;
import graph.GraphRenderer;
import graph.Node;
import lusio.scenes.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PJOGL;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lusio extends PApplet {
  public static PApplet instance;

  public static void main(String[] args) {
    main("lusio.Lusio");
  }

  private SyphonServer server;
  private PGraphics canvas;

  private Scene[] scenes = { new CubeScene(), new SceneThree(), new SceneFour(), new SceneOne(), new SceneTwo() };

  private Scene currentScene;
  private int currentSceneIndex;

  private Map<String, Graph> graphs;
  private int selectedGraphIndex;
  private List<String> graphNames;

  private ControlP5 controlP5;
  private Textfield graphNameField;
  private Node selectedNode;
  private boolean creatingEdge;
  private boolean graphsVisible = true;

  private float switchTimer = 0;
  private float timeUntilSwitch = 50;
  private float switchThreshold = 0.9f;
  private boolean transitionOut = false;

  private Lightcube lightcube;

  public static int WIDTH = 1920;
  public static int HEIGHT = 1080;

  public Lusio() {
    Lusio.instance = this;
  }

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    graphs = new HashMap<>();
    graphNames = new ArrayList<>();
    lightcube = Lightcube.wireless();

    canvas = createGraphics(1920, 1080, P3D);
    canvas.smooth();
    server = new SyphonServer(this, this.getClass().getName());

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

    loadGraphs();

    switchScene(0);
  }

  public final void draw() {
    canvas.beginDraw();
    canvas.background(0);

    lightcube.update();
    lightcube.drawDebug(canvas, 100, 100);

    updateSwitchTimer();

    canvas.noFill();
    canvas.stroke(255);

    if (selectedNode != null && creatingEdge) {
      canvas.strokeWeight(1);
      canvas.color(255);
      canvas.line(selectedNode.position.x, selectedNode.position.y, mouseX, mouseY);
    }

    canvas.ortho();

    if (currentScene != null) {
      if (transitionOut) {
        if (currentScene.transitionOut()) {
          transitionOut = false;
          // TODO: be smarter about assigning the zero point?
          switchScene((currentSceneIndex + 1) % scenes.length);
        }
      }

      currentScene.draw(lightcube, canvas);
    }

    if (graphsVisible) {
      GraphRenderer renderer = new BasicGraphRenderer();
      for (int i = 0; i < graphNames.size(); i++) {
        Graph g = graphs.get(graphNames.get(i));
        if (i == selectedGraphIndex) {
          canvas.stroke(255);
        } else {
          canvas.stroke(100);
        }
        renderer.render(canvas, g);
      }
    }

    canvas.pushMatrix();
    canvas.translate(width / 2, height / 2);
    canvas.fill(255, 255, 255, lightcube.getFlipAmount() * 255);
    canvas.noStroke();
    canvas.ellipse(0, 0, switchTimer / timeUntilSwitch * width * 1.2f, switchTimer / timeUntilSwitch * width * 1.2f);
    canvas.popMatrix();

    image(canvas, 0, 0);
    canvas.endDraw();
  }

  private final void updateSwitchTimer() {
    if (lightcube.getFlipAmount() > switchThreshold) {
      switchTimer++;
    } else if (switchTimer > 0) {
      switchTimer -= 5;
    } else {
      switchTimer = 0;
    }

    if (switchTimer > timeUntilSwitch) {
      lightcube.flipOrientation();
      switchTimer = 0;

      int nextSceneIndex = (currentSceneIndex + 1) % scenes.length;
      switchScene(nextSceneIndex);
    }

    canvas.pushMatrix();
    canvas.translate(50, 220);
    canvas.noFill();
    canvas.stroke(255);
    canvas.rect(0, 0, 100, 10);
    canvas.fill(255);
    canvas.rect(0, 0, switchTimer / timeUntilSwitch * 100, 10);
    canvas.popMatrix();
  }

  public Graph selectedGraph() {
    return graphs.get(graphNames.get(selectedGraphIndex));
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

  public void switchScene(int sceneIndex) {
    if (currentScene != null) {
      currentScene.teardown();
    }

    Scene scene = scenes[sceneIndex];
    currentSceneIndex = sceneIndex;

    // TODO: transition old scene out; new scene in.
    scene.setup(graphs);

    currentScene = scene;
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
}