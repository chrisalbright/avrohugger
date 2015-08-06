
import java.io.File

import avrohugger._
import avrohugger.format.SpecificRecord
import org.specs2._

class SpecificGeneratorSpec extends mutable.Specification {

  "a SpecificGenerator" should {
    
    "correctly generate a case class definition that extends `SpecificRecordBase` in a package" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/mail.avpr")
      val gen = new Generator(SpecificRecord)
      val outDir = gen.defaultOutputDir + "/specific/"
      gen.fileToFile(infile, outDir)
      val source = scala.io.Source.fromFile(s"$outDir/example/proto/Message.scala").mkString
      source === 
        """|/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
          |package example.proto
          |
          |case class Message(var to: String, var from: String, var body: String) extends org.apache.avro.specific.SpecificRecordBase {
          |  def this() = this("", "", "")
          |  def get(field: Int): AnyRef = {
          |    field match {
          |      case pos if pos == 0 => {
          |        to
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 1 => {
          |        from
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 2 => {
          |        body
          |      }.asInstanceOf[AnyRef]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |  }
          |  def put(field: Int, value: Any): Unit = {
          |    field match {
          |      case pos if pos == 0 => this.to = {
          |        value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        }
          |      }.asInstanceOf[String]
          |      case pos if pos == 1 => this.from = {
          |        value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        }
          |      }.asInstanceOf[String]
          |      case pos if pos == 2 => this.body = {
          |        value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        }
          |      }.asInstanceOf[String]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |    ()
          |  }
          |  def getSchema: org.apache.avro.Schema = Message.SCHEMA$
          |}
          |
          |object Message {
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Message\",\"namespace\":\"example.proto\",\"fields\":[{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"body\",\"type\":\"string\"}]}")
          |}""".stripMargin
    }



    "correctly generate a specific case class definition from a schema as a string" in {
      val schemaString = """{"type":"record","name":"Person","namespace":"test","fields":[{"name":"name","type":"string"}],"doc:":"A basic schema for storing Twitter messages"}"""
      val gen = new Generator(SpecificRecord)
      val List(source) = gen.stringToStrings(schemaString)

       
      source ===
        """package test
          |
          |case class Person(var name: String) extends org.apache.avro.specific.SpecificRecordBase {
          |  def this() = this("")
          |  def get(field: Int): AnyRef = {
          |    field match {
          |      case pos if pos == 0 => {
          |        name
          |      }.asInstanceOf[AnyRef]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |  }
          |  def put(field: Int, value: Any): Unit = {
          |    field match {
          |      case pos if pos == 0 => this.name = {
          |        value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        }
          |      }.asInstanceOf[String]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |    ()
          |  }
          |  def getSchema: org.apache.avro.Schema = Person.SCHEMA$
          |}
          |
          |object Person {
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Person\",\"namespace\":\"test\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"}],\"doc:\":\"A basic schema for storing Twitter messages\"}")
          |}""".stripMargin.trim
    }

    "correctly generate enums with SCHEMA$" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/enums.avsc")
      val gen = new Generator(SpecificRecord)
      val outDir = gen.defaultOutputDir + "/specific/"
      gen.fileToFile(infile, outDir)

      val source = scala.io.Source.fromFile(s"$outDir/example/Suit.java").mkString
      source ====
        """/**
          | * Autogenerated by Avro
          | * 
          | * DO NOT EDIT DIRECTLY
          | */
          |package example;  
          |@SuppressWarnings("all")
          |@org.apache.avro.specific.AvroGenerated
          |public enum Suit { 
          |  SPADES, DIAMONDS, CLUBS, HEARTS  ;
          |  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Suit\",\"namespace\":\"example\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}");
          |  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
          |}
          |""".stripMargin
    }

    "correctly generate enums in AVDLs with `SpecificRecord`" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/enums.avdl")
      val gen = new Generator(SpecificRecord)
      val outDir = gen.defaultOutputDir + "/specific/"
      gen.fileToFile(infile, outDir)

      val sourceEnum = scala.io.Source.fromFile(s"$outDir/example/idl/Suit.java").mkString
      sourceEnum ====
        """/**
          | * Autogenerated by Avro
          | * 
          | * DO NOT EDIT DIRECTLY
          | */
          |package example.idl;  
          |@SuppressWarnings("all")
          |@org.apache.avro.specific.AvroGenerated
          |public enum Suit { 
          |  SPADES, DIAMONDS, CLUBS, HEARTS  ;
          |  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Suit\",\"namespace\":\"example.idl\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}");
          |  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
          |}
          |""".stripMargin
      val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/idl/Card.scala").mkString
      sourceRecord ====
        """/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
          |package example.idl
          |
          |case class Card(var suit: Suit, var number: Int) extends org.apache.avro.specific.SpecificRecordBase {
          |  def this() = this(null, 1)
          |  def get(field: Int): AnyRef = {
          |    field match {
          |      case pos if pos == 0 => {
          |        suit
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 1 => {
          |        number
          |      }.asInstanceOf[AnyRef]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |  }
          |  def put(field: Int, value: Any): Unit = {
          |    field match {
          |      case pos if pos == 0 => this.suit = {
          |        value
          |      }.asInstanceOf[Suit]
          |      case pos if pos == 1 => this.number = {
          |        value
          |      }.asInstanceOf[Int]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |    ()
          |  }
          |  def getSchema: org.apache.avro.Schema = Card.SCHEMA$
          |}
          |
          |object Card {
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Card\",\"namespace\":\"example.idl\",\"fields\":[{\"name\":\"suit\",\"type\":{\"type\":\"enum\",\"name\":\"Suit\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}},{\"name\":\"number\",\"type\":\"int\"}]}")
          |}""".stripMargin.trim
    }


    "correctly generate nested enums in AVSCs with `SpecificRecord`" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/enums_nested.avsc")
      val gen = new Generator(SpecificRecord)
      val outDir = gen.defaultOutputDir + "/specific/"
      gen.fileToFile(infile, outDir)

      val sourceEnum = scala.io.Source.fromFile(s"$outDir/example/Direction.java").mkString
      sourceEnum ====
      """/**
        | * Autogenerated by Avro
        | * 
        | * DO NOT EDIT DIRECTLY
        | */
        |package example;  
        |@SuppressWarnings("all")
        |@org.apache.avro.specific.AvroGenerated
        |public enum Direction { 
        |  NORTH, SOUTH, EAST, WEST  ;
        |  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Direction\",\"namespace\":\"example\",\"symbols\":[\"NORTH\",\"SOUTH\",\"EAST\",\"WEST\"]}");
        |  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
        |}
      |""".stripMargin

      val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/Compass.scala").mkString
      sourceRecord ====
      """/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
        |package example
        |
        |case class Compass(var direction: Direction) extends org.apache.avro.specific.SpecificRecordBase {
        |  def this() = this(null)
        |  def get(field: Int): AnyRef = {
        |    field match {
        |      case pos if pos == 0 => {
        |        direction
        |      }.asInstanceOf[AnyRef]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |  }
        |  def put(field: Int, value: Any): Unit = {
        |    field match {
        |      case pos if pos == 0 => this.direction = {
        |        value
        |      }.asInstanceOf[Direction]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |    ()
        |  }
        |  def getSchema: org.apache.avro.Schema = Compass.SCHEMA$
        |}
        |
        |object Compass {
        |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Compass\",\"namespace\":\"example\",\"fields\":[{\"name\":\"direction\",\"type\":{\"type\":\"enum\",\"name\":\"Direction\",\"symbols\":[\"NORTH\",\"SOUTH\",\"EAST\",\"WEST\"]}}]}")
        |}""".stripMargin.trim
    }

    "correctly generate default values in AVDL with `SpecificRecord`" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/defaults.avdl")
      val gen = new Generator(SpecificRecord)
      val outDir = gen.defaultOutputDir + "/specific/"
      gen.fileToFile(infile, outDir)

      val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/idl/DefaultTest.scala").mkString
      sourceRecord ====
        """/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
          |package example.idl
          |
          |case class DefaultTest(var suit: DefaultEnum = DefaultEnum.SPADES, var number: Int = 0, var str: String = "str", var optionString: Option[String] = None, var optionStringValue: Option[String] = Some("default"), var embedded: Embedded = new Embedded(1), var defaultArray: List[Int] = List(1, 3, 4, 5), var optionalEnum: Option[DefaultEnum] = None) extends org.apache.avro.specific.SpecificRecordBase {
          |  def this() = this(DefaultEnum.SPADES, 0, "str", None, Some("default"), new Embedded(1), List(1, 3, 4, 5), None)
          |  def get(field: Int): AnyRef = {
          |    field match {
          |      case pos if pos == 0 => {
          |        suit
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 1 => {
          |        number
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 2 => {
          |        str
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 3 => {
          |        optionString match {
          |          case Some(x) => x
          |          case None => null
          |        }
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 4 => {
          |        optionStringValue match {
          |          case Some(x) => x
          |          case None => null
          |        }
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 5 => {
          |        embedded
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 6 => {
          |        java.util.Arrays.asList(({
          |          defaultArray map { x =>
          |            x
          |          }
          |        }: _*))
          |      }.asInstanceOf[AnyRef]
          |      case pos if pos == 7 => {
          |        optionalEnum match {
          |          case Some(x) => x
          |          case None => null
          |        }
          |      }.asInstanceOf[AnyRef]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |  }
          |  def put(field: Int, value: Any): Unit = {
          |    field match {
          |      case pos if pos == 0 => this.suit = {
          |        value
          |      }.asInstanceOf[DefaultEnum]
          |      case pos if pos == 1 => this.number = {
          |        value
          |      }.asInstanceOf[Int]
          |      case pos if pos == 2 => this.str = {
          |        value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        }
          |      }.asInstanceOf[String]
          |      case pos if pos == 3 => this.optionString = {
          |        Option(value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        })
          |      }.asInstanceOf[Option[String]]
          |      case pos if pos == 4 => this.optionStringValue = {
          |        Option(value match {
          |          case (value: org.apache.avro.util.Utf8) => value.toString
          |          case _ => value
          |        })
          |      }.asInstanceOf[Option[String]]
          |      case pos if pos == 5 => this.embedded = {
          |        value
          |      }.asInstanceOf[Embedded]
          |      case pos if pos == 6 => this.defaultArray = {
          |        value match {
          |          case null => null
          |          case (array: org.apache.avro.generic.GenericData.Array[_]) => {
          |            scala.collection.JavaConversions.asScalaIterator(array.iterator).toList map { x =>
          |              x
          |            }
          |          }
          |        }
          |      }.asInstanceOf[List[Int]]
          |      case pos if pos == 7 => this.optionalEnum = {
          |        Option(value)
          |      }.asInstanceOf[Option[DefaultEnum]]
          |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
          |    }
          |    ()
          |  }
          |  def getSchema: org.apache.avro.Schema = DefaultTest.SCHEMA$
          |}
          |
          |object DefaultTest {
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DefaultTest\",\"namespace\":\"example.idl\",\"fields\":[{\"name\":\"suit\",\"type\":{\"type\":\"enum\",\"name\":\"DefaultEnum\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]},\"default\":\"SPADES\"},{\"name\":\"number\",\"type\":\"int\",\"default\":0},{\"name\":\"str\",\"type\":\"string\",\"default\":\"str\"},{\"name\":\"optionString\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"optionStringValue\",\"type\":[\"string\",\"null\"],\"default\":\"default\"},{\"name\":\"embedded\",\"type\":{\"type\":\"record\",\"name\":\"Embedded\",\"fields\":[{\"name\":\"inner\",\"type\":\"int\"}]},\"default\":{\"inner\":1}},{\"name\":\"defaultArray\",\"type\":{\"type\":\"array\",\"items\":\"int\"},\"default\":[1,3,4,5]},{\"name\":\"optionalEnum\",\"type\":[\"null\",\"DefaultEnum\"],\"default\":null}]}")
          |}""".stripMargin
    }
  }

  "correctly generate records depending on others defined in a different AVDL file" in {
    val importing = new java.io.File("avrohugger-core/src/test/avro/import.avdl")
    val gen = new Generator(SpecificRecord)
    val outDir = gen.defaultOutputDir + "/specific/"
    gen.fileToFile(importing, outDir)

    val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/idl/DependentRecord.scala").mkString
    sourceRecord ===
    """/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
      |package example.idl
      |
      |import other.ns.ExternalDependency
      |
      |case class DependentRecord(var dependency: ExternalDependency, var number: Int) extends org.apache.avro.specific.SpecificRecordBase {
      |  def this() = this(new ExternalDependency, 1)
      |  def get(field: Int): AnyRef = {
      |    field match {
      |      case pos if pos == 0 => {
      |        dependency
      |      }.asInstanceOf[AnyRef]
      |      case pos if pos == 1 => {
      |        number
      |      }.asInstanceOf[AnyRef]
      |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
      |    }
      |  }
      |  def put(field: Int, value: Any): Unit = {
      |    field match {
      |      case pos if pos == 0 => this.dependency = {
      |        value
      |      }.asInstanceOf[ExternalDependency]
      |      case pos if pos == 1 => this.number = {
      |        value
      |      }.asInstanceOf[Int]
      |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
      |    }
      |    ()
      |  }
      |  def getSchema: org.apache.avro.Schema = DependentRecord.SCHEMA$
      |}
      |
      |object DependentRecord {
      |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DependentRecord\",\"namespace\":\"example.idl\",\"fields\":[{\"name\":\"dependency\",\"type\":{\"type\":\"record\",\"name\":\"ExternalDependency\",\"namespace\":\"other.ns\",\"fields\":[{\"name\":\"number\",\"type\":\"int\"}]}},{\"name\":\"number\",\"type\":\"int\"}]}")
      |}""".stripMargin
  }

  "correctly generate records depending on others defined in a different AVDL file and in a nested field" in {
    val importing = new java.io.File("avrohugger-core/src/test/avro/import-nested.avdl")
    val gen = new Generator(SpecificRecord)
    val outDir = gen.defaultOutputDir + "/specific/"
    gen.fileToFile(importing, outDir)

    val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/idl/DependentRecord.scala").mkString
    sourceRecord ===
      """/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
        |package example.idl
        |
        |import other.ns.ExternalDependency
        |
        |case class DependentRecord(var dependency: Option[ExternalDependency], var number: Int) extends org.apache.avro.specific.SpecificRecordBase {
        |  def this() = this(Some(new ExternalDependency), 1)
        |  def get(field: Int): AnyRef = {
        |    field match {
        |      case pos if pos == 0 => {
        |        dependency match {
        |          case Some(x) => x
        |          case None => null
        |        }
        |      }.asInstanceOf[AnyRef]
        |      case pos if pos == 1 => {
        |        number
        |      }.asInstanceOf[AnyRef]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |  }
        |  def put(field: Int, value: Any): Unit = {
        |    field match {
        |      case pos if pos == 0 => this.dependency = {
        |        Option(value)
        |      }.asInstanceOf[Option[ExternalDependency]]
        |      case pos if pos == 1 => this.number = {
        |        value
        |      }.asInstanceOf[Int]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |    ()
        |  }
        |  def getSchema: org.apache.avro.Schema = DependentRecord.SCHEMA$
        |}
        |
        |object DependentRecord {
        |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DependentRecord\",\"namespace\":\"example.idl\",\"fields\":[{\"name\":\"dependency\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"ExternalDependency\",\"namespace\":\"other.ns\",\"fields\":[{\"name\":\"number\",\"type\":\"int\"}]}]},{\"name\":\"number\",\"type\":\"int\"}]}")
        |}""".stripMargin
  }

  "not generate copy of imported classes in the importing package" in {
    val importing = new java.io.File("avrohugger-core/src/test/avro/import.avdl")
    val gen = new Generator(SpecificRecord)
    val outDir = gen.defaultOutputDir + "/specific/"
    gen.fileToFile(importing, outDir)

    (new File(s"$outDir/example/idl/ExternalDependency.scala")).exists === false
  }

  "Generate imported classes in the declared package" in {
    val importing = new java.io.File("avrohugger-core/src/test/avro/import.avdl")
    val gen = new Generator(SpecificRecord)
    val outDir = gen.defaultOutputDir + "/specific/"
    gen.fileToFile(importing, outDir)

    (new File(s"$outDir/other/ns/ExternalDependency.scala")).exists === true
  }




}
