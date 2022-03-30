package spacefiller.apps.tests;

import processing.core.PApplet;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.behaviors.ParticleFriction;
import spacefiller.particles.behaviors.RepelParticles;
import spacefiller.particles.behaviors.SoftBounds;

public class ParticleApp extends PApplet {
    public static void main(String[] args) {
        PApplet.main("spacefiller.apps.tests.ParticleApp");
    }

    ParticleSystem system;

    public void settings() {
        size(500, 500);
    }

    public void setup() {
        system = new ParticleSystem(width, height);
        system.fillWithParticles(500);
        system.addBehavior(new SoftBounds());
        system.addBehavior(new ParticleFriction(0.99f));
        system.addBehavior(new RepelParticles(20, 1));
    }

    public void draw() {
        background(0);
        noStroke();
        fill(255);
        system.update();
        system.getParticles().forEach(p -> {
            rect(p.getPosition().x, p.getPosition().y, 2, 2);
        });
    }
}
