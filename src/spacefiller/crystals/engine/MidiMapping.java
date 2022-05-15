package spacefiller.crystals.engine;

import java.io.Serializable;
import java.util.*;

public class MidiMapping implements Serializable {
   public static class Source implements Serializable {
      public Type type;
      public int channel;

      public Source(Type type, int channel) {
         this.type = type;
         this.channel = channel;
      }

      @Override
      public String toString() {
         return "Source{" + "type=" + type + ", channel=" + channel + '}';
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Source source = (Source) o;
         return channel == source.channel && type == source.type;
      }

      @Override
      public int hashCode() {
         return Objects.hash(type, channel);
      }
   }

   public enum Type {
      MIDI,
      AUDIO,
      RANDOM
   }

   public enum EngineInput {
      KERNEL_1,
      KERNEL_2,
      KERNEL_3,
      MAP_INPUT,
      SEED_INPUT,
      MAP_SELECTION,
      SEED_SELECTION
   }

   private Map<Source, Set<EngineInput>> mappings;

   public MidiMapping() {
      this.mappings = new HashMap<>();

      for (int i = 1; i < 4; i++) {
         mappings.put(new Source(Type.MIDI, i), new HashSet<>());
      }

      for (int i = 0; i < 4; i++) {
         mappings.put(new Source(Type.AUDIO, i), new HashSet<>());
      }

      for (int i = 0; i < 3; i++) {
         mappings.put(new Source(Type.RANDOM, i), new HashSet<>());
      }
   }

   public void addMapping(Source source, EngineInput input) {
      mappings.get(source).add(input);
   }

   public Set<Source> getChannels() {
      return mappings.keySet();
   }

   public Set<EngineInput> getMappedInputs(Source source) {
      return mappings.get(source);
   }

   public void set(MidiMapping other) {
      this.mappings.clear();
      for (Source s : other.mappings.keySet()) {
         this.mappings.put(s, new HashSet<>(other.mappings.get(s)));
      }
   }

   public void clearInputMappings(EngineInput input) {
      for (Set<EngineInput> engineInputs : mappings.values()) {
         engineInputs.remove(input);
      }
   }

   public void clearMidiMappings(Source source) {
      mappings.get(source).clear();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MidiMapping that = (MidiMapping) o;
      return Objects.equals(mappings, that.mappings);
   }

   @Override
   public int hashCode() {
      return Objects.hash(mappings);
   }
}
