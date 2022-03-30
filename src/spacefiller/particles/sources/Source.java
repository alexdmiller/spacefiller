package spacefiller.particles.sources;

import spacefiller.math.Vector;

public interface Source {
  Vector generatePoint();
  int getSpawnRate();
  int getDimension();
  int getTeam();
}
