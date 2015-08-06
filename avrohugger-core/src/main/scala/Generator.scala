package avrohugger

import avrohugger.NestedSchemaExtractor._
import avrohugger.format.DependencyInspectionSupport._
import avrohugger.format._
import avrohugger.inputformat._
import avrohugger.inputformat.schemagen._

import java.io.{File, FileNotFoundException, IOException}

import org.apache.avro.Schema

import scala.collection.JavaConverters._


// Unable to overload the methods of this class because outDir uses a default value
class Generator(format: SourceFormat) {
  val sourceFormat = format
  val defaultOutputDir = "target/generated-sources"
  lazy val fileParser = new FileInputParser
  lazy val stringParser = new StringInputParser
  lazy val schemaParser = new Schema.Parser
  val classStore = new ClassStore


  //methods for writing definitions out to file 
  def schemaToFile(schema: Schema, outDir: String = defaultOutputDir): Unit = {
    val namespace: Option[String] = getReferredNamespace(schema)
    val topLevelSchemas: List[Schema] = schema::(getNestedSchemas(schema))
    topLevelSchemas.reverse.foreach { schema => // most-nested classes processed first
      // pass in the top-level schema's namespace if the nested schema has none
      val ns = getReferredNamespace(schema) orElse namespace
      sourceFormat.writeToFile(classStore, ns, schema, outDir)
    }
  }

  def stringToFile(schemaStr: String, outDir: String = defaultOutputDir): Unit = {
    val schemas = stringParser.getSchemas(schemaStr)
    schemas.foreach(schema => schemaToFile(schema, outDir))
  }

  def fileToFile(inFile: File, outDir: String = defaultOutputDir): Unit = {
    val schemas: List[Schema] = fileParser.getSchemas(inFile)
    schemas.foreach(schema => schemaToFile(schema, outDir))
  }


  // methods for writing definitions to a list of definitions in String format
  def schemaToStrings(schema: Schema): List[String] = {
    val namespace: Option[String] = getReferredNamespace(schema)
    val topLevelSchemas: List[Schema] = schema::getNestedSchemas(schema)
    topLevelSchemas.reverse.map(schema => { // process most-nested classes first
      // pass in the top-level schema's namespace if the nested schema has none
      val ns = getReferredNamespace(schema) orElse namespace
      val codeString = sourceFormat.asDefinitionString(classStore, ns, schema)
      // drop the comment because it's not applicable outside of file writing/overwriting
      codeString.replace("/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */\n", "")
    }) 
  }

  def stringToStrings(schemaStr: String): List[String] = {
    val schemas = stringParser.getSchemas(schemaStr)
    // reverse to restore printing order and print top-level classes first 
    schemas.flatMap(schema => schemaToStrings(schema)).reverse
  }

  def fileToStrings(inFile: File): List[String] = {
    try {
      val schemas: List[Schema] = fileParser.getSchemas(inFile)
      schemas.flatMap(schema => schemaToStrings(schema))
    } 
    catch {
      case ex: FileNotFoundException => sys.error("File not found:" + ex)
      case ex: IOException => sys.error("There was a problem using the file: " + ex)
    }
  }

}

