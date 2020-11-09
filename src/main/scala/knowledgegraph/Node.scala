package knowledgegraph

import scala.collection.mutable.ArrayBuffer


class Node(_text: String) {
  val children = ArrayBuffer[String]()
  val parents = ArrayBuffer[String]()
  val IDLENGTH: Int = 6

  var Id: String = ""
  var level: Int = 0
  var text: String = ""

  processText(_text)

  private def processText(text: String): Unit = {
    val textLines: Array[String] = text.split(" ")
    this.text = textLines.slice(1, textLines.length).mkString(" ")
    this.level = textLines(0).length
    setId()
  }

  private def setId() =
  {
    val h: String = this.text.hashCode().toString

    if (this.IDLENGTH > h.length) {
      this.Id = h
    } else {
      this.Id = h.substring(0, this.IDLENGTH)
    }
  }

  def addParent(parentId: String): Unit =
  {
    this.parents += parentId
  }

  def addChild(childId: String): Unit = {
    this.children += childId
  }

  override def toString = {
    "Node:" + this.Id
  }

}
