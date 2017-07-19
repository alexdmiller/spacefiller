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
import lusio.scenes.Scene;
import lusio.scenes.SceneOne;
import lusio.scenes.SceneTwo;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PJOGL;

import java.util.HashMap;
import java.util.Map;

public class Lusio extends PApplet {
  private static PApplet instance;

  public static void main(String[] args) {
    main("lusio.Lusio");
  }

  private SyphonServer server;
  private PGraphics canvas;
  private Scene[] scenes = { new SceneOne(), new SceneTwo() };
  private Scene currentScene;

  private Map<String, Graph> graphs;
  private Graph selectedGraph;
  private ControlP5 controlP5;
  private Textfield graphNameField;
  private Node selectedNode;
  private boolean creatingEdge;

  public Lusio() {
    Lusio.instance = this;
    graphs = new HashMap<>();
  }

  public void settings() {
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    canvas = createGraphics(1920, 1080, P3D);
    canvas.smooth();
    server = new SyphonServer(this, this.getClass().getName());

    controlP5 = new ControlP5(this);
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
        .addItem("Scene Two", 1);
  }

  public final void draw() {
    canvas.beginDraw();

    canvas.background(0);
    canvas.noFill();
    canvas.stroke(255);

    GraphRenderer renderer = new BasicGraphRenderer();
    for (Graph g : graphs.values()) {
      renderer.render(canvas, g);
    }

    if (selectedNode != null && creatingEdge) {
      canvas.strokeWeight(1);
      canvas.color(255);
      canvas.line(selectedNode.position.x, selectedNode.position.y, mouseX, mouseY);
    }

    canvas.ortho();

    if (currentScene != null) {
      currentScene.draw(canvas);
    }

    image(canvas, 0, 0);
    canvas.endDraw();
  }

  public void controlEvent(ControlEvent event) {
    if (event.getId() == 1) {
      Graph g = new Graph();
      selectedGraph = g;
      graphs.put(graphNameField.getStringValue(), g);
    } else if (event.getId() == 2) {
      switchScene((int) event.getController().getValue());
    }
  }

  public final void mousePressed() {
    selectedNode = null;
    creatingEdge = false;

    if (keyPressed && keyCode == SHIFT) {
      creatingEdge = true;
    }

    if (selectedGraph != null) {
      // Is the click near a node that already exists? If so, select the node.
      for (Node n : selectedGraph.getNodes()) {
        if (PVector.dist(n.position, new PVector(mouseX, mouseY)) < 20) {
          selectedNode = n;
        }
      }

      if (selectedNode == null && !creatingEdge) {
        selectedNode = selectedGraph.createNode(mouseX, mouseY);
      }
    }
  }

  public final void mouseReleased() {
    if (selectedNode != null) {
      if (creatingEdge) {
        for (Node n : selectedGraph.getNodes()) {
          if (PVector.dist(n.position, new PVector(mouseX, mouseY)) < 20) {
            selectedGraph.createEdge(selectedNode, n);
          }
        }
      }
      selectedNode = null;
    }
  }

  public final void mouseDragged() {
    if (selectedNode != null) {
      if (creatingEdge) {

      } else {
        selectedNode.position.set(mouseX, mouseY);
      }
    }
  }

  public void switchScene(int sceneIndex) {
    if (currentScene != null) {
      currentScene.teardown();
    }

    Scene scene = scenes[sceneIndex];
    // TODO: transition old scene out; new scene in.k
    scene.setup(graphs);
    currentScene = scene;
  }
}