package com.david.knowledgegraph;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Document
{
  private File fileObj;
  private String Id;
  private String text;
  private int IDLENGTH = 6;

  public ArrayList<Node> nodes;
  public int level;

  private void setId() {
    String h = Integer.toString(this.text.hashCode());

    if (this.IDLENGTH > h.length()) {
      this.Id = h;
    } else {
      this.Id = h.substring(0, this.IDLENGTH);
    }

  }

  public String getText() {
    String text = "";
    for (Node n : this.nodes){
      String level = new String(new char[n.level]).replace("\0", "*");
      text += level;
      text += " ";
      text += n.text;
      text += "\n";
    }
    return text;
  }

  public Document() {
    // WARNING: Can throw exception in processOrg if fileObj not initialized
  }

  public Document(File f) {
    this.fileObj = f;
  }

  public Document(String text) {
  }

  public void processOrg() throws IOException, FileNotFoundException {

    FileReader fr = new FileReader(this.fileObj);

    int readIndex;
    String data = "";

    while ((readIndex=fr.read()) != -1)
      data += (char) readIndex;

    this.text = data;

    String lines[] = data.split("\\r?\\n");

    List<Integer> topicIndex = new ArrayList<Integer>();

    for (int i=0; i<lines.length; i++) {
      String line = lines[i];
      if (line.startsWith("*")) {
        topicIndex.add(i);
      }
    }

    ArrayList<Node> nodeList = new ArrayList<Node>();
    int lastElem = topicIndex.get(topicIndex.size() - 1);

    for (int i=0; i<topicIndex.size(); i++) {
      int index = topicIndex.get(i);
      String idea = "";
      int endIndex;

      if (index == lastElem) {
        endIndex = lines.length;
      } else {
        endIndex = topicIndex.get((i+1));
      }

      for (int j=index; j<endIndex; j++) {
        idea += lines[j];
      }

      idea = idea.trim().replaceAll(" +", " ");
      Node ideaNode = new Node(idea);
      nodeList.add(ideaNode);

    }
    int currentLevel = 1;
    // TODO set class attr docTag
    String docTag = new String(new char[this.IDLENGTH]).replace("\0", "0");
    ArrayList<Node> reversed = (ArrayList<Node>)nodeList.clone();
    Collections.reverse(reversed);

    // cuz we need the entire node set before finding relationships
    for (int i=0; i<nodeList.size(); i++) {

      Node n = nodeList.get(i);

      List<String> parents;

      int newLevel = n.level;

      if (newLevel == 1) {

        n.parents.add(docTag);

      } else if (newLevel == currentLevel) {

        n.parents = (ArrayList<String>) nodeList.get(i-1).parents.clone();

      } else {

        int offset = reversed.size() - i;
        int traverseLevel = newLevel;
        ArrayList<Node> parentNodes = new ArrayList<Node>(reversed.subList(offset, nodeList.size()));

        for (Node parent : parentNodes) {

          int parentLevel = parent.level;
          if (parentLevel == 1) {

            n.parents.add(parent.Id);

          } else if (parentLevel == traverseLevel) {

            continue;

          } else if (parentLevel > traverseLevel) {

            continue;

          } else {

            traverseLevel = parentLevel;
            n.parents.add(parent.Id);

          }
        }
      }
      currentLevel = newLevel;
    }

    // TODO find all children
    this.nodes = nodeList;
    setId();
  }

  @Override
  public String toString() {
    return "Doc:" + this.Id;
  }

}
