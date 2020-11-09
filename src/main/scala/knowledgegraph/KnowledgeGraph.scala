package knowledgegraph

import java.io.File

import collection._

object KnowledgeGraph {
  def main (args: Array[String]): Unit = {

    if (args.length == 0) {
      println("Need to add input directory, exiting...")
      return 0
    } else {
      val orgDir = new File(args(0))
    }

    if (orgDir.exists) {
        val knowledgegraph = new Collection()
        val myfiles = knowledgegraph.processDir(orgDir)
    } else {
        println("Org dir not valid")
    }
  }
}
