package com.team5104.lib.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Processes bezier curves between two points.
 * Desmos Link: https://www.desmos.com/calculator/da8zwxpgzo */
public class BezierCurve {
  @JsonProperty("x1")
  public double x1;
  @JsonProperty("y1")
  public double y1;
  @JsonProperty("x2")
  public double x2;
  @JsonProperty("y2")
  public double y2;

  public BezierCurve(double x1, double y1, double x2, double y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /** Get value of curve at point x. Flips the curve for negative values. */
  public double getPoint(double x) {
    boolean tn = x < 0;
    x = Math.abs(x);
    double x0 = 0.0;
    double y0 = 0.0;
    double x3 = 1.0;
    double y3 = 1.0;
    x = (1 - x);
    x = 1 - (((1 - x) * p(x, x0, x1, x2)) + (x * p(x, x1, x2, x3)));
    x = (((1 - x) * p(x, y0, y1, y2)) + (x * p(x, y1, y2, y3)));
    return x * (tn ? -1 : 1);
  }

  private double p(double x, double a, double b, double c) {
    return ((1 - x) * ((1 - x) * a + (x * b))) + (x * ((1 - x) * b + (x * c)));
  }
}

