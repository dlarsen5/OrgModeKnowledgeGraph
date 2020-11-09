package knowledgegraph

import scala.collection.mutable.ArrayBuffer

import java.io.File
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayList
import java.util.List
import java.util.Collections

class Document(inputFile: File) {
  val IDLENGTH: Int = 6

  var nodes = ArrayBuffer[Node]()
  var Id = ""
  var text = ""

  processOrg(inputFile)

  private def setId() =
  {
    val h: String = this.text.hashCode().toString

    if (this.IDLENGTH > h.length) {
      this.Id = h
    } else {
      this.Id = h.substring(0, this.IDLENGTH)
    }
  }

  def getText(): String = {
    var text: String = ""
    for (n: Node <- this.nodes) {
      var level: Int = n.level
      text += "*" * level
      text += " "
      text += n.text
      text += "\n"
    }
    return text
  }

  @throws(classOf[IOException])
  @throws(classOf[FileNotFoundException])
  def processOrg(inputFile: File): Unit = {

    text = io.Source.fromFile(inputFile.toString).mkString

    val lines: Array[String] = text.split("\n")

    val topicIndex = ArrayBuffer[Int]()

    for (i <- 0 until lines.length) {
      var line = lines(i)
      if (line.startsWith("*")) {
        topicIndex += i
      }
    }

    val nodeList = ArrayBuffer[Node]()
    val lastElem = topicIndex.last

    for (i <- 0 until topicIndex.size) {
      val index: Int = topicIndex(i)
      var _idea = StringBuilder.newBuilder
      var endIndex: Int = 0

      if (index == lastElem) {
        endIndex = lines.length
      } else {
        endIndex = topicIndex((i+1))
      }

      for (j <- index until endIndex) {
        _idea.append(lines(j))
      }

      val idea = _idea.toString.trim().replaceAll(" +", " ")
      val ideaNode = new Node(idea)
      nodeList += ideaNode

    }
    var currentLevel = 1
    // TODO set class attr docTag
    /*
    String docTag = new String(new char[this.IDLENGTH]).replace("\0", "0")
    ArrayList<Node> reversed = (ArrayList<Node>)nodeList.clone()
    Collections.reverse(reversed)

    // cuz we need the entire node set before finding relationships
    for (int i=0 i<nodeList.size() i++) {

      Node n = nodeList.get(i)

      List<String> parents

      int newLevel = n.level

      if (newLevel == 1) {

        n.parents.add(docTag)

      } else if (newLevel == currentLevel) {

        n.parents = (ArrayList<String>) nodeList.get(i-1).parents.clone()

      } else {

        int offset = reversed.size() - i
        int traverseLevel = newLevel
        ArrayList<Node> parentNodes = new ArrayList<Node>(reversed.subList(offset, nodeList.size()))

        for (Node parent : parentNodes) {

          int parentLevel = parent.level
          if (parentLevel == 1) {

            n.parents.add(parent.Id)

          } else if (parentLevel == traverseLevel) {

            continue

          } else if (parentLevel > traverseLevel) {

            continue

          } else {

            traverseLevel = parentLevel
            n.parents.add(parent.Id)

          }
        }
      }
      currentLevel = newLevel
    }

    // TODO find all children
     */
    this.nodes = nodeList
    setId()
  }

  override def toString = {
    "Doc:" + this.Id
  }

}
