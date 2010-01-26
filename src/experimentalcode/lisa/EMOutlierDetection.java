package experimentalcode.lisa;

import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.EM;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.EMModel;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.AnnotationFromDatabase;
import de.lmu.ifi.dbs.elki.result.MultiResult;
import de.lmu.ifi.dbs.elki.result.OrderingFromAssociation;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.utilities.Description;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
/**
 * outlier detection algorithm using EM Clustering. 
 * If an object does not belong to any cluster it is supposed to be an outlier. 
 * If the probability for an object to belong to the most probable cluster is still relatively low this object is an outlier. 
 *
 * @author Lisa Reichert
 *
 * @param <V> Vector type
 */
public class EMOutlierDetection<V extends NumberVector<V, ?>> extends AbstractAlgorithm<V, MultiResult>{
  EM<V> emClustering = new EM<V>();
  /**
   * association id to associate the 
   */
  public static final AssociationID<Double> DBOD_MAXCPROB= AssociationID.getOrCreateAssociationID("dbod_maxcprob", Double.class);
  /**
   * Provides the result of the algorithm.
   */
  MultiResult result;

  /**
   * Constructor, adding options to option handler.
   */
  public EMOutlierDetection() {
    super();
    addParameterizable(emClustering);
  }
  
  /**
   * Calls the super method
   * and sets additionally the values of the parameter
   * {@link #K_PARAM}, {@link #N_PARAM} 
   */
  @Override
  public List<String> setParameters(List<String> args) throws ParameterException {
      List<String> remainingParameters = super.setParameters(args);
      remainingParameters= emClustering.setParameters(remainingParameters);
      return remainingParameters;
  }

  /**
   * Runs the algorithm in the timed evaluation part.
   */

  @Override
  protected MultiResult runInTime(Database<V> database) throws IllegalStateException {
     
    Clustering<EMModel<V>> emresult = emClustering.run(database);
    double globmax = 0.0;
    for (Integer id : database) {

      double maxProb = 0.0;
      double[] probs = emClustering.getProbClusterIGivenX(id);
      for (double prob : probs){
         maxProb = Math.max(prob, maxProb);
      }
      logger.debug("maxprob"+ maxProb);
      database.associate(DBOD_MAXCPROB, id, 1 - maxProb);     
      globmax = Math.max(1-maxProb, globmax);
    }
    result = new MultiResult();
    result.addResult(new AnnotationFromDatabase<Double, V>(database, DBOD_MAXCPROB));
     // Ordering
    result.addResult(new OrderingFromAssociation<Double, V>(database, DBOD_MAXCPROB, true));
    result.addResult(emresult);
    ResultUtil.setGlobalAssociation(result, DBOD_MAXCPROB, globmax);
    return result;
  }

  @Override
  public Description getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MultiResult getResult() {
    
    return result;
  }
  
}
