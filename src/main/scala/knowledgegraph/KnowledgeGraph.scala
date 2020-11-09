package knowledgegraph

import java.io.File

import collection._

object KnowledgeGraph {
  def main (args: Array[String]): Unit = {
    val orgDir = new File("/Users/davidlarsen/Git/Describe/describe")

    if (orgDir.exists) {
        val knowledgegraph = new Collection()
        val myfiles = knowledgegraph.processDir(orgDir)
    } else {
        println("Org dir not valid");
    }
  }
}
