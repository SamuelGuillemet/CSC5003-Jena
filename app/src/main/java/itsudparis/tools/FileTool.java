/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itsudparis.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

/**
*
* @author DO.IT-SUDPARIS
* Ce fichier est pour
* + Lire le contenu d'un fichier: getContents
* + Entree: Objet File
* + Sortie: La chaine de caractere
*/
public class FileTool {

  private FileTool() {
  }

  public static String getContents(@Nonnull InputStream inputStream) {
    StringBuilder contents = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        contents.append(line).append(System.getProperty("line.separator"));
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return contents.toString();
  }

  public static InputStream getInputStream(String filepath) {
    ClassLoader classLoader = FileTool.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(filepath);

    if (inputStream == null) {
      System.err.println("Resource not found: " + filepath);
      return null;
    } else {
      return inputStream;
    }
  }

  public static String getContents(String filepath) {
    InputStream inputStream = getInputStream(filepath);

    if (inputStream == null) {
      return null;
    } else {
      return getContents(inputStream);
    }
  }

  public static boolean verifyFileExist(String filepath) {
    InputStream inputStream = getInputStream(filepath);
    return inputStream != null;
  }
}
