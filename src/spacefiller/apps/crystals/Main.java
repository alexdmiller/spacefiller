package spacefiller.apps.crystals;

import crystal.audio.Audio;
import spacefiller.crystals.engine.*;
import crystal.ui.*;
import g4p_controls.GWinData;
import g4p_controls.GWindow;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.patchbay.Patchbay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import processing.sound.*;

public class Main extends PApplet {
  private Amplitude loudness;

  public static void main(String[] args) {
    PApplet.main("Main");
  }

  Engine engine;
  GWindow controlWindow;

  List<Component> components;

  EngineStatePicker engineStatePicker;
  MappingEditor mappingEditor;
  KernelSelector kernelSelector;
  KernelEditor kernelEditor;
  ThresholdEditor thresholdEditor;
  MapSelector mapSelector;
  SeedSelector seedSelector;
  FrameskipEditor frameskipEditor;
  TimeBlurEditor timeBlurEditor;
  ScaleEditor scaleEditor;
  GlobalSettings globalSettings;
  PFont font;
  MidiController midiController;
  Audio audio;
  boolean controlsReady;


  public void settings() {
    // size(1920/2, 1080/2, P3D);
    fullScreen(P3D, 2);
    PJOGL.profile = 1;
  }

  public void setup() {

    frameRate(60);
    controlWindow = GWindow.getWindow(this, "c r y s t a l︎", 0, 0, 1792, 1100, P2D);
    controlWindow.noSmooth();
    ((PGraphicsOpenGL) controlWindow.getGraphics()).textureSampling(3);

    font = controlWindow.createFont("data/fonts/InputMono-Thin.ttf", 13, false);

    controlWindow.addDrawHandler(this, "drawControls");
    controlWindow.addMouseHandler(this, "onMouseEvent");
    controlWindow.addKeyHandler(this, "onKeyboardEvent");

    noSmooth();
    ((PGraphicsOpenGL) g).textureSampling(3);

    try {
      Kernel[][] kernels = Loaders.loadKernels(this);
      EngineState[] states = Loaders.loadEngineStates();
      engine = new Engine(this, kernels, states, 3, 4, 3);
      audio = new Audio(this, 7, engine.getNumAudioChannels());
      audio.listen(engine);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    components = new ArrayList<>();
    Layout layout = new Layout(20);

    engineStatePicker = new EngineStatePicker(controlWindow, engine, 1000);
    layout.placeComponent(engineStatePicker);
    components.add(engineStatePicker);

    mapSelector = new MapSelector(controlWindow, engine, 450);
    layout.placeComponent(mapSelector);
    components.add(mapSelector);

    seedSelector = new SeedSelector(controlWindow, engine, 450);
    layout.placeComponent(seedSelector);
    components.add(seedSelector);

    layout.newColumn();

    kernelSelector = new KernelSelector(controlWindow, engine, 3);
    layout.placeComponent(kernelSelector);
    components.add(kernelSelector);

    kernelEditor = new KernelEditor(controlWindow);
    layout.placeComponent(kernelEditor);
    components.add(kernelEditor);

    thresholdEditor = new ThresholdEditor(controlWindow);
    layout.placeComponent(thresholdEditor);
    components.add(thresholdEditor);

    frameskipEditor = new FrameskipEditor(controlWindow, engine, (int) kernelEditor.getWidth());
    layout.placeComponent(frameskipEditor);
    components.add(frameskipEditor);


    layout.newColumn();

    globalSettings = new GlobalSettings(controlWindow, engine);
    layout.placeComponent(globalSettings);
    components.add(globalSettings);

    timeBlurEditor = new TimeBlurEditor(controlWindow, engine, 200);
    layout.placeComponent(timeBlurEditor);
    components.add(timeBlurEditor);

    scaleEditor = new ScaleEditor(controlWindow, engine, 200, 1, 100);
    layout.placeComponent(scaleEditor);
    components.add(scaleEditor);

    mappingEditor = new MappingEditor(engine, audio, controlWindow);
    layout.placeComponent(mappingEditor);
    components.add(mappingEditor);

    MidiConsole console = new MidiConsole(controlWindow, engine);
    layout.placeComponent(console);
    components.add(console);

    AudioMonitor audioMonitor = new AudioMonitor(controlWindow, audio);
    layout.placeComponent(audioMonitor);
    components.add(audioMonitor);

    Patchbay patchbay = new Patchbay();
    patchbay.autoroute(this);
    patchbay.autoroute(engine);

    for (Animator animator : engine.getSeeds()) {
      patchbay.autoroute(animator);
    }

    for (Animator animator : engine.getMaps()) {
      patchbay.autoroute(animator);
    }

    for (Component component : components) {
      patchbay.autoroute(component);
    }

    midiController = new MidiController(engine);
    patchbay.autoroute(midiController);

    patchbay.printRoutes();
    patchbay.midi().registerEventListener(engine);
    // patchbay.midi().log();

    controlsReady = true;
  }

  public void drawControls(PApplet app, GWinData data) {
    if (!controlsReady) {
      return;
    }

    app.background(0);
    controlWindow.textFont(font, 13);

    if (components != null) {
      for (Component component : components) {
        component.draw();
      }
    }

    if (kernelSelector != null) {
      Kernel selectedKernel = engine.getKernel(kernelSelector.getCurrentPack(), kernelSelector.getCurrentKernel());
      kernelEditor.setKernel(selectedKernel);
      thresholdEditor.setKernel(selectedKernel);
      frameskipEditor.setKernel(selectedKernel);
    }

    app.fill(255);
    app.stroke(255);
    app.text(frameRate, app.width - 30, app.height - 20);
  }

  public void draw() {
    audio.update();

    background(0);

    PImage result = engine.run();
    int renderHeight = 1080;
    int renderWidth = (int) (renderHeight * ((float) 1920 / 1080));

    float scale = engine.getScale();
    image(
        result,
        width / 2 - renderWidth / 2 * scale,
        height / 2 - renderHeight / 2 * scale,
        renderWidth * scale,
        renderHeight * scale);
    // saveFrame("####.png");
  }

  public void onMouseEvent(PApplet app, GWinData data, MouseEvent event) {
    PVector mouse = new PVector(event.getX(), event.getY());
    for (Component component : components) {
      PVector localMouse = PVector.sub(mouse, component.getPosition());

      component.setMousePosition(localMouse);
      if (event.getAction() == MouseEvent.PRESS) {
        if (component.mouseInside()) {
          component.mousePressed(localMouse);
        }
      } else if (event.getAction() == MouseEvent.RELEASE) {
        component.mouseReleased(localMouse);
      }
    }

    if (event.getAction() == MouseEvent.RELEASE) {
      try {
        Loaders.saveKernels(engine.getKernels());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void onKeyboardEvent(PApplet app, GWinData data, KeyEvent event) {
    if (event.getAction() == KeyEvent.PRESS) {
      if (event.isMetaDown()) {
        switch (event.getKey()) {

        }
      } else {
        switch (event.getKey()) {
          case '1':
            engine.setKernelPackIndex(0, kernelSelector.getCurrentPack());
            engine.setKernelIndex(0, kernelSelector.getCurrentKernel());
            break;
          case '2':
            engine.setKernelPackIndex(1, kernelSelector.getCurrentPack());
            engine.setKernelIndex(1, kernelSelector.getCurrentKernel());
            break;
          case '3':
            engine.setKernelPackIndex(2, kernelSelector.getCurrentPack());
            engine.setKernelIndex(2, kernelSelector.getCurrentKernel());
            break;
        }
      }

      if (event.getKey() == 'm') {
        engine.getRenderer().setRenderMap(!engine.getRenderer().getRenderMap());
      }

      if (event.getKey() == 'c') {
        engine.getRenderer().clear();
      }

      if (event.getKey() == 's') {
        try {
          Loaders.saveKernels(engine.getKernels());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    for (Component component : components) {
      if (event.getAction() == KeyEvent.PRESS) {
        component.keyPressed(event);
      } else if (event.getAction() == KeyEvent.RELEASE) {
        component.keyReleased(event);
      }
    }
  }
}
