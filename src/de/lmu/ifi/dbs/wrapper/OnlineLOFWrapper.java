package de.lmu.ifi.dbs.wrapper;

import java.util.List;

import de.lmu.ifi.dbs.algorithm.AbortException;
import de.lmu.ifi.dbs.algorithm.KDDTask;
import de.lmu.ifi.dbs.algorithm.outlier.OnlineLOF;
import de.lmu.ifi.dbs.distance.EuklideanDistanceFunction;
import de.lmu.ifi.dbs.utilities.optionhandling.AttributeSettings;
import de.lmu.ifi.dbs.utilities.optionhandling.OptionHandler;
import de.lmu.ifi.dbs.utilities.optionhandling.ParameterException;

/**
 * Wrapper class for LOF algorithm. Performs an attribute wise normalization
 * on the database objects.
 *
 * @author Elke Achtert (<a
 *         href="mailto:achtert@dbs.ifi.lmu.de">achtert@dbs.ifi.lmu.de</a>)
 */
public class OnlineLOFWrapper extends FileBasedDatabaseConnectionWrapper {
//  /**
//   * Holds the class specific debug status.
//   */
//  @SuppressWarnings({"unused", "UNUSED_SYMBOL"})
//  private static final boolean DEBUG = LoggingConfiguration.DEBUG;
////  private static final boolean DEBUG = true;
//
//  /**
//   * The logger of this class.
//   */
//  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * The value of the minpts parameter.
   */
  private String minpts;

  /**
   * The value of the insertions parameter.
   */
  private String insertions;

  /**
   * The value of the lof parameter.
   */
  private String lof;

  /**
   * The value of the nn parameter.
   */
  private String nn;

  /**
   * Main method to run this wrapper.
   *
   * @param args the arguments to run this wrapper
   */
  public static void main(String[] args) {
    OnlineLOFWrapper wrapper = new OnlineLOFWrapper();
    try {
      wrapper.setParameters(args);
      wrapper.run();
    }
    catch (ParameterException e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      wrapper.exception(wrapper.optionHandler.usage(e.getMessage()), cause);
//      wrapper.logger.log(Level.SEVERE, wrapper.optionHandler.usage(e.getMessage()), cause);
    }
    catch (AbortException e) {
    	wrapper.verbose(e.getMessage());
//      wrapper.logger.info(e.getMessage());
    }
    catch (Exception e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      cause.printStackTrace();
      e.printStackTrace();
      wrapper.exception(wrapper.optionHandler.usage(e.getMessage()), e);
//      wrapper.logger.log(Level.SEVERE, wrapper.optionHandler.usage(e.getMessage()), e);
    }
  }

  /**
   * Sets the parameters epsilon and minpts in the parameter map additionally
   * to the parameters provided by super-classes.
   */
  public OnlineLOFWrapper() {
    super();
    parameterToDescription.put(OnlineLOF.MINPTS_P + OptionHandler.EXPECTS_VALUE, OnlineLOF.MINPTS_D);
    parameterToDescription.put(OnlineLOF.INSERTIONS_P + OptionHandler.EXPECTS_VALUE, OnlineLOF.INSERTIONS_D);
    parameterToDescription.put(OnlineLOF.LOF_P + OptionHandler.EXPECTS_VALUE, OnlineLOF.LOF_D);
    parameterToDescription.put(OnlineLOF.NN_P + OptionHandler.EXPECTS_VALUE, OnlineLOF.NN_D);
    optionHandler = new OptionHandler(parameterToDescription, getClass().getName());
  }

  /**
   * @see KDDTaskWrapper#getKDDTaskParameters()
   */
  public List<String> getKDDTaskParameters() {
    List<String> parameters = super.getKDDTaskParameters();

    // algorithm OnlineLOF
    parameters.add(OptionHandler.OPTION_PREFIX + KDDTask.ALGORITHM_P);
    parameters.add(OnlineLOF.class.getName());

    // minpts
    parameters.add(OptionHandler.OPTION_PREFIX + OnlineLOF.MINPTS_P);
    parameters.add(minpts);

    // insertions
    parameters.add(OptionHandler.OPTION_PREFIX + OnlineLOF.INSERTIONS_P);
    parameters.add(insertions);

    // lof
    parameters.add(OptionHandler.OPTION_PREFIX + OnlineLOF.LOF_P);
    parameters.add(lof);

    // nn
    parameters.add(OptionHandler.OPTION_PREFIX + OnlineLOF.NN_P);
    parameters.add(nn);

    // distance function
    parameters.add(OptionHandler.OPTION_PREFIX + OnlineLOF.DISTANCE_FUNCTION_P);
    parameters.add(EuklideanDistanceFunction.class.getName());

    // page size
//    parameters.add(OptionHandler.OPTION_PREFIX + LOF.PAGE_SIZE_P);
//    parameters.add("8000");

    // cache size
//    parameters.add(OptionHandler.OPTION_PREFIX + LOF.CACHE_SIZE_P);
//    parameters.add("" + 8000 * 10);


    return parameters;
  }

  /**
   * @see de.lmu.ifi.dbs.utilities.optionhandling.Parameterizable#setParameters(String[])
   */
  public String[] setParameters(String[] args) throws ParameterException {
    String[] remainingParameters = super.setParameters(args);

    // minpts, insertions, lof, nn
    minpts = optionHandler.getOptionValue(OnlineLOF.MINPTS_P);
    insertions = optionHandler.getOptionValue(OnlineLOF.INSERTIONS_P);
    lof = optionHandler.getOptionValue(OnlineLOF.LOF_P);
    nn = optionHandler.getOptionValue(OnlineLOF.NN_P);

    return remainingParameters;
  }

  /**
   * @see de.lmu.ifi.dbs.utilities.optionhandling.Parameterizable#getAttributeSettings()
   */
  public List<AttributeSettings> getAttributeSettings() {
    List<AttributeSettings> settings = super.getAttributeSettings();
    AttributeSettings mySettings = settings.get(0);
    mySettings.addSetting(OnlineLOF.MINPTS_P, minpts);
    mySettings.addSetting(OnlineLOF.INSERTIONS_P, insertions);
    mySettings.addSetting(OnlineLOF.LOF_P, lof);
    mySettings.addSetting(OnlineLOF.NN_P, nn);
    return settings;
  }
}
