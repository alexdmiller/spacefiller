package lab;

import javafx.util.Pair;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;

import java.util.*;

/**
 * Created by miller on 10/14/17.
 */
public class Evolve extends PApplet {
  private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz ";
  private static final float MATE_CHANCE = 0.9f;
  private static final float MUTATE_CHANCE = 0.00001f;

  public static void main(String[] args) {
    main("lab.Evolve");
  }


  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    LSystemRules r1 = LSystemRules.createRandom();
    LSystemRules r2 = LSystemRules.createRandom();
    System.out.println(r1);
    System.out.println(r2);

    LSystemRules[] cross = LSystemRules.cross(r1, r2);

    r1.score();
  }

//  public void setup() {
//    String goalString = "hello world";
//    FitnessComparator comparator = new FitnessComparator(goalString);
//
//    List<String> population = new ArrayList<>();
//    int populationSize = 500;
//
//    for (int i = 0; i < populationSize; i++) {
//      String s = "";
//      for (int j = 0; j < goalString.length(); j++) {
//        s += randomSymbol();
//      }
//      population.add(s);
//    }
//
//    for (int i = 0; i < 20; i++) {
//      Collections.sort(population, comparator);
//      List<String> newPopulation = new ArrayList<>();
//
//      while (newPopulation.size() < populationSize) {
//        String s1 = population.remove(0);
//        String s2 = population.remove(0);
//
//        String b1 = "";
//        String b2 = "";
//        if (Math.random() < MATE_CHANCE) {
//          int crossover = (int) Math.floor(Math.random() * s1.length());
//          b1 = s1.substring(0, crossover) + s2.substring(crossover);
//          b2 = s2.substring(0, crossover) + s1.substring(crossover);
//        } else {
//          b1 = s1;
//          b2 = s2;
//        }
//
//        for (int k = 0; k < b1.length(); k++) {
//          if (Math.random() < MUTATE_CHANCE) {
//            b1 = b1.substring(0, k) + randomSymbol() + b1.substring(k + 1);
//          }
//
//          if (Math.random() < MUTATE_CHANCE) {
//            b2 = b2.substring(0, k) + randomSymbol() + b2.substring(k + 1);
//          }
//        }
//
//        newPopulation.add(b1);
//        newPopulation.add(b2);
//      }
//      population = newPopulation;
//    }
//
//    Collections.sort(population, comparator);
//
//    System.out.println(population);
//
//    String axiom = "F++F++F";
//
//    for (int i = 0; i < 3; i++) {
//      axiom = axiom.replace("F", "F-F++F-F");
//    }
//
//    translate(width / 2, height / 2);
//    scale(0.2f);
//
//    background(0);
//    stroke(255);
//    strokeWeight(4);
//    drawPath(axiom);
//  }

  private void drawPath(String axiom) {
    for (int i = 0; i < axiom.length(); i++) {
      if (axiom.charAt(i) == 'F') {
        line(0, 0, 50, 0);
        translate(50, 0);
      } else if (axiom.charAt(i) == '+') {
        rotate((float) (Math.PI / 3));
      } else if (axiom.charAt(i) == '-') {
        rotate((float) (-Math.PI / 3));
      }
    }
  }

  public void draw() {
  }

  private static char randomSymbol() {
    return SYMBOLS.charAt((int) Math.floor(Math.random() * SYMBOLS.length()));
  }

  static class LSystemRules implements Comparable<LSystemRules> {
    public static final int AXIOM_LENGTH = 5;
    public static final int RULE_LENGTH = 10;
    public static final int NUM_RULES = 4;
    public static final String AVAILABLE_SYMBOLS = "F-+            ";

    String axiom;
    List<Pair<String, String>> replacementRules;

    private boolean run = false;
    private PVector position;

    public LSystemRules(String axiom) {
      this.axiom = axiom;
      this.replacementRules = new ArrayList<>();
      position = new PVector();
    }

    public void addRule(String symbol, String replacement) {
      replacementRules.add(new Pair<String, String>(symbol, replacement));
    }

    public String toString() {
      String ruleString = "";
      for (Pair<String, String> rule : replacementRules) {
        ruleString += rule.getKey() + rule.getValue();
      }
      return axiom + ruleString;
    }

    public static LSystemRules fromString(String ruleString) {
      LSystemRules rules = new LSystemRules(ruleString.substring(0, AXIOM_LENGTH));
      for (int i = 0; i < NUM_RULES; i++) {
        int offset = AXIOM_LENGTH + i * (RULE_LENGTH + 1);
        rules.addRule(
            ruleString.substring(offset, offset + 1),
            ruleString.substring(offset + 1, offset + RULE_LENGTH + 1));
      }
      return rules;
    }

    public static LSystemRules createRandom() {
      String axiom = "";
      for (int i = 0; i < AXIOM_LENGTH; i++) {
        axiom += randomSymbol();
      }
      LSystemRules rules = new LSystemRules(axiom);

      for (int i = 0; i < NUM_RULES; i++) {
        String symbol = randomSymbol() + "";
        String rule = "";
        for (int j = 0; j < RULE_LENGTH; j++) {
          rule += randomSymbol();
        }
        rules.addRule(symbol, rule);
      }

      return rules;
    }

    private static char randomSymbol() {
      return AVAILABLE_SYMBOLS.charAt((int) Math.floor(Math.random() * AVAILABLE_SYMBOLS.length()));
    }

    public LSystemRules mutate() {
      String s = this.toString();
      for (int i = 0; i < s.length(); i++) {
        if (Math.random() < MUTATE_CHANCE) {
          s = s.substring(0, i) + randomSymbol() + s.substring(i + 1);
        }
      }

      return LSystemRules.fromString(s);
    }

    public float score() {
      if (!run) {
        String a = axiom;
        for (int i = 0; i < 5; i++) {
          System.out.println(a.length());
          for (Pair<String, String> rule : replacementRules) {
            if (!rule.getKey().equals(" ")) {
              a = a.replace(rule.getKey(), rule.getValue());
            }
          }
        }

        System.out.println("ready");
        System.out.println(a);
      }

      return 0;
    }

    @Override
    public int compareTo(LSystemRules o) {
      if (this.score() > o.score()) {
        return 1;
      } else if (this.score() < o.score()) {
        return -1;
      } else {
        return 0;
      }
    }

    public static LSystemRules[] cross(LSystemRules r1, LSystemRules r2) {
      String s1 = r1.toString();
      String s2 = r2.toString();

      int crossover = (int) Math.floor(Math.random() * s1.length());
      String b1 = s1.substring(0, crossover) + s2.substring(crossover);
      String b2 = s2.substring(0, crossover) + s1.substring(crossover);

      return new LSystemRules[] { LSystemRules.fromString(b1), LSystemRules.fromString(b2)};
    }
  }

  static class FitnessComparator implements Comparator<String> {
    private String goal;

    public FitnessComparator(String goal) {
      this.goal = goal;
    }

    public float computeFitness(String s) {
      int numCorrect = 0;
      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == goal.charAt(i)) {
          numCorrect++;
        }
      }

      return (float) (Math.pow(2, (float) numCorrect / s.length()));
    }

    public int compare(String a, String b) {
      if (computeFitness(a) > computeFitness(b)) {
        return -1;
      } else if (computeFitness(a) < computeFitness(b)) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
