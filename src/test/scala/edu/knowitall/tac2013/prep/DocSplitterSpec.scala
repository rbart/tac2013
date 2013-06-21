package edu.knowitall.tac2013.prep

import org.scalatest._
import java.io.File
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


class DocSplitterSpec extends FlatSpec {
  
  val docSplitter = new DocSplitter()
  val splitDocsDir = "src/main/resources/samples/docs-split/"
  val splitWebDocsDir = splitDocsDir + "web"
  val splitNewsDocsDir= splitDocsDir + "news"
  val splitForumDocsDir=splitDocsDir + "forum"
  
  val allDocsDirs = Seq(splitWebDocsDir, splitNewsDocsDir, splitForumDocsDir)
  
  "DocSplitter" should "Tag lines with correct byte offsets" in {
    
    val files = allDocsDirs.flatMap(dir => new File(dir).listFiles())
    for (file <- files) {
      
      testFile(file)
    }
  }
  
  /*
   * Assumes that a file contains a single kbp doc
   */
  def testFile(file: File): Unit = {
    
    val source = io.Source.fromFile(file)
    
    val spliterator = docSplitter.splitDocs(source)
    require(spliterator.hasNext)
    
    val kbpDoc = spliterator.next()
    
    require(!spliterator.hasNext)
    
    val path = Paths.get(file.getPath)
    
    val bytes = Files.readAllBytes(path);
    
    for (kbpline <- kbpDoc.lines) {
      val targetString = new String(bytes.drop(kbpline.startByte).take(kbpline.length), "UTF8")
      assert(targetString === kbpline.line)
    }
    source.close()
  }
}