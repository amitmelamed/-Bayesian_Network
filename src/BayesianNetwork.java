import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A Bayesian network is a probabilistic graphical model that represents a set of variables and their conditional
 * dependencies via a directed acyclic graph (DAG).
 * Bayesian networks are ideal for taking an event that occurred and predicting the likelihood that any one of several
 * possible known causes was the contributing factor.
 * For example, a Bayesian network could represent the probabilistic relationships between diseases and symptoms.
 * Given symptoms, the network can be used to compute the probabilities of the presence of various diseases.
 * Source: Wikipedia
 *
 * Our network will be implemented by Adjacency list graph.
 * where our network holds Array of nodes.
 * and each node have Array of pointers to his parents and childrens (Outer edges and Inner edges).
 */

public class BayesianNetwork {
    private static ArrayList<Variable> network;
    private ArrayList<Query> queries;

    /**Constructor
     * Input: XML destination in the form of string
     * The constructor will define new network,
     * and will call to ImportValuesFromXML
     * to import the data from out XML file into our network
     * @param InputDestination
     */
    public BayesianNetwork(String InputDestination) {
        network = new ArrayList<>();
        queries=new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(InputDestination));
            String line = reader.readLine();
            ImportValuesFromXML("src/"+line);
            line = reader.readLine();
            while (line != null) {
                // read next line
                Query query=new Query(line,this);
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Function to read from our Values from XML file.
     * Algorithm:
     * 1.Import file data using file class.
     * 2.For each Variable create new Variable Instance.
     * 2.1 Add to Variable Instance his outcomes.
     * 2.2 Add Variable to Our network List.
     * 3.Go over all Definitions:
     * 3.1 Add edges.
     * 3.2 add CPT Table.
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

            //Go over all definitions
            nodeList = doc.getElementsByTagName("DEFINITION");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    //Import FOR node from XML into String
                    String to_string_node;
                    NodeList to_nodes=eElement.getElementsByTagName("FOR");
                    to_string_node = to_nodes.item(0).getTextContent();

                    //Import GIVEN nodes from XML into Arraylist of String
                    ArrayList<String> given_strings=new ArrayList<>();
                    NodeList given_nodes=eElement.getElementsByTagName("GIVEN");
                    for (int i = 0; i < given_nodes.getLength(); i++) {
                        given_strings.add(given_nodes.item(i).getTextContent());
                    }

                    //Add edges (chiled,parent) using add edge function
                    for (String given_string : given_strings) {
                        addEdge(to_string_node, given_string);
                    }

                    //Table string Creation and sending it to relevant Variable node to create CPT table
                    NodeList Table_Node_List=eElement.getElementsByTagName("TABLE");
                    String tableString = Table_Node_List.item(0).getTextContent();
                    for (int i = 0; i < network.size(); i++) {
                        if (network.get(i).getName().equals(to_string_node))
                        {
                            network.get(i).SetCPT(tableString);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Function to print our network data.
     */
    public void printNetwork(){
        for (int i = 0; i < network.size(); i++) {
            System.out.println(network.get(i).toString());
        }
        for (int i = 0; i < queries.size(); i++) {
            System.out.println(queries.get(i).toString());
        }
    }
    /**
     * Add new Variable to our network.
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
     * 1.1 if found -> remove instance from network and call function recursivly to find other appearance of variable name.
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

    /**
     * Algorithm for add edge in the Network:
     * 1.Check for valid input.
     * 2.Find nodes and add parent and childes edges.
     * @param from
     * @param to
     */
    public static void addEdge(String from,String to)
    {
        //Check for valid input
        if(!addEdgeValidInputCheck(from, to))
        {
            return;
        }
        //Find nodes and add parent and childes edges
        for (int i = 0; i < network.size(); i++) {
            for (int j = 0; j < network.size(); j++) {
                if(network.get(i).getName().equals(from) && network.get(j).getName().equals(to))
                {
                    network.get(i).addParent(network.get(j));
                    network.get(j).addChild(network.get(i));
                }
            }
        }
    }

    /**
     * Function to check valid input in the addEdge function.
     * @param from
     * @param to
     * @return
     */
    private static boolean addEdgeValidInputCheck(String from, String to)
    {
        //First Check if inputs are VALID
        boolean from_exist=false,to_exist=false;
        for (int i = 0; i < network.size(); i++) {
            if(network.get(i).getName().equals(from))
            {
                from_exist=true;
            }
            if(network.get(i).getName().equals(to))
            {
                to_exist=true;
            }
        }
        if(!from_exist)
        {
            System.out.println("From node does not exist");
            return false;
        }
        if(!to_exist)
        {
            System.out.println("From node does not exist");
            return false;
        }
        return true;
    }

    /**
     * Get Variable by name function.
     * Throws expection if the variable not found.
     * In case of Duplicate variable -> return the first one.
     * @param name
     * @return
     * @throws Exception
     */
    public Variable getVariableByName(String name) throws Exception {
        for (Variable variable : network) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        throw new Exception("Variable Not Found");
    }

    /**
     * Add Query to our network
     * @param query
     */
    public void addQuery(Query query)
    {
        queries.add(query);
    }
}

