/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itsudparis.tools;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

import org.apache.jena.riot.RDFDataMgr;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import java.io.OutputStream;

/**
*
* @author DO.ITSUDPARIS
*/
public class JenaEngine {
  private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  private JenaEngine() {
  }

  /**
  * Charger un model a partir un fichier owl
  * @param args
  * + Entree: le chemin vers le fichier owl
  * + Sortie: l'objet model jena
   * @throws IOException
  */
  public static Model readModel(String inputDataFile) {
    // create an empty model
    Model model = ModelFactory.createDefaultModel();
    try (InputStream in = FileTool.getInputStream(inputDataFile)) {
      model.read(in, "");
      return model;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Model readAbsoluteModel(String filepath) {
    System.out.println("Reading model from " + filepath);
    Model model = RDFDataMgr.loadModel(filepath);
    System.out.println("Model read");
    return model;
  }

  /**
  * Faire l'inference
  * @param args
  * + Entree: l'objet model Jena avec le chemin du fichier des regles
  * + Sortie: l'objet model infere Jena
  */
  public static Model readInferencedModelFromRuleFile(Model model, String inputRuleFile) {
    if (!FileTool.verifyFileExist(inputRuleFile)) {
      System.out.println("Rule file: " + inputRuleFile + " not found");
      return null;
    }
    List<Rule> rules = Rule.rulesFromURL(inputRuleFile);
    GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
    reasoner.setDerivationLogging(true);
    reasoner.setOWLTranslation(true); // not needed in RDFS case
    reasoner.setTransitiveClosureCaching(true);
    return ModelFactory.createInfModel(reasoner, model);
  }

  /**
  * Executer une requete
  * @param args
  * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
  * + Sortie: le resultat de la requete en String
  */
  public static String executeQuery(Model model, String queryString) {
    // System.out.println("Query: " + queryString);
    Query query = QueryFactory.create(queryString);
    // No reasoning
    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(query, model);
    ResultSet results = qe.execSelect();
    OutputStream output = new OutputStream() {
      private StringBuilder string = new StringBuilder();

      @Override
      public void write(int b) throws IOException {
        this.string.append((char) b);
      }

      //Netbeans IDE automatically overrides this toString()
      public String toString() {
        return this.string.toString();
      }
    };
    ResultSetFormatter.out(output, results, query);
    return output.toString();
  }

  public static String executePlainQuery(Model model, String query) {
    String queryHeaders = FileTool.getContents("query-headers.txt");

    return executeQuery(model, queryHeaders + query);
  }

  /**
    * Executer un fichier d'une requete
    * @param args
    * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
    * + Sortie: le resultat de la requete en String
    */
  public static String executeQueryFile(Model model, String filepath) {
    String queryString = FileTool.getContents(filepath);
    if (queryString != null) {
      return executeQuery(model, queryString);
    } else {
      System.out.println("Query file: " + filepath + " not found");
      return null;
    }
  }

  /**
  * Executer un fichier d'une requete avec le parametre
  * @param args
  * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
  * + Sortie: le resultat de la requete en String
  */
  public static String executeQueryFileWithParameter(Model model, String filepath, String parameter) {
    String queryString = FileTool.getContents(filepath);
    if (queryString == null) {
      System.out.println("Query file: " + filepath + " not found");
      return null;
    }
    queryString = queryString.replace("%PARAMETER%", parameter);
    return executeQuery(model, queryString);
  }

  /**
  * Creer une Instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de le classe
  * - Le nom de l'instance
  * + Sortie: le resultat de la requete en String
  */
  public static boolean createInstanceOfClass(Model model, String namespace, String className, String instanceName) {
    Resource rs = model.getResource(namespace + instanceName);
    if (rs == null)
      rs = model.createResource(namespace + instanceName);
    Property p = model.getProperty(RDF + "type");
    Resource rs2 = model.getResource(namespace + className);
    if ((rs2 != null) && (rs != null) && (p != null)) {
      //add new value
      rs.addProperty(p, rs2);
      return true;
    }
    return false;
  }

  /**
  * Mettre a jour la valeur d'une propriete objet d'une instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de la premier Instance
  * - Le nom de la propriete
  * - Le nom de la deuxieme Instance
  * + Sortie: le resultat de la requete en String
  */
  public static boolean updateValueOfObjectProperty(Model model, String namespace, String object1Name,
      String propertyName, String object2Name) {
    Resource rs1 = model.getResource(namespace + object1Name);
    Resource rs2 = model.getResource(namespace + object2Name);
    Property p = model.getProperty(namespace + propertyName);
    if ((rs1 != null) && (rs2 != null) && (p != null)) {
      //remove all old values of property p
      rs1.removeAll(p);
      //add new value
      rs1.addProperty(p, rs2);
      return true;
    }
    return false;
  }

  /**
  * Mettre a jour la valeur d'une propriete objet d'un Instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de le premier Instance
  * - Le nom de la propriete
  * - Le nom de le deuxieme Instance
  * + Sortie: le resultat de la requete en String
  */
  public static boolean addValueOfObjectProperty(Model model, String namespace, String instance1Name,
      String propertyName, String instance2Name) {
    Resource rs1 = model.getResource(namespace + instance1Name);
    Resource rs2 = model.getResource(namespace + instance2Name);
    Property p = model.getProperty(namespace + propertyName);
    if ((rs1 != null) && (rs2 != null) && (p != null)) {
      //add new value
      rs1.addProperty(p, rs2);
      return true;
    }
    return false;
  }

  /**
  * Mettre a jour la valeur d'une propriete datatype d'un Instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de l'Instance
  * - Le nom de la propriete
  * - La nouvelle valeur
  * + Sortie: le resultat de la requete en String
  */
  public static boolean updateValueOfDataTypeProperty(Model model,
      String namespace, String instanceName, String propertyName, Object value) {
    Resource rs = model.getResource(namespace + instanceName);
    Property p = model.getProperty(namespace + propertyName);
    if ((rs != null) && (p != null)) {
      //remove all old values of property p
      rs.removeAll(p);
      //add new value
      rs.addLiteral(p, value);
      return true;
    }
    return false;
  }

  /**
  * Ajouter la valeur d'une propriete datatype d'un Instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de l'Instance
  * - Le nom de la propriete
  * - La valeur
  * + Sortie: le resultat de la requete en String
  */
  public static boolean addValueOfDataTypeProperty(Model model, String namespace, String instanceName,
      String propertyName, Object value) {
    Resource rs = model.getResource(namespace + instanceName);
    Property p = model.getProperty(namespace + propertyName);
    if ((rs != null) && (p != null)) {
      //add new value
      rs.addLiteral(p, value);
      return true;
    }
    return false;
  }

  /**
  * Supprimer toutes les valeurs d'une propriete d'un Instance
  * @param args
  * + Entree:
  * - l'objet model Jena
  * - Namespace de l'ontologie
  * - Le nom de l'Instance
  * - Le nom de la propriete
  * + Sortie: le resultat de la requete en String
  */
  public static boolean removeAllValuesOfProperty(Model model, String namespace, String objectName,
      String propertyName) {
    Resource rs = model.getResource(namespace + objectName);
    Property p = model.getProperty(namespace + propertyName);
    if ((rs != null) && (p != null)) {
      //remove all old values of property p
      rs.removeAll(p);
      //add new value
      return true;
    }
    return false;
  }
}
