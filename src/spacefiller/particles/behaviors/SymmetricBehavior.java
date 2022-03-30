package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;

import java.util.stream.Stream;

public abstract class SymmetricBehavior {
    private ParticleSystem particleSystem;
    private ParticleTag applyTo;

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
    public void setParticleSystem(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
    }
    public abstract void apply(Particle particle, Particle other);

    public void setTagConstraint(ParticleTag tag) {
        this.applyTo = tag;
    }

    public ParticleTag getTag() {
        return applyTo;
    }
}
