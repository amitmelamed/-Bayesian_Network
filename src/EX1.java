import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class EX1 {


    public static void main(String[] args) throws Exception {
        //ExampleNetwork_1();
        //ExampleNetwork_2();


    }

    private static void ExampleNetwork_2() throws Exception {
        BayesianNetwork network= new BayesianNetwork("src/input.txt");
        ArrayList<String> list=new ArrayList<>();

        list.add("T");
        list.add("T");
        list.add("T");
        list.add("T");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("F");
        list.add("T");
        list.add("T");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("T");
        list.add("F");
        list.add("T");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("F");
        list.add("F");
        list.add("T");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("T");
        list.add("T");
        list.add("F");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("F");
        list.add("T");
        list.add("F");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("T");
        list.add("F");
        list.add("F");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
        list=new ArrayList<>();
        list.add("F");
        list.add("F");
        list.add("F");
        System.out.println(network.getVariableByName("A").getElementFromCPT(list));
    }

    private static void ExampleNetwork_1() throws Exception {
        BayesianNetwork network= new BayesianNetwork("src/input.txt");
        ArrayList<String> list=new ArrayList<>();
        //000
        list.add("v1");
        list.add("True");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.11");
        //100
        list=new ArrayList<>();
        list.add("v2");
        list.add("True");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.12");
        //200
        list=new ArrayList<>();
        list.add("v3");
        list.add("True");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.77");
        //010
        list=new ArrayList<>();
        list.add("v1");
        list.add("False");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.13");
        //110
        list=new ArrayList<>();
        list.add("v2");
        list.add("False");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.14");
        //210
        list=new ArrayList<>();
        list.add("v3");
        list.add("False");
        list.add("one");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.73");


        //001
        list=new ArrayList<>();
        list.add("v1");
        list.add("True");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.15");


        //101
        list=new ArrayList<>();
        list.add("v2");
        list.add("True");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.16");

        //201
        list=new ArrayList<>();
        list.add("v3");
        list.add("True");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.69");

        //011
        list=new ArrayList<>();
        list.add("v1");
        list.add("False");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.17");

        //111
        list=new ArrayList<>();
        list.add("v2");
        list.add("False");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.18");

        //211
        list=new ArrayList<>();
        list.add("v3");
        list.add("False");
        list.add("two");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.65");

        //002
        list=new ArrayList<>();
        list.add("v1");
        list.add("True");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.19");

        //102
        list=new ArrayList<>();
        list.add("v2");
        list.add("True");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.2");

        //202
        list=new ArrayList<>();
        list.add("v3");
        list.add("True");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.61");

        //012
        list=new ArrayList<>();
        list.add("v1");
        list.add("False");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.21");

        //112
        list=new ArrayList<>();
        list.add("v2");
        list.add("False");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.22");

        //212
        list=new ArrayList<>();
        list.add("v3");
        list.add("False");
        list.add("three");
        System.out.println(network.getVariableByName("Son").getElementFromCPT(list)+" 0.57");
    }
}
