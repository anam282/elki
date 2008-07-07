package de.lmu.ifi.dbs.elki.distance.distancefunction;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.IntListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.ListGreaterEqualConstraint;

import java.util.BitSet;
import java.util.List;

/**
 * Provides a distance function that computes the distance
 * (which is a double distance) between feature vectors only in specified dimensions.
 *
 * @author Elke Achtert
 */
public abstract class AbstractDimensionsSelectingDoubleDistanceFunction<V extends NumberVector<V, ?>>
    extends AbstractDoubleDistanceFunction<V> {

  /**
   * Option string for parameter dims.
   */
  public static final String DIMS_P = "dims";

  /**
   * Description for parameter dim.
   */
  public static final String DIMS_D = "<d_1,...,d_n> a comma separated array of integer " +
                                      "values, where 1 <= d_i <= the " +
                                      "dimensionality of the feature space " +
                                      "specifying the dimensions to be considered " +
                                      "for distance computation. If this parameter is not set, " +
                                      "no dimensions will be considered, i.e. the distance between " +
                                      "two objects is always 0.";
  /**
   * The dimensions to be considered for distance computation.
   */
  private BitSet dimensions = new BitSet();

  /**
   * Provides a distance function that computes the distance
   * (which is a double distance) between feature vectors only in specified dimensions.
   */
  public AbstractDimensionsSelectingDoubleDistanceFunction() {
    super();
    IntListParameter dims = new IntListParameter(DIMS_P, DIMS_D, new ListGreaterEqualConstraint<Integer>(0));
    dims.setOptional(true);
    optionHandler.put(dims);
  }


  /**
   * @see de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable#setParameters(String[])
   */
  public String[] setParameters(String[] args) throws ParameterException {
    String[] remainingParameters = super.setParameters(args);

    // dim
    //dimensions = new BitSet();
    if (optionHandler.isSet(DIMS_P)) {
      List<Integer> dimensionList = optionHandler.getOptionValue(DIMS_P);
      for (int d : dimensionList) {
        dimensions.set(d);
      }
    }

    return remainingParameters;
  }

  /**
   * Returns a bit set representing the selected dimensions.
   *
   * @return a bit set representing the selected dimensions
   */
  public BitSet getSelectedDimensions() {
    BitSet dimensions = new BitSet(this.dimensions.size());
    dimensions.or(this.dimensions);
    return dimensions;
  }

  /**
   * Sets the selected dimensions according to the set bits in the given BitSet.
   * 
   * 
   * @param dimensions a BitSet designating the new selected dimensions
   */
  public void setSelectedDimensions(BitSet dimensions) {
    this.dimensions.clear();
    this.dimensions.or(dimensions);
    // TODO: set value in parameter
  }
  
  
}
