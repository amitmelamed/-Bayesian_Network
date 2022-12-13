public class EX1 {
    /**
     * Basic Main function that create a BayesianNetwork, and import Queries
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BayesianNetwork network = new BayesianNetwork("input.txt");
        for (int i = 0; i < network.getQueries().size(); i++) {
            network.getQueries().get(i).algorithm_1();
        }
    }
}
