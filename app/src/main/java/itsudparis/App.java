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
      App app = new App();
      app.interactive(model);
    } else {
      System.out.println("Error when reading model from ontology");
    }
  }

  private String readFromUserInput(Scanner scanner) throws QuitException {
    System.out.println("Enter a command:");
    System.out.println("1. List all products");
    System.out.println("2. Get the product with the most sugar");
    System.out.println("3. Get the product with the most salt");
    System.out.println("4. Get the product with the most fat");
    System.out.println("5. List ingredients of a product");
    System.out.println("Type 'exit' or 'quit' to quit");
    if (!scanner.hasNextLine()) {
      throw new QuitException();
    }
    String input = scanner.nextLine();
    if (input.equals("exit") || input.equals("quit") || input.isEmpty() || input.isBlank()) {
      throw new QuitException();
    }
    return input;
  }

  private String listProducts(Model model) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ?name");
    sb.append(" WHERE {");
    sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
    sb.append(" ?foodProduct ns:hasName ?name.");
    sb.append("}");
    return JenaEngine.executePlainQuery(model, sb.toString());
  }

  private String getMostSugarProduct(Model model) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ?name");
    sb.append(" WHERE {");
    sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
    sb.append(" ?foodProduct ns:hasName ?name.");
    sb.append(" ?foodProduct ns:hasSugarContent ?sugar.");
    sb.append("}");
    sb.append(" ORDER BY DESC(?sugar)");
    sb.append(" LIMIT 1");
    return JenaEngine.executePlainQuery(model, sb.toString());
  }

  private String getMostSaltProduct(Model model) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ?name");
    sb.append(" WHERE {");
    sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
    sb.append(" ?foodProduct ns:hasName ?name.");
    sb.append(" ?foodProduct ns:hasSaltContent ?salt.");
    sb.append("}");
    sb.append(" ORDER BY DESC(?salt)");
    sb.append(" LIMIT 1");
    return JenaEngine.executePlainQuery(model, sb.toString());
  }

  private String getMostFatProduct(Model model) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ?name");
    sb.append(" WHERE {");
    sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
    sb.append(" ?foodProduct ns:hasName ?name.");
    sb.append(" ?foodProduct ns:hasFatContent ?fat.");
    sb.append("}");
    sb.append(" ORDER BY DESC(?fat)");
    sb.append(" LIMIT 1");
    return JenaEngine.executePlainQuery(model, sb.toString());
  }

  private String listIngredients(Model model, String foodProduct) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ?ingredient");
    sb.append(" WHERE {");
    sb.append(" ?foodProduct rdf:type ns:FoodProduct.");
    sb.append(" ?foodProduct ns:hasName ?name.");
    sb.append(" ?foodProduct ns:hasIngredient ?ingredient.");
    sb.append(" FILTER(?name = \"" + foodProduct + "\")");
    sb.append("}");
    return JenaEngine.executePlainQuery(model, sb.toString());
  }

  private void interactive(Model model) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      try {
        String result = "";
        String input = readFromUserInput(scanner);
        switch (input) {
          case "1":
            result = listProducts(model);
            break;
          case "2":
            result = getMostSugarProduct(model);
            break;
          case "3":
            result = getMostSaltProduct(model);
            break;
          case "4":
            result = getMostFatProduct(model);
            break;
          case "5":
            System.out.println("Enter the name of a product: ");
            String foodProduct = scanner.nextLine();
            result = listIngredients(model, foodProduct);
            break;
          default:
            System.out.println("Invalid command");
            break;
        }
        System.out.println("Result: \n" + result);
      } catch (QuitException e) {
        scanner.close();
        System.out.println("Bye");
        return;
      }
    }
  }

}
