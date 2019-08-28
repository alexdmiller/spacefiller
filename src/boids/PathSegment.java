package boids;

import processing.core.PVector;

public class PathSegment {
    public PVector p1;
    public PVector p2;

    public PathSegment(PVector p1, PVector p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
