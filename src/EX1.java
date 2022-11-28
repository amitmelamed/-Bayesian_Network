import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class EX1 {


    public static void main(String[] args) {
        BayesianNetwork bayesianNetwork=new BayesianNetwork("src/alarm_net.xml");
        bayesianNetwork.printNetwork();
    }
}
