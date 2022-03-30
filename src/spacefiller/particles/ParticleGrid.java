package spacefiller.particles;

import spacefiller.math.Vector;

import java.util.*;
import java.util.stream.Stream;

public class ParticleGrid {
    private Bounds bounds;
    private List<Particle> particles;
    private Map<ParticleTag, List<Particle>> globalTagIndex;
    private Cell[] hash;
    private int rows, cols;
    private float cellSize;

    public ParticleGrid(Bounds bounds, float cellSize) {
        this.cellSize = cellSize;
        this.bounds = bounds;
        this.rows = (int) Math.ceil((bounds.getHeight() + cellSize) / cellSize);
        this.cols = (int) Math.ceil((bounds.getWidth() + cellSize) / cellSize);
        this.hash = new Cell[rows * cols];
        for (int i = 0; i < hash.length; i++) {
            hash[i] = new Cell();
        }
        particles = new ArrayList<>();
        globalTagIndex = new HashMap<>();
    }

    public void addParticle(Particle particle) {
        particles.add(particle);

        for (ParticleTag tag : particle.getTags()) {
            addTag(particle, tag);
        }

        if (bounds.contains(particle.getPosition())) {
            hash[hashPosition(particle.getPosition())].add(particle);
        }
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void removeParticle(Particle particle) {
        int oldHash = hashPosition(particle.getPosition());
        if (oldHash >= 0 && oldHash < hash.length) {
            hash[oldHash].remove(particle);
        }
        particles.remove(particle);

        for (Spring spring : particle.getConnections()) {
            spring.removeFlag = true;
        }

        for (ParticleTag tag : particle.getTags()) {
            removeTag(particle, tag);
        }

    }

    public void findOrphans() {
        List<Particle> copy = new ArrayList<>(particles);

        for (Particle p : copy) {
            boolean found = false;

            for (Cell c : getCells()) {
                if (c.getParticles().contains(p)) {
                    found = true;
                }
            }

            if (!found) {
                System.out.println("Particle " + p + " found in particle list but not in cells");
            }
        }


        // TODO: why are there particles in the cells that aren't in the particle list?
        // are particles being removed? where?
        for (Cell c : getCells()) {
            for (Particle p : c.getParticles()) {
                boolean found = false;

                if (copy.contains(p)) {
                    found = true;
                }

                if (!found) {
                    System.out.println(p.getPosition());
                    System.out.println("Particle " + p + " found in cells but not in particle list");
                }
            }
        }

        for (Cell c : getCells()) {
            for (Particle p1 : c.getParticles()) {
                int count = 0;

                for (Cell c2 : getCells()) {
                    for (Particle p2 : c2.getParticles()) {
                        if (p1 == p2) {
                            count++;
                        }
                    }
                }

                System.out.println(count);
                if (count != 1) {
                    System.out.println("BAD");
                }
            }
        }


    }

    public int hashPosition(Vector position) {
        int row = (int) (Math.floor(position.y / cellSize));
        int col = (int) (Math.floor(position.x / cellSize));

        if (row < 0 || row > rows || col < 0 || col > cols) {
            return -1;
        }

        return row * cols + col;
    }

    public void updateCell(Particle p, int oldHash) {
        int newHash = hashPosition(p.getPosition());
        if (newHash != oldHash) {
            synchronized (hash) {
                if (oldHash > -1) {
                    hash[oldHash].remove(p);
                }
                if (newHash > -1) {
                    hash[newHash].add(p);
                }
            }
        }
    }

    protected Stream<Particle> getNeighbors(Vector position, ParticleTag tag) {
        int cx = (int) (Math.floor(position.x / cellSize));
        int cy = (int) (Math.floor(position.y / cellSize));

        Stream stream = Stream.empty();
        if (cx >= 0 && cx < cols && cy >= 0 && cy < rows) {

            for (int x = cx - 1; x <= cx + 1; x++) {
                for (int y = cy - 1; y <= cy + 1; y++) {
                    if (x >= 0 && x < cols && y >= 0 && y < rows) {
                        Cell cell = hash[x + y * cols];
                        Collection<Particle> particles = tag == null ? cell.getParticles() : cell.getParticlesByTag(tag);
                        stream = Stream.concat(stream, particles.stream());
                    }
                }
            }
        }

        return stream;
    }

    public void addTag(Particle p, ParticleTag tag) {
        int hashPos = hashPosition(p.getPosition());
        if (hashPos >= 0 && hashPos < hash.length) {
            hash[hashPos].addTag(p, tag);
        }

        if (!globalTagIndex.containsKey(tag)) {
            synchronized (globalTagIndex) {
                globalTagIndex.put(tag, new ArrayList<>());
            }
        }

        if (!globalTagIndex.get(tag).contains(p)) {
            globalTagIndex.get(tag).add(p);
        }
    }

    public void removeTag(Particle p, ParticleTag tag) {
        int hashPos = hashPosition(p.getPosition());
        if (hashPos >= 0 && hashPos < hash.length) {
            hash[hashPos].removeTag(p, tag);
        }

        if (globalTagIndex.containsKey(tag)) {
            globalTagIndex.get(tag).remove(p);
        }
    }

    public List<Particle> getParticlesWithTag(ParticleTag tag) {
        synchronized (globalTagIndex) {
            return globalTagIndex.containsKey(tag) ? globalTagIndex.get(tag) : Collections.emptyList();
        }
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Cell getCell(int x, int y) {
        int index = x + y * cols;
        if (index >= 0 && index < hash.length) {
            return hash[x + y * cols];
        } else {
            return null;
        }
    }

    public Map<ParticleTag, List<Particle>> getAllTags() {
        return globalTagIndex;
    }

  public Cell[] getCells() {
    return hash;
  }

  public class Cell {
        List<Particle> particles;
        Map<ParticleTag, List<Particle>> tagIndex;

        // todo: add, remove
        public Cell() {
            particles = new ArrayList<>();
            tagIndex = new HashMap<>();
        }

        public void add(Particle particle) {
            particles.add(particle);

            for (ParticleTag tag : particle.getTags()) {
                addTag(particle, tag);
            }
        }

        public void remove(Particle particle) {
            particles.remove(particle);

            for (ParticleTag tag : particle.getTags()) {
                removeTag(particle, tag);
            }
        }

        public List<Particle> getParticlesByTag(ParticleTag tag) {
            return tagIndex.containsKey(tag) ? tagIndex.get(tag) : Collections.emptyList();
        }

        public List<Particle> getParticles() {
            return new ArrayList<>(particles);
        }

        public void addTag(Particle particle, ParticleTag tag) {
            if (!tagIndex.containsKey(tag)) {
                tagIndex.put(tag, new ArrayList<>());
            }

            if (!tagIndex.get(tag).contains(particle)) {
                tagIndex.get(tag).add(particle);
            }
        }

        public void removeTag(Particle particle, ParticleTag tag) {
            if (tagIndex.containsKey(tag)) {
                tagIndex.get(tag).remove(particle);
            }
        }
    }

    public float getCellSize() {
        return cellSize;
    }
}
