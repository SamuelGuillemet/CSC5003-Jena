package itsudparis;

import java.util.Scanner;

import org.apache.jena.rdf.model.Model;

import itsudparis.tools.JenaEngine;
import itsudparis.tools.QuitException;

public class App {
  public static void main(String[] args) {
    // lire le model a partir d'une ontologie
    Model model = JenaEngine.readModel("food.rdf");
    if (model != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("SELECT ?foodProduct ?ingredient ?flavor");
      sb.append(" WHERE {");
      sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
      sb.append(" ?foodProduct ns:hasIngredient ?ingredient.");
      sb.append(" ?foodProduct ns:hasFlavor ?flavor.");
      sb.append("}");
      String result = JenaEngine.executePlainQuery(model, sb.toString());
      System.out.println(result);

    } else {
      System.out.println("Error when reading model from ontology");
    }
  }

  private String readFromUserInput() throws QuitException {
    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    scanner.close();
    if (input.equals("exit") || input.equals("quit") || input.isEmpty() || input.isBlank()) {
      throw new QuitException();
    }
    return input;
  }

}
