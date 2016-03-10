import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ComparisonTest {

    private static final String ALL_SOLUTIONS_TXT = "allSolutions.txt";
    private static final String UNIQUE_SOLUTIONS_TXT = "uniqueSolutions.txt";
    private static final String DUPLICATE_SOLUTIONS_TXT = "duplicateSolutions.txt";
    private static final String RESOURCES_ALL_SOLUTIONS_TXT = "resources/" + ALL_SOLUTIONS_TXT;
    private static final String RESOURCES_UNIQUE_SOLUTIONS_TXT = "resources/" + UNIQUE_SOLUTIONS_TXT;
    private static final String RESOURCES_DUPLICATE_SOLUTIONS_TXT = "resources/" + DUPLICATE_SOLUTIONS_TXT;
    private static final String INPUT_XML = "resources/input.xml";
    private static final String ROOT_ELEMENT = "ProductReference";


    public static void main(String[] args) {
        try {
            File fXmlFile = new File(INPUT_XML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
            flushFiles();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                NodeList pricedItineraries = eElement.getElementsByTagName(ROOT_ELEMENT);
                List<String> listOfXmls = new ArrayList<>();
                Set<String> hashSetOfXmls = new HashSet<>();
                List<String> listOfDuplicates = new ArrayList<>();
                for (int i = 0; i < pricedItineraries.getLength(); i++) {
                    Node node = pricedItineraries.item(i);
                    String xml = toString(node).substring(38);
                    listOfXmls.add(xml);
                    writeToFile(xml, ALL_SOLUTIONS_TXT);
                    if (hashSetOfXmls.add(xml)) {
                        writeToFile(xml, UNIQUE_SOLUTIONS_TXT);
                    } else {
                        listOfDuplicates.add(xml);
                        writeToFile(xml, DUPLICATE_SOLUTIONS_TXT);
                    }
                }
                System.out.println("Total size of list:\t\t " + listOfXmls.size());
                System.out.println("Size of Hash list:\t\t " + hashSetOfXmls.size());
                System.out.println("Size of Duplicate list:\t " + listOfDuplicates.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void flushFiles() throws IOException {

        Files.deleteIfExists(Paths.get(RESOURCES_ALL_SOLUTIONS_TXT));
        Files.deleteIfExists(Paths.get(RESOURCES_UNIQUE_SOLUTIONS_TXT));
        Files.deleteIfExists(Paths.get(RESOURCES_DUPLICATE_SOLUTIONS_TXT));

        new File(RESOURCES_ALL_SOLUTIONS_TXT).createNewFile();
        new File(RESOURCES_UNIQUE_SOLUTIONS_TXT).createNewFile();
        new File(RESOURCES_DUPLICATE_SOLUTIONS_TXT).createNewFile();
    }

    private static String toString(Node node) {
        String xml = null;
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            xml = writer.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void writeToFile(String xml, String fileName) {
        try {
            Files.write(Paths.get("resources/" + fileName), xml.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {

        }
    }

}
