import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tool to create a global unique identifier for the output of the
 * ldbc-snb-generator
 */
public class Main {
  /**
   * fileReader
   */
  public static BufferedReader fileReader;
  /**
   * fileWriter
   */
  public static BufferedWriter fileWriter;
  /**
   * Path to the generated files
   */
  private static String PATH;
  /**
   * new globalId for all verticesunique
   */
  private static long globalId;
  /**
   * global vertex counter
   */
  private static long globalVertexCount;
  /**
   * vertex counter for statistics
   */
  private static long vertexCount;
  /**
   * global edge counter
   */
  private static long globalEdgeCount;
  /**
   * Edge counter for statistics
   */
  private static long edgeCount;
  /**
   * Line token separator "|"
   */
  private static final Pattern LINE_TOKEN_SEPARATOR = Pattern.compile("\\|");
  /**
   * vertex list of all vertices
   */
  private static List<Map<Long, Long>> vertexList;
  /**
   * Map for statistical output
   */
  private static Map<String, Long> counterMapNodes;
  /**
   * Map for statistical output
   */
  private static Map<String, Long> counterMapEdges;
  /**
   * Option Help
   */
  private static final String OPTION_HELP = "h";
  /**
   * Option Statistic
   */
  private static final String OPTION_STATISTIC = "s";
  /**
   * CLI Options
   */
  private static Options OPTIONS;

  static {
    OPTIONS = new Options();
    OPTIONS.addOption(OPTION_HELP, "help", false, "Help");
    OPTIONS.addOption(OPTION_STATISTIC, "statistic", false,
      "Print " + "Statistics?");
  }

  /**
   * initialize method
   */
  private static void initialize() {
    PATH = System.getProperty("user.dir") + "/";
    counterMapNodes = new HashMap<>();
    counterMapEdges = new HashMap<>();
    vertexList = new ArrayList<>();
    globalVertexCount = 0;
    globalEdgeCount = 0;
    vertexCount = 0;
    globalId = 0;
    edgeCount = 0;
  }

  /**
   * returns new global id
   *
   * @return globalID
   */
  private static long getGlobalId() {
    return globalId++;
  }

  /**
   * Method to process the node files of the generated output
   *
   * @param name node file
   * @throws IOException
   */
  private static void readAndWriteNodes(String name) throws IOException {
    System.out.println(name);
    Map<Long, Long> nodeMap = new HashMap<>();
    boolean firstLine = true;
    fileReader = new BufferedReader(new FileReader(PATH + name));
    fileWriter = new BufferedWriter(new FileWriter(PATH + "/uid/" + name));
    String line;
    while ((line = fileReader.readLine()) != null) {
      if (!firstLine) {
        long globalId = getGlobalId();
        String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
        long ID = Long.parseLong(lineTokens[0]);
        nodeMap.put(ID, globalId);
        String writeLine = "";
        for (int i = 0; i < lineTokens.length; i++) {
          if (i == 0) {
            writeLine += globalId;
          } else {
            writeLine += lineTokens[i];
          }
          writeLine += "|";
        }
        fileWriter.write(writeLine);
        fileWriter.newLine();
        vertexCount++;
        globalVertexCount++;
      } else {
        fileWriter.write(line);
        fileWriter.newLine();
        firstLine = false;
      }
    }
    fileWriter.close();
    fileReader.close();
    counterMapNodes.put(name, vertexCount);
    vertexCount = 0;
    vertexList.add(nodeMap);
  }

  /**
   * CommandLineParser parser = new BasicParser();
   * CommandLine cmd = parser.parse(OPTIONS, args);
   * if (cmd.hasOption(OPTION_HELP)) {
   * printHelp();
   * System.exit(0);
   * }
   * performSanityCheck(cmd);
   * Method to process edge files of the generated output
   * nodeA > nodeB
   *
   * @param name   edge file
   * @param indexA index of the nodeMap of nodeA
   * @param indexB index of the nodeMap of nodeB
   * @throws IOException
   */
  private static void readAndWriteEdges(String name, int indexA,
    int indexB) throws IOException {
    System.out.println(name);
    boolean firstLine = true;
    fileReader = new BufferedReader(new FileReader(PATH + name));
    fileWriter = new BufferedWriter(new FileWriter(PATH + "/uid/" + name));
    String line;
    while ((line = fileReader.readLine()) != null) {
      if (!firstLine) {
        String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
        long globalA =
          vertexList.get(indexA).get(Long.parseLong(lineTokens[0]));
        long globalB =
          vertexList.get(indexB).get(Long.parseLong(lineTokens[1]));
        String writeLine = "";
        for (int i = 0; i < lineTokens.length; i++) {
          if (i == 0) {
            writeLine += globalA;
          } else if (i == 1) {
            writeLine += globalB;
          } else {
            writeLine += lineTokens[i];
          }
          writeLine += "|";
        }
        fileWriter.write(writeLine);
        fileWriter.newLine();
        edgeCount++;
        globalEdgeCount++;
      } else {
        fileWriter.write(line);
        fileWriter.newLine();
        firstLine = false;
      }
    }
    counterMapEdges.put(name, edgeCount);
    edgeCount = 0;
    fileReader.close();
    fileWriter.close();
  }

  /**
   * Method to process property files of the generated output
   *
   * @param name property file
   * @throws IOException
   */
  private static void readAndWriteProperties(String name) throws IOException {
    System.out.println(name);
    boolean firstLine = true;
    fileReader = new BufferedReader(new FileReader(PATH + name));
    fileWriter = new BufferedWriter(new FileWriter(PATH + "/uid/" + name));
    String line;
    while ((line = fileReader.readLine()) != null) {
      if (!firstLine) {
        String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
        long globalID = vertexList.get(3).get(Long.parseLong(lineTokens[0]));
        String writeLine = "";
        for (int i = 0; i < lineTokens.length; i++) {
          if (i == 0) {
            writeLine += globalID;
          } else {
            writeLine += lineTokens[i];
          }
          writeLine += "|";
        }
        fileWriter.write(writeLine);
        fileWriter.newLine();
      } else {
        fileWriter.write(line);
        fileWriter.newLine();
        firstLine = false;
      }
    }
    fileReader.close();
    fileWriter.close();
  }

  /**
   * method to write the statistics
   *
   * @param name name of the statistics file
   * @throws IOException
   */
  private static void writeStatistics(String name) throws IOException {
    fileWriter = new BufferedWriter(new FileWriter(PATH + "/uid/" + name));
    fileWriter.write("Number of Nodes total: " + globalVertexCount);
    fileWriter.newLine();
    fileWriter.write("Number of Edges total: " + globalEdgeCount);
    fileWriter.newLine();
    fileWriter.write("######################################");
    fileWriter.newLine();
    fileWriter.write("Nodes counted:");
    fileWriter.newLine();
    for (Map.Entry<String, Long> entry : counterMapNodes.entrySet()) {
      fileWriter
        .write(entry.getKey().replace("_0.csv", "") + ": " + entry.getValue());
      fileWriter.newLine();
    }
    fileWriter.write("######################################");
    fileWriter.newLine();
    fileWriter.write("Edges counted:");
    fileWriter.newLine();
    for (Map.Entry<String, Long> entry : counterMapEdges.entrySet()) {
      fileWriter
        .write(entry.getKey().replace("_0.csv", "") + ": " + entry.getValue());
      fileWriter.newLine();
    }
    fileWriter.close();
  }

  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Main.class.getName(), OPTIONS, false);
  }

  private static void processFiles() throws IOException {
    initialize();
    System.out.println("######################################");
    System.out.println("Process: Nodes");
    readAndWriteNodes("comment_0.csv"); // 0
    readAndWriteNodes("forum_0.csv"); // 1
    readAndWriteNodes("organisation_0.csv"); // 2
    readAndWriteNodes("person_0.csv"); // 3
    readAndWriteNodes("place_0.csv");// 4
    readAndWriteNodes("post_0.csv");// 5
    readAndWriteNodes("tag_0.csv");// 6
    readAndWriteNodes("tagclass_0.csv");// 7
    //comment edges
    System.out.println("######################################");
    System.out.println("Process: Edges");
    readAndWriteEdges("comment_hasCreator_person_0.csv", 0, 3);
    readAndWriteEdges("comment_hasTag_tag_0.csv", 0, 6);
    readAndWriteEdges("comment_isLocatedIn_place_0.csv", 0, 4);
    readAndWriteEdges("comment_replyOf_comment_0.csv", 0, 0);
    readAndWriteEdges("comment_replyOf_post_0.csv", 0, 5);
    //forum edges
    readAndWriteEdges("forum_containerOf_post_0.csv", 1, 5);
    readAndWriteEdges("forum_hasMember_person_0.csv", 1, 3);
    readAndWriteEdges("forum_hasModerator_person_0.csv", 1, 3);
    readAndWriteEdges("forum_hasTag_tag_0.csv", 1, 6);
    //organisation edges
    readAndWriteEdges("organisation_isLocatedIn_place_0.csv", 2, 4);
    //person edges
    readAndWriteEdges("person_hasInterest_tag_0.csv", 3, 6);
    readAndWriteEdges("person_isLocatedIn_place_0.csv", 3, 4);
    readAndWriteEdges("person_knows_person_0.csv", 3, 3);
    readAndWriteEdges("person_likes_comment_0.csv", 3, 0);
    readAndWriteEdges("person_likes_post_0.csv", 3, 5);
    readAndWriteEdges("person_studyAt_organisation_0.csv", 3, 2);
    readAndWriteEdges("person_workAt_organisation_0.csv", 3, 2);
    //place edges
    readAndWriteEdges("place_isPartOf_place_0.csv", 4, 4);
    //post edges
    readAndWriteEdges("post_hasCreator_person_0.csv", 5, 3);
    readAndWriteEdges("post_hasTag_tag_0.csv", 5, 6);
    readAndWriteEdges("post_isLocatedIn_place_0.csv", 5, 4);
    //tag edges
    readAndWriteEdges("tag_hasType_tagclass_0.csv", 6, 7);
    //tagclass edges
    readAndWriteEdges("tagclass_isSubclassOf_tagclass_0.csv", 7, 7);
    //read and write properties
    System.out.println("######################################");
    System.out.println("Process: Properties");
    readAndWriteProperties("person_speaks_language_0.csv");
    readAndWriteProperties("person_email_emailaddress_0.csv");
  }

  private static boolean crateDirectory() {
    boolean success =
      (new File(System.getProperty("user.dir") + "/uid")).mkdirs();
    if (success) {
      System.out.println("All files will be written to uid/");
    } else {
      success = (new File(System.getProperty("user.dir") + "/uid_0")).mkdirs();
      System.out.println("All files will be written to uid_0/");
    }
    return success;
  }

  /**
   * Main Method
   *
   * @param args given arguments
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, ParseException {
    CommandLineParser parser = new BasicParser();
    CommandLine cmd = parser.parse(OPTIONS, args);
    if (cmd.hasOption(OPTION_HELP)) {
      printHelp();
      System.exit(0);
    }
    if (crateDirectory()) {
      processFiles();
    }
    if (cmd.hasOption(OPTION_STATISTIC)) {
      //print statistics.txt with statistics
      writeStatistics("statistics.txt");
    }
  }
}
