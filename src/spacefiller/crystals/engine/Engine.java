package spacefiller.crystals.engine;

import crystal.audio.BeatListener;
import crystal.maps.*;
import crystal.maps.Image;
import crystal.seeds.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import spacefiller.patchbay.annotations.*;
import spacefiller.patchbay.midi.MidiEventListener;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static crystal.engine.MidiMapping.*;
import static crystal.engine.MidiMapping.Type.*;
import static crystal.maps.Constants.R;
import static crystal.maps.Constants.G;
import static crystal.maps.Constants.B;
import static crystal.engine.MidiMapping.EngineInput;

public class Engine implements MidiEventListener, BeatListener {
  private static final float SCALE_SPEED = 0.2f;
  private final Animator[] maps;
  private final Animator.SimpleAnimator[] seeds;

  private PApplet applet;
  private Kernel[][] kernels;
  private Kernel blackKernel;
  private Kernel[] activeKernels;
  private Renderer renderer;
  private PGraphics mapCanvas;
  private PGraphics seedCanvas;
  private EngineState[] engineStates;
  private int currentEngineState = 0;
  private EngineState state;
  private float currentScale;
  private RenderPromise renderPromise;
  private PGraphics previewRenderer;
  private Map<Source, List<Integer>> midiNotes;
  private int numMidiChannels;
  private int numAudioChannels;
  private int numRandomChannels;

  // Inputs
  @Midi(channel = 10, note = 0)
  public boolean pulse = false;
  public boolean on = true;

  private static int MIN_SCALE = 2;

  public Engine(
      PApplet applet,
      Kernel[][] kernels,
      EngineState[] engineStates,
      int midiChannels,
      int audioChannels,
      int randomChannels) {
    this.applet = applet;
    this.kernels = kernels;
    this.engineStates = engineStates;
    this.state = new EngineState();

    activeKernels = new Kernel[3];
    renderer = new Renderer(applet, 1920 / MIN_SCALE, 1080 / MIN_SCALE);

    mapCanvas = applet.createGraphics(
        renderer.getSimWidth() / 1,
        renderer.getSimHeight() / 1,
        PConstants.P3D);
    mapCanvas.noSmooth();
    ((PGraphicsOpenGL) mapCanvas).textureSampling(3);

    seedCanvas = applet.createGraphics(
        renderer.getSimWidth() / 1,
        renderer.getSimHeight() / 1,
        PConstants.P2D);
     seedCanvas.noSmooth();
     ((PGraphicsOpenGL) seedCanvas).textureSampling(3);

    previewRenderer = applet.createGraphics(
        renderer.getSimWidth() / 2,
        renderer.getSimHeight() / 2,
        PConstants.P3D);
    previewRenderer.noSmooth();
    ((PGraphicsOpenGL) previewRenderer).textureSampling(3);

    currentScale = state.scale;

    blackKernel = new Kernel(kernels[0][0].getSize(), applet);

    midiNotes = new HashMap<>();
    for (int i = 0; i < midiChannels; i++) {
      midiNotes.put(new Source(MIDI, 1 + i), new CopyOnWriteArrayList<>(new ArrayList<>()));
    }

    for (int i = 0; i < audioChannels; i++) {
      midiNotes.put(new Source(AUDIO, i), new CopyOnWriteArrayList<>(new ArrayList<>()));
    }

    for (int i = 0; i < randomChannels; i++) {
      midiNotes.put(new Source(RANDOM, i), new CopyOnWriteArrayList<>(new ArrayList<>()));
    }

    numMidiChannels = midiChannels;
    numAudioChannels = audioChannels;
    numRandomChannels = randomChannels;

    this.maps = new Animator[] {
        new Shader(applet, "data/shaders/zoom.glsl"),
        new Shader(applet, "data/shaders/checker.glsl"),
        new Shapes().square(R,0.5f, 0.5f, 0.1f),
        new Shapes().square(R,0.5f, 0.5f, 0.25f),
        new Shapes().square(R,0.5f, 0.5f, 0.5f),
        new Shapes().circle(R,0.5f, 0.5f, 0.1f),
        new Shapes().circle(R,0.5f, 0.5f, 0.25f),
        new Shapes().square(R,0.5f, 0.5f, 0.5f),

        new Shapes()
            .square(R,0.33f, 0.5f, 0.25f)
            .square(G,0.66f, 0.5f, 0.25f),

        new Shapes()
            .square(R,0.5f, 0.33f - 0.05f, 0.18f)
            .square(G,0.5f, 0.66f + 0.05f, 0.18f),

        new Shapes()
            .square(R,0.25f, 0.5f, 0.1f)
            .square(R,0.5f, 0.5f, 0.1f)
            .square(R,0.75f, 0.5f, 0.1f),

        new Shapes()
            .square(R,0.25f, 0.5f, 0.1f)
            .square(G,0.5f, 0.5f, 0.1f)
            .square(B,0.75f, 0.5f, 0.1f),

        new Color(R),
        new Color(G),
        new Color(B),

        new Shapes()
            .red(0, 0, 0.5f, 1)
            .green(0.5f, 0, 1, 1),
        new Shapes()
            .red(0, 0, 1, 0.5f)
            .green(0, 0.5f, 1, 1),
        new Shapes()
            .red(0, 0, 0.33f, 1)
            .green(0.33f, 0, 0.66f, 1)
            .blue(0.66f, 0, 1, 1),
        new Shapes()
            .red(1/7f, 0, 2/7f, 1)
            .green(3/7f, 0, 4/7f, 1)
            .blue(5/7f, 0, 6/7f, 1),
        new Intersection(),
        new CircleZoom(),
        new Interlaced(),
        new Cube(),
        new Color(R, 50, 50),
        new Color(R, 100, 100),
        new Wiggles(),
        new WiggleCircle(),
        new Door(applet, mapCanvas.width, mapCanvas.height),
        new Cube(),
        new Image("data/maps/circuit.png")
    };

    this.seeds = new Animator.SimpleAnimator[] {
        new TinySeed(),

        new SeedShapes().square(0.5f, 0.5f, 0.1f),
        new SeedShapes().square(0.5f, 0.5f, 0.25f),
        new SeedShapes().square(0.5f, 0.5f, 0.5f),
        new SeedShapes().circle(0.5f, 0.5f, 0.1f),
        new SeedShapes().circle(0.5f, 0.5f, 0.25f),
        new SeedShapes().square(0.5f, 0.5f, 0.5f),

        new SeedShapes()
            .square(0.33f, 0.5f, 0.25f)
            .square(0.66f, 0.5f, 0.25f),

        new SeedShapes()
            .square(0.5f, 0.33f - 0.05f, 0.18f)
            .square(0.5f, 0.66f + 0.05f, 0.18f),

        new SeedShapes()
            .square(0.25f, 0.5f, 0.1f)
            .square(0.5f, 0.5f, 0.1f)
            .square(0.75f, 0.5f, 0.1f),

        new SeedShapes()
            .line(0.5f, 0, 0.5f, 1, 1),
        new SeedShapes()
            .line(0, 0.5f, 1, 0.5f, 1),
        new SeedShapes()
            .line(0.33f, 0, 0.33f, 1, 1)
            .line(0.66f, 0, 0.66f, 1, 1),
        new SeedShapes()
            .rect(1/7f, -0.1f, 2/7f, 1.1f)
            .rect(3/7f, -0.1f, 4/7f, 1.1f)
            .rect(5/7f, -0.1f, 6/7f, 1.1f),

        new Spinning(),
        new CircleZoomLines(),
        new VerticalLines(),
        new Rectangle(50, 50),
        new Rectangle(100, 100),
        new X(11),
        new Triangle(11),
        new Spinner(12),
        new Grid(10, 10, 1),
        new Grid(20, 20, 1),
        new Grid(30, 30, 1),
        new PianoSeed(),
        new Image("data/seeds/circuit.png")
    };
  }

  public void setKernelPackIndex(int kernel, int index) {
    state.kernelIndex[kernel][0] = index;

    if (state.kernelIndex[kernel][1] != -1) {
      setFrameskips(kernels[state.kernelIndex[kernel][0]][state.kernelIndex[kernel][1]].getFrameskips());
    }
  }

  public void setKernelIndex(int kernel, int index) {
    index = index % kernels[0].length;

    state.kernelIndex[kernel][1] = index;

    if (index != -1) {
      setFrameskips(kernels[state.kernelIndex[kernel][0]][state.kernelIndex[kernel][1]].getFrameskips());
    }
  }

  public void setFrameskips(int skips) {
    state.frameskips = skips;
  }

  public void setMapIndex(int index) {
    if (index < maps.length) {
      state.mapIndex = index;
    }
  }

  public void setSeedIndex(int index) {
    if (index < seeds.length) {
      state.seedIndex = index;
    }
  }

  public int[][] getKernelIndex() {
    return state.kernelIndex;
  }

  public int getMapIndex() {
    return state.mapIndex;
  }

  public int getSeedIndex() {
    return state.seedIndex;
  }

  public Animator[] getMaps() {
    return maps;
  }

  public Animator[] getSeeds() {
    return seeds;
  }

  private void updateKernel(List<Integer> notesDown, int kernelIndex) {
    if (notesDown.isEmpty()) {
      if (!state.stickyKernels) {
        setKernelIndex(kernelIndex, -1);
      }
    } else {
      setKernelIndex(kernelIndex, notesDown.get(notesDown.size() - 1));
    }
  }

  public PImage run() {
    Map<EngineInput, List<Integer>> inputs = new HashMap<>();
    for (EngineInput input : EngineInput.values()) {
      inputs.put(input, new ArrayList<>());
    }

    for (int i = 0; i < numRandomChannels; i++) {
      List<Integer> randomChannel = midiNotes.get(new Source(RANDOM, i));
      randomChannel.clear();
      randomChannel.add((int) (Math.random() * 12));
    }


    // TODO: fix race condition here
    for (Source source : midiNotes.keySet()) {
      if (state.mapping.getMappedInputs(source) != null) {
        for (EngineInput input : state.mapping.getMappedInputs(source)) {
          List<Integer> notes = midiNotes.get(source);
          for (int j = 0; j < notes.size(); j++) {
            Integer note = notes.get(j);
            if (!inputs.get(input).contains(note)) {
              inputs.get(input).add(note);
            }
          }
        }
      }
    }

    updateKernel(inputs.get(EngineInput.KERNEL_1), 0);
    updateKernel(inputs.get(EngineInput.KERNEL_2), 1);
    updateKernel(inputs.get(EngineInput.KERNEL_3), 2);

    // Clear out beat detections
    midiNotes.forEach((source, integers) -> {
      if (source.type == AUDIO) {
        integers.clear();
      }
    });

    // TODO: update maps + seeds selector

    for (int i = 0; i < 3; i++) {
      if (state.kernelIndex[i][1] == -1) {
        activeKernels[i] = blackKernel;
      } else {
        activeKernels[i] = kernels[state.kernelIndex[i][0]][state.kernelIndex[i][1]];
      }
    }

    if (Math.abs(state.scale - currentScale) > 0.1) {
      currentScale += (state.scale - currentScale) * SCALE_SPEED;
    } else {
      currentScale = state.scale;
    }


    if (state.mapIndex != -1 && maps[state.mapIndex] instanceof Animator.DoubleAnimator) {
      Animator.DoubleAnimator doubleAnimator = (Animator.DoubleAnimator) maps[state.mapIndex];

      mapCanvas.beginDraw();
      mapCanvas.background(0);
      doubleAnimator.drawMap(mapCanvas, applet.frameCount, inputs.get(EngineInput.MAP_INPUT));
      mapCanvas.endDraw();

      seedCanvas.beginDraw();
      seedCanvas.background(0);
      doubleAnimator.drawSeed(seedCanvas, applet.frameCount, inputs.get(EngineInput.MAP_INPUT));
      seedCanvas.endDraw();
    } else {
      mapCanvas.beginDraw();
      mapCanvas.background(0);
      if (state.mapIndex != -1) {
        Animator.SimpleAnimator simpleAnimator = (Animator.SimpleAnimator) maps[state.mapIndex];
        simpleAnimator.draw(mapCanvas, applet.frameCount, inputs.get(EngineInput.MAP_INPUT), currentScale);
      }
      mapCanvas.endDraw();

      seedCanvas.beginDraw();
      seedCanvas.background(0);
      if (state.seedIndex != -1) {
        seeds[state.seedIndex].draw(seedCanvas, applet.frameCount, inputs.get(EngineInput.SEED_INPUT), currentScale);
      }
      seedCanvas.endDraw();
    }

    renderer.setKernelMap(mapCanvas);
    renderer.setSeed(seedCanvas);
    renderer.setKernels(activeKernels);
    renderer.setTimeBlur(state.timeBlur);

    int computedFrameskips = state.frameskips;

    if (!pulse && !on) {
      computedFrameskips = 0;
    }

    PImage result = renderer.render(computedFrameskips);
    if (renderPromise != null) {
      previewRenderer.beginDraw();
      previewRenderer.background(0);
      int scale = Math.round(getScale());
      previewRenderer.image(
          result,
          previewRenderer.width / 2 - previewRenderer.width / 2 * scale,
          previewRenderer.height / 2 - previewRenderer.height / 2 * scale,
          previewRenderer.width * scale,
          previewRenderer.height * scale);
      previewRenderer.endDraw();

      renderPromise.image = previewRenderer.get();
      if (renderPromise.onResolve != null) {
        renderPromise.onResolve.resolve();
      }
      renderPromise = null;
    }
    return result;
  }

  public Kernel getKernel(int packIndex, int index) {
    return kernels[packIndex][index];
  }

  public Kernel[][] getKernels() {
    return kernels;
  }

  public Renderer getRenderer() {
    return renderer;
  }

  public PGraphics getMapCanvas() {
    return mapCanvas;
  }

  public PGraphics getSeedCanvas() {
    return seedCanvas;
  }

  public void setScale(float scale) {
    this.state.scale = scale;
  }

  public float getScale() {
    return currentScale;
  }

  public void setTimeBlur(float timeBlur) {
    this.state.timeBlur = timeBlur;
  }

  public float getTimeBlur() {
    return state.timeBlur;
  }

  @Midi(channel = 10, note = 1)
  public void toggleOn() {
    on = !on;
  }

  public boolean isStickyKernels() {
    return state.stickyKernels;
  }

  public boolean isStickySeeds() {
    return state.stickySeeds;
  }

  public boolean isStickyMaps() {
    return state.stickyMaps;
  }

  public void toggleStickyKernels() {
    state.stickyKernels = !state.stickyKernels;
  }

  public void toggleStickySeeds() {
    state.stickySeeds = !state.stickySeeds;
  }

  public void toggleStickyMaps() {
    state.stickyMaps = !state.stickyMaps;
  }

  public void setStickyKernels(boolean stickyKernels) {
    this.state.stickyKernels = stickyKernels;
  }

  public void setStickySeeds(boolean stickySeeds) {
    this.state.stickySeeds = stickySeeds;
  }

  public void setStickyMaps(boolean stickyMaps) {
    this.state.stickyMaps = stickyMaps;
  }

  public boolean isOn() {
    return on;
  }

  public EngineState getState() {
    return state;
  }

  public void setState(EngineState state) {
    this.state.set(state);
  }

  public RenderPromise getSnapshot() {
    this.renderPromise = new RenderPromise();
    return renderPromise;
  }

  @Override
  public void noteOn(String device, int channel, int note, int velocity) {
    midiNotes.get(new Source(Type.MIDI, channel)).add(note);
    Source source = new Source(Type.MIDI, channel);
    Set<EngineInput> mappedInputs = state.mapping.getMappedInputs(source);
    if (mappedInputs.contains(EngineInput.SEED_INPUT)) {
      seeds[state.seedIndex].noteOn(channel, note);
    }
    if (mappedInputs.contains(EngineInput.MAP_INPUT)) {
      maps[state.mapIndex].noteOn(channel, note);
    }
  }

  @Override
  public void noteOff(String device, int channel, int note) {
    Source source = new Source(Type.MIDI, channel);
    int i = midiNotes.get(source).indexOf(note);
    midiNotes.get(source).remove(i);
    Set<EngineInput> mappedInputs = state.mapping.getMappedInputs(source);
    if (mappedInputs.contains(EngineInput.SEED_INPUT)) {
      seeds[state.seedIndex].noteOff(channel, note);
    }
    if (mappedInputs.contains(EngineInput.MAP_INPUT)) {
      maps[state.mapIndex].noteOff(channel, note);
    }
  }

  public void onBeat(int channel) {
    Source source = new Source(AUDIO, channel);
    midiNotes.get(source).add((int) (Math.random() * 12));
  }

  public Map<Source, List<Integer>> getMidiNotes() {
    return midiNotes;
  }

  public EngineState[] getEngineStates() {
    return engineStates;
  }

  public void setStateIndex(int index) {
    currentEngineState = index;
    setState(engineStates[currentEngineState]);
  }

  public int getStateIndex() {
    return currentEngineState;
  }

  public int getNumMidiChannels() {
    return numMidiChannels;
  }

  public int getNumAudioChannels() {
    return numAudioChannels;
  }

  public int getNumRandomChannels() {
    return numRandomChannels;
  }
}
