package com.david.knowledgegraph;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class KnowledgeGraph
{

  public static void main (String[] args) {
    String orgDir = "/home/neutron/Rap/lyrics/orglyrics/";
    File orgPath = new File(orgDir);

    if (orgPath.exists()) {

        Collection knowledgegraph = new Collection();
        knowledgegraph.processDir(orgPath);

        for (Document d : knowledgegraph.documents) {
          System.out.println(d);
        }

    } else {

        System.out.println("Org dir not valid");

    }
  }

}
