import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class EX1 {
    /**
     * Basic Main function that create a BayesianNetwork, and import Queries
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        PrintWriter writer = new PrintWriter("output.txt", StandardCharsets.UTF_8);
        BayesianNetwork network = new BayesianNetwork("input.txt",writer);


        writer.close();
    }
}
