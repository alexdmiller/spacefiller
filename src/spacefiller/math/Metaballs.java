package spacefiller.math;

import spacefiller.math.sdf.FloatField2;

import java.util.ArrayList;
import java.util.List;

import static spacefiller.math.PerlinNoise.noise;

public class Metaballs implements FloatField2 {
  public static class Ball {
    public Ball(Vector position, float radius, float polarity) {
      this.position = position;
      this.radius = radius;
      this.polarity = polarity;
    }

    public Vector position;
    public float radius;
    public float polarity;
  }

  private List<Ball> balls;
  private View defaultView;
  private float noiseAmount;
  private float noiseSpeed;
  private float noisePosition;
  private float noiseScale = 0.01f;

  public Metaballs(float noiseAmount, float noiseSpeed) {
    this.balls = new ArrayList<>();
    this.defaultView = new View(0, 1);
    this.noiseAmount = noiseAmount;
    this.noiseSpeed = noiseSpeed;
  }

  public Ball create(Vector position, float radius) {
    return create(position, radius, 1);
  }

  public Ball create(Vector position, float radius, float polarity) {
    Ball ball = new Ball(position, radius, polarity);
    balls.add(ball);
    return ball;
  }

  public void update() {
    noisePosition += noiseSpeed;
  }

  public Ball getBallAt(Vector position) {
    return balls.stream()
        .filter(b -> b.position.dist(position) < b.radius)
        .findFirst()
        .orElse(null);
  }

  @Override public float get(float x, float y) {
    return defaultView.get(x, y);
  }

  public View getView(float radius) {
    return new View(0, radius);
  }

  public View getView(float inner, float outer) {
    return new View(inner, outer);
  }

  public class View implements FloatField2 {
    private float outerRadius = 1;
    private float innerRadius = 0;

    public View(float innerRadius, float outerRadius) {
      this.outerRadius = outerRadius;
      this.innerRadius = innerRadius;
    }

    @Override public float get(float x, float y) {
      Vector position = new Vector(x, y);

      float sum = 1;
      for (Ball ball : balls) {
        float outerRadiusPixels = outerRadius * ball.radius;
        float innerRadiusPixels = innerRadius * ball.radius;

        Vector dist = Vector.sub(position, ball.position);
        sum -= Math.pow((outerRadiusPixels / 2 - innerRadiusPixels / 2), 2) /
            Math.pow(dist.magnitude() - (outerRadiusPixels / 2 + innerRadiusPixels / 2), 2);
      }

      sum += (noise(x * noiseScale, y * noiseScale, noisePosition) - 0.5f) * noiseAmount;

      if (sum < 0) {
        sum = 0;
      }

//      for (Ball ball : balls) {
//        if (ball.polarity < 0) {
//          Vector dist = Vector.sub(position, ball.position);
//          sum -= ball.polarity * (ball.radius * ball.radius) / dist.magnitudeSquared();
//        }
//      }

      return sum;
    }
  }
}
