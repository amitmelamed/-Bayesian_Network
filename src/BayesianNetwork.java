import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;



public class BayesianNetwork {
    private static ArrayList<Variable> network;

    /**Constructor
     * @param XML_DESTENATION
     */
    public BayesianNetwork(String XML_DESTENATION) {
        network = new ArrayList<>();
        ImportValuesFromXML(XML_DESTENATION);
    }
    /**
     * Function to read from our Values from XML file
     * Algorithm:
     * 1.Import file data using file class
     * 2.For each Variable create new Variable Instance
     * 2.1 Add to Variable Instance his outcomes
     * 2.2 Add Variable to Our network List.
     *
     *  **Using this function will Reset our network**
     *  Used Resources:
     * https://www.javatpoint.com/how-to-read-xml-file-in-java
     * @param xmlDestination
     */
    private static void ImportValuesFromXML(String xmlDestination) {
        try{
            network = new ArrayList<>();
            //creating a constructor of file class and parsing an XML file
            File file = new File(xmlDestination);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("VARIABLE");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    ArrayList<String> outcomes=new ArrayList<>();
                    NodeList outcomesList=eElement.getElementsByTagName("OUTCOME");
                    for (int i = 0; i < outcomesList.getLength(); i++) {
                        outcomes.add(outcomesList.item(i).getTextContent());
                    }
                    //Creating a new variable with the imported data from the XML file
                    Variable variable=new Variable(eElement.getElementsByTagName("NAME").item(0).getTextContent(),outcomes);
                    network.add(variable);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Function to print our network data
     */
    public void printNetwork(){
        for (int i = 0; i < network.size(); i++) {
            System.out.println(network.get(i).toString());
        }
    }

    /**
     * Add new Variable to our network
     * @param variable
     */
    public void addVariable(Variable variable)
    {
        network.add(variable);
    }

    /**
     * Remove Variable from network recursively.
     * Remove function remove all appearance.
     * Algorithm:
     * 1.Iterate over all variables.
     * 1.1 if found -> remove instance from network and call function recursivly to find other appearance of variable name
     * @param variableName
     */
    public void removeVariable(String variableName)
    {
        //For each Variable
        for (int i = 0; i < network.size(); i++) {
            if(network.get(i).getName().equals(variableName))
            {
                network.remove(i);
                removeVariable(variableName);
            }
        }
    }

}

