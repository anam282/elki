package de.lmu.ifi.dbs.evaluation.holdout;

import de.lmu.ifi.dbs.data.ClassLabel;
import de.lmu.ifi.dbs.data.DatabaseObject;
import de.lmu.ifi.dbs.database.Database;
import de.lmu.ifi.dbs.utilities.UnableToComplyException;
import de.lmu.ifi.dbs.utilities.optionhandling.IntParameter;
import de.lmu.ifi.dbs.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.utilities.optionhandling.constraints.GreaterConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A stratified n-fold crossvalidation to distribute the data to n buckets where
 * each bucket exhibits approximately the same distribution of classes as does
 * the complete dataset. The buckets are disjoint. The distribution is
 * deterministic.
 *
 * @author Arthur Zimek 
 */
public class StratifiedCrossValidation<O extends DatabaseObject, L extends ClassLabel<L>> extends
    AbstractHoldout<O,L> {
  /**
   * Parameter n for the number of folds.
   */
  public static final String N_P = "nfold";

  /**
   * Default number of folds.
   */
  public static final int N_DEFAULT = 10;

  /**
   * Description of the parameter n.
   */
  public static final String N_D = "number of folds for cross-validation";

  /**
   * Holds the number of folds.
   */
  protected int nfold = N_DEFAULT;

  /**
   * Provides a stratified crossvalidation. Setting parameter N_P to the
   * OptionHandler.
   */
  public StratifiedCrossValidation() {
    super();

    IntParameter n = new IntParameter(N_P, N_D, new GreaterConstraint(0));
    n.setDefaultValue(N_DEFAULT);
    optionHandler.put(N_P, n);
  }

  /**
   * @see Holdout#partition(de.lmu.ifi.dbs.database.Database)
   */
  public TrainingAndTestSet<O,L>[] partition(Database<O> database) {
    this.database = database;
    setClassLabels(database);

    // noinspection unchecked
    List<Integer>[] classBuckets = new ArrayList[this.labels.length];
    for (int i = 0; i < classBuckets.length; i++) {
      classBuckets[i] = new ArrayList<Integer>();
    }
    for (Iterator<Integer> iter = database.iterator(); iter.hasNext();) {
      Integer id = iter.next();
      int classIndex = Arrays.binarySearch(labels, database
          .getAssociation(CLASS, id));
      classBuckets[classIndex].add(id);
    }
    // noinspection unchecked
    List<Integer>[] folds = new ArrayList[nfold];
    for (int i = 0; i < folds.length; i++) {
      folds[i] = new ArrayList<Integer>();
    }
    for (List<Integer> bucket : classBuckets) {
      for (int i = 0; i < bucket.size(); i++) {
        folds[i % nfold].add(bucket.get(i));
      }
    }
    // noinspection unchecked
    TrainingAndTestSet<O,L>[] partitions = new TrainingAndTestSet[nfold];
    for (int i = 0; i < nfold; i++) {
      Map<Integer, List<Integer>> partition = new HashMap<Integer, List<Integer>>();
      List<Integer> training = new ArrayList<Integer>();
      for (int j = 0; j < nfold; j++) {
        if (j != i) {
          training.addAll(folds[j]);
        }
      }
      partition.put(0, training);
      partition.put(1, folds[i]);
      try {
        Map<Integer, Database<O>> part = database.partition(partition);
        partitions[i] = new TrainingAndTestSet<O,L>(part.get(0), part
            .get(1), this.labels);
      }
      catch (UnableToComplyException e) {
        throw new RuntimeException(e);
      }
    }
    return partitions;
  }

  /**
   * @see de.lmu.ifi.dbs.utilities.optionhandling.Parameterizable#description()
   */
  public String description() {
    return "Provides a stratified n-fold cross-validation holdout.";
  }

  /**
   * Sets the parameter n additionally to the parameters set by
   * {@link AbstractHoldout#setParameters(String[]) AbstractHoldout.setParameters(args)}.
   *
   * @see de.lmu.ifi.dbs.utilities.optionhandling.Parameterizable#setParameters(String[])
   */
  @Override
  public String[] setParameters(String[] args) throws ParameterException {
    String[] remainingParameters = super.setParameters(args);

    nfold = (Integer) optionHandler.getOptionValue(N_P);

    setParameters(args, remainingParameters);
    return remainingParameters;
  }
}
