package de.lmu.ifi.dbs.elki.data.spatial;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;

/**
 * Utility class with spatial functions.
 * 
 * @author Erich Schubert
 */
// IMPORTANT NOTE: when editing this class, bear in mind that the dimension numbers start at 1!
public final class SpatialUtil {
  /**
   * Returns a clone of the minimum hyper point.
   * 
   * @return the minimum hyper point
   */
  public static double[] getMin(SpatialComparable box) {
    final int dim = box.getDimensionality();
    double[] min = new double[dim];
    for(int i = 0; i < dim; i++) {
      min[i] = box.getMin(i + 1);
    }
    return min;
  }

  /**
   * Returns a clone of the maximum hyper point.
   * 
   * @return the maximum hyper point
   */
  public static double[] getMax(SpatialComparable box) {
    final int dim = box.getDimensionality();
    double[] max = new double[dim];
    for(int i = 0; i < dim; i++) {
      max[i] = box.getMax(i + 1);
    }
    return max;
  }

  /**
   * Returns true if this HyperBoundingBox and the given HyperBoundingBox
   * intersect, false otherwise.
   * 
   * @param box the HyperBoundingBox to be tested for intersection
   * @return true if this HyperBoundingBox and the given HyperBoundingBox
   *         intersect, false otherwise
   */
  public static boolean intersects(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("The spatial objects do not have the same dimensionality!");
    }
    boolean intersect = true;
    for(int i = 1; i <= dim; i++) {
      if(box1.getMin(i) > box2.getMax(i) || box1.getMax(i) < box2.getMin(i)) {
        intersect = false;
        break;
      }
    }
    return intersect;
  }

  /**
   * Returns true if this SpatialComparable contains the given
   * SpatialComparable, false otherwise.
   * 
   * @param box the SpatialComparable to be tested for containment
   * @return true if this SpatialComparable contains the given
   *         SpatialComparable, false otherwise
   */
  public static boolean contains(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("The spatial objects do not have the same dimensionality!");
    }

    boolean contains = true;
    for(int i = 1; i <= dim; i++) {
      if(box1.getMin(i) > box2.getMin(i) || box1.getMax(i) < box2.getMax(i)) {
        contains = false;
        break;
      }
    }
    return contains;
  }

  /**
   * Returns true if this SpatialComparable contains the given point, false
   * otherwise.
   * 
   * @param point the point to be tested for containment
   * @return true if this SpatialComparable contains the given point, false
   *         otherwise
   */
  public static boolean contains(SpatialComparable box, double[] point) {
    final int dim = box.getDimensionality();
    if(dim != point.length) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given point need same dimensionality");
    }

    boolean contains = true;
    for(int i = 0; i < dim; i++) {
      if(box.getMin(i + 1) > point[i] || box.getMax(i + 1) < point[i]) {
        contains = false;
        break;
      }
    }
    return contains;
  }

  /**
   * Computes the volume of this HyperBoundingBox
   * 
   * @return the volume of this SpatialComparable
   */
  public static double volume(SpatialComparable box) {
    double vol = 1;
    final int dim = box.getDimensionality();
    for(int i = 1; i <= dim; i++) {
      vol *= box.getMax(i) - box.getMin(i);
    }
    return vol;
  }

  /**
   * Computes the perimeter of this SpatialComparable.
   * 
   * @return the perimeter of this SpatialComparable
   */
  public static double perimeter(SpatialComparable box) {
    double perimeter = 0;
    final int dim = box.getDimensionality();
    for(int i = 1; i <= dim; i++) {
      perimeter += box.getMax(i) - box.getMin(i);
    }
    return perimeter;
  }

  /**
   * Computes the volume of the overlapping box between this HyperBoundingBox
   * and the given HyperBoundingBox and return the relation between the volume
   * of the overlapping box and the volume of both HyperBoundingBoxes.
   * 
   * @param box the HyperBoundingBox for which the intersection volume with this
   *        HyperBoundingBox should be computed
   * @return the relation between the volume of the overlapping box and the
   *         volume of this HyperBoundingBox and the given HyperBoundingBox
   */
  public static double overlap(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given HyperBoundingBox need same dimensionality");
    }

    // the maximal and minimal value of the overlap box.
    double omax, omin;

    // the overlap volume
    double overlap = 1.0;

    for(int i = 1; i <= dim; i++) {
      // The maximal value of that overlap box in the current
      // dimension is the minimum of the max values.
      omax = Math.min(box1.getMax(i), box2.getMax(i));
      // The minimal value is the maximum of the min values.
      omin = Math.max(box1.getMin(i), box2.getMin(i));

      // if omax <= omin in any dimension, the overlap box has a volume of zero
      if(omax <= omin) {
        return 0.0;
      }

      overlap *= omax - omin;
    }

    return overlap / (volume(box1) + volume(box2));
  }

  /**
   * Computes the union HyperBoundingBox of this HyperBoundingBox and the given
   * HyperBoundingBox.
   * 
   * @param box the HyperBoundingBox to be united with this HyperBoundingBox
   * @return the union HyperBoundingBox of this HyperBoundingBox and the given
   *         HyperBoundingBox
   */
  public static HyperBoundingBox union(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given HyperBoundingBox need same dimensionality");
    }

    double[] min = new double[dim];
    double[] max = new double[dim];

    for(int i = 1; i <= dim; i++) {
      min[i - 1] = Math.min(box1.getMin(i), box2.getMin(i));
      max[i - 1] = Math.max(box1.getMax(i), box2.getMax(i));
    }
    return new HyperBoundingBox(min, max);
  }

  /**
   * Returns the centroid of this SpatialComparable.
   * 
   * @param obj Spatial object to process
   * @return the centroid of this SpatialComparable
   */
  public static double[] centroid(SpatialComparable obj) {
    final int dim = obj.getDimensionality();
    double[] centroid = new double[dim];
    for(int d = 1; d <= dim; d++) {
      centroid[d - 1] = (obj.getMax(d) + obj.getMin(d)) / 2.0;
    }
    return centroid;
  }

  /**
   * Returns the centroid of the specified values of this HyperBoundingBox.
   * 
   * @param obj Spatial object to process
   * @param start the start dimension to be considered
   * @param end the end dimension to be considered
   * @return the centroid of the specified values of this HyperBoundingBox
   */
  public static double[] centroid(SpatialComparable obj, int start, int end) {
    double[] centroid = new double[end - start + 1];
    for(int d = start - 1; d < end; d++) {
      centroid[d - start + 1] = (obj.getMax(d + 1) + obj.getMin(d + 1)) / 2.0;
    }
    return centroid;
  }
}