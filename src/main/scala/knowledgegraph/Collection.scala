package knowledgegraph

import java.io.File
import scala.collection.mutable.ArrayBuffer

class Collection() {

  def processDir(orgPath: File): Int = {
    val fileList = getListOfFiles(orgPath)
    val docs = ArrayBuffer[Document]()

    for (inputFile: File <- fileList) {
      try {docs += new Document(inputFile)}
      catch {case _: Throwable => }
    }
    // docs.foreach(doc => println(doc.text))
    0
  }

  def getListOfFiles(dir: File): List[File] = {
    if (dir.exists && dir.isDirectory) {
      dir.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
}
