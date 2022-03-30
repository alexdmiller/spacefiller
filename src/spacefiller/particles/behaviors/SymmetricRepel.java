package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class SymmetricRepel extends SymmetricBehavior {
    private float repelThreshold;
    private float repelStrength;
    private float distanceEpsilon;
    private boolean teams = false;

    public SymmetricRepel(float repelThreshold, float repelStrength, float epsilon) {
        this.repelThreshold = repelThreshold;
        this.repelStrength = repelStrength;
        this.distanceEpsilon = epsilon;
    }

    public SymmetricRepel(float repelThreshold, float repelStrength) {
        this.repelThreshold = repelThreshold;
        this.repelStrength = repelStrength;
        this.distanceEpsilon = 0.01f;
    }

    public float getRepelThreshold() {
        return repelThreshold;
    }

    public void setRepelThreshold(float repelThreshold) {
        this.repelThreshold = repelThreshold;
    }

    public float getRepelStrength() {
        return repelStrength;
    }

    public void setRepelStrength(float repelStrength) {
        this.repelStrength = repelStrength;
    }

    public void setTeams(boolean teams) {
        this.teams = teams;
    }

    @Override
    public void apply(Particle particle, Particle other) {
        if (!teams || particle.getTeam() == other.getTeam()) {
            Vector delta = Vector.sub(particle.getPosition(), other.getPosition());
            float mag = (float) delta.magnitude();
            if (mag < repelThreshold) {
                float force = repelStrength / (mag * mag + distanceEpsilon);
                delta.mult(force);
                particle.applyForce(delta);
                other.applyForce(Vector.mult(delta, -1));
            }
        }
    }
}
