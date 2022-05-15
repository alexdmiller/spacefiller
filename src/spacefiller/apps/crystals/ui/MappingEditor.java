package spacefiller.apps.crystals.ui;

import crystal.audio.Audio;
import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.MidiMapping;
import spacefiller.crystals.engine.MidiMapping.Source;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.*;

import static crystal.engine.MidiMapping.Type.*;

public class MappingEditor extends Component {
  private List<ClickNode> nodes;
  private Map<Source, MidiClickNode> midiClickNodeMap;
  private Map<MidiMapping.EngineInput, EngineInputNode> engineInputNodeMap;
  private Engine engine;

  private ClickNode lastClicked;
  private boolean clearKeyDown;

  public MappingEditor(Engine engine, Audio audio, PApplet applet) {
    super(applet);

    this.engine = engine;
    this.nodes = new ArrayList<>();
    this.midiClickNodeMap = new HashMap<>();
    this.engineInputNodeMap = new HashMap<>();

    int y = 0;
    for (int i = 0; i < engine.getNumMidiChannels(); i++) {
      Source source = new Source(MIDI, i + 1);
      midiClickNodeMap.put(
          source,
          new MidiClickNode(source, new PVector(0, y)));
      y += 30;
    }

    for (int i = 0; i < engine.getNumAudioChannels(); i++) {
      Source source = new Source(AUDIO, i);
      midiClickNodeMap.put(
          source,
          new MidiClickNode(source, new PVector(0, y)));
      y += 30;
    }

    for (int i = 0; i < engine.getNumRandomChannels(); i++) {
      Source source = new Source(RANDOM, i);
      midiClickNodeMap.put(
          source,
          new MidiClickNode(source, new PVector(0, y)));
      y += 30;
    }

    nodes.addAll(midiClickNodeMap.values());

    y = 0;
    for (MidiMapping.EngineInput input : MidiMapping.EngineInput.values()) {
      EngineInputNode engineInputNode = new EngineInputNode(input, new PVector(200, y));
      engineInputNodeMap.put(input, engineInputNode);
      nodes.add(engineInputNode);
      y += 30;
    }

  }

  @Override
  protected void doDraw() {
    for (ClickNode node : nodes) {
      node.draw();
    }

    for (MidiClickNode midiClickNode : midiClickNodeMap.values()) {
      Set<MidiMapping.EngineInput> mappedInputs =
          engine.getState().mapping.getMappedInputs(midiClickNode.source);
      for (MidiMapping.EngineInput input : mappedInputs) {
        EngineInputNode engineInputNode = engineInputNodeMap.get(input);
        applet.stroke(200);
        applet.strokeWeight(2);
        applet.line(
            midiClickNode.position.x + 30, midiClickNode.position.y + 15,
            engineInputNode.position.x, engineInputNode.position.y + 15);
        applet.strokeWeight(1);
      }
    }

    if (lastClicked != null) {
      applet.strokeWeight(2);
      applet.line(lastClicked.position.x, lastClicked.position.y, mouse.x, mouse.y);
      applet.strokeWeight(1);
    }
  }

  @Override
  public float getWidth() {
    return 320;
  }

  @Override
  public float getHeight() {
    return 250;
  }

  @Override
  public void mousePressed(PVector mouse) {
    super.mousePressed(mouse);

    for (ClickNode clickNode : nodes) {
      if (clickNode.isInside(mouse.x, mouse.y)) {

        if (clearKeyDown) {
          if (clickNode instanceof MidiClickNode) {
            MidiClickNode midiClickNode = (MidiClickNode) clickNode;
            engine.getState().mapping.clearMidiMappings(midiClickNode.source);
          } else if (clickNode instanceof EngineInputNode) {
            EngineInputNode engineInputNode = (EngineInputNode) clickNode;
            engine.getState().mapping.clearInputMappings(engineInputNode.input);
          }
        } else if (lastClicked != null) {
          ClickNode clicked = clickNode;

          MidiClickNode midiClickNode = null;
          EngineInputNode engineInputNode = null;

          if (lastClicked instanceof MidiClickNode && clicked instanceof EngineInputNode) {
            midiClickNode = (MidiClickNode) lastClicked;
            engineInputNode = (EngineInputNode) clicked;
            engine.getState().mapping.addMapping(midiClickNode.source, engineInputNode.input);
          } else if (lastClicked instanceof EngineInputNode && clicked instanceof MidiClickNode) {
            midiClickNode = (MidiClickNode) clicked;
            engineInputNode = (EngineInputNode) lastClicked;
            engine.getState().mapping.addMapping(midiClickNode.source, engineInputNode.input);
          }
          lastClicked = null;
        } else {
          lastClicked = clickNode;
        }
      }
    }

  }

  @Override
  public void mouseReleased(PVector mouse) {
    super.mouseReleased(mouse);
  }

  @Override
  public void keyPressed(KeyEvent event) {
    super.keyPressed(event);
    if (event.isMetaDown()) {
      clearKeyDown = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent event) {
    super.keyReleased(event);

    if (event.isMetaDown()) {
      clearKeyDown = false;
    }
  }

  private static class ClickNode {
    PVector position;

    public ClickNode(PVector position) {
      this.position = position;
    }

    public void draw() { }
    public boolean isInside(float x, float y) { return false; }
  }

  private class MidiClickNode extends ClickNode {
    Source source;
    int size = 30;

    public MidiClickNode(Source source, PVector position) {
      super(position);
      this.source = source;
    }

    @Override
    public void draw() {
      applet.pushMatrix();
      applet.translate(position.x, position.y);
      applet.noFill();
      applet.stroke(100);
      applet.rectMode(PConstants.CORNERS);
      applet.rect(0, 0, 30, 30);
      applet.fill(255);
      applet.textAlign(PConstants.LEFT, PConstants.TOP);
      applet.text(source.type + " " + (source.channel + 1), 5, 7);
      applet.popMatrix();
    }

    @Override
    public boolean isInside(float x, float y) {
      return x > position.x
          && x < position.x + size
          && y > position.y
          && y < position.y + size;
    }
  }

  private class EngineInputNode extends ClickNode {
    MidiMapping.EngineInput input;
    int size = 30;

    public EngineInputNode(MidiMapping.EngineInput input, PVector position) {
      super(position);
      this.input = input;
    }

    @Override
    public void draw() {
      applet.pushMatrix();
      applet.translate(position.x, position.y);
      applet.noFill();
      applet.stroke(100);
      applet.rectMode(PConstants.CORNER);
      applet.rect(0, 0, 120, 30);
      applet.fill(255);
      applet.textAlign(PConstants.LEFT, PConstants.TOP);
      applet.text(input.toString(), 5, 7);
      applet.popMatrix();
    }

    @Override
    public boolean isInside(float x, float y) {
      return x > position.x
          && x < position.x + 120
          && y > position.y
          && y < position.y + size;
    }
  }
}
