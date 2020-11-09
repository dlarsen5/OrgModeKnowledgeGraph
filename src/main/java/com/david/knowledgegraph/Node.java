package com.david.knowledgegraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Node
{
  private int IDLENGTH = 6;
  public ArrayList<String> children;
  public ArrayList<String> parents;
  public String Id;
  public String text = "";
  public int level;

  private void setId() {
    String h = Integer.toString(this.text.hashCode());
    String Id;

    if (this.IDLENGTH > h.length()) {
      this.Id = h;
    } else {
      this.Id = h.substring(0, this.IDLENGTH);
    }
  }

  public Node(String text) {
    String textLines[] = text.split(" ");
    List<String> splitList = Arrays.asList(textLines);
    List<String> subList= splitList.subList(1, textLines.length);
    for (String s : subList) {
      this.text += s.toString();
      this.text += " ";
    }
    String levelLength = textLines[0];
    this.level = levelLength.length();
    setId();

    this.children = new ArrayList<String>();
    this.parents = new ArrayList<String>();
  }

  public void addParent(String parentId) {
    this.parents.add(parentId);
  }

  public void addChild(String childId) {
    this.children.add(childId);
  }

  @Override
  public String toString() {
    return "Node:" + this.Id;
  }

}
