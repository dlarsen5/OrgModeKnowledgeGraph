package com.david.knowledgegraph;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class Collection
{
  public ArrayList<Document> documents;

  public Collection()
  {
  }

  public void processDir(File dir)
  {

    FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(File f, String name) {
        return name.endsWith(".org");
      }
    };

    File[] listOfFiles = dir.listFiles(filter);
    int successes = 0;
    int numDocs = listOfFiles.length;
    this.documents = new ArrayList(numDocs);

    for (File f : listOfFiles) {
        if (f.isFile()) {
          try {

            Document doc = new Document(f);

            doc.processOrg();
            this.documents.add(doc);
            successes += 1;

          } catch(Exception e) {

            e.printStackTrace();

          }

        }
    }

    // Write out processing stats
    System.out.println("NumFiles: " + numDocs);
    System.out.println("NumSuccesses: " + successes);
    System.out.println("NumFailures: " + (numDocs - successes));

  }

}
