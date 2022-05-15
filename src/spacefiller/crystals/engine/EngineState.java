package spacefiller.crystals.engine;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class EngineState implements Serializable {
  public int kernelIndex[][];
  public int mapIndex;
  public int seedIndex;
  public float scale = 5;
  public float timeBlur = 0.2f;
  public int frameskips = 1;
  public boolean stickyKernels = true;
  public boolean stickySeeds;
  public boolean stickyMaps;
  public MidiMapping mapping;

  public transient RenderPromise preview;

  public EngineState() {
    this.kernelIndex = new int[3][2];
    this.mapping = new MidiMapping();
  }

  public void set(EngineState state) {
    for (int i = 0; i < kernelIndex.length; i++) {
      for (int j = 0; j < kernelIndex[i].length; j++) {
        kernelIndex[i][j] = state.kernelIndex[i][j];
      }
    }
    this.mapping.set(state.mapping);
    this.mapIndex = state.mapIndex;
    this.seedIndex = state.seedIndex;
    this.scale = state.scale;
    this.frameskips = state.frameskips;
    this.stickyKernels = state.stickyKernels;
    this.stickySeeds = state.stickySeeds;
    this.stickyMaps = state.stickyMaps;
    this.timeBlur = state.timeBlur;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EngineState that = (EngineState) o;

    for (int i = 0; i < kernelIndex.length; i++) {
      for (int j = 0; j < kernelIndex[i].length; j++) {
        if (kernelIndex[i][j] != that.kernelIndex[i][j]) {
          return false;
        }
      }
    }

    return mapIndex == that.mapIndex &&
        seedIndex == that.seedIndex &&
        Float.compare(that.scale, scale) == 0 &&
        frameskips == that.frameskips &&
        stickyKernels == that.stickyKernels &&
        stickySeeds == that.stickySeeds &&
        stickyMaps == that.stickyMaps &&
        this.mapping.equals(((EngineState) o).mapping) &&
        timeBlur == that.timeBlur;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(mapIndex, seedIndex, scale, frameskips, stickyKernels, stickySeeds, stickyMaps);
    result = 31 * result + Arrays.hashCode(kernelIndex);
    return result;
  }
}
