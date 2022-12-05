import java.util.ArrayList;

/**
 * The Query class represents a Query in our given network
 * We want to calculate what is the probability of:
 * Query Variable = Query Outcome
 * Given:
 * Evidence_1 = EvidenceOutcome_1
 * Evidence_2 = EvidenceOutcome_2
 * Evidence_3 = EvidenceOutcome_3
 * .
 * .
 * .
 * Evidence_k = EvidenceOutcome_k
 *
 * Outcome will be our desired probability calculated with the chosen algorithm.
 * Algorithm to choose from:
 * Choose 1 to
 */
public class Query {
    Variable query;
    String queryOutCome;
    ArrayList<Variable> evidences;
    ArrayList<String> evidencesOutComes;
    int algorithm;
    BayesianNetwork network;

    /**
     * Constructor
     * 1.Get Inputs By strings
     * 2.Pull Info from Given Network
     * @param queryName ->Pull from given network the Query Variable into main Query variable
     * @param queryOutCome
     * @param evidencesName ->Pull from given network the Evidences Variable into Evidences List
     * @param evidencesOutComes
     * @param algorithm
     * @param network
     * @throws Exception
     */
    public Query(String queryName,String queryOutCome,ArrayList<String> evidencesName,
                 ArrayList<String> evidencesOutComes, int algorithm, BayesianNetwork network) throws Exception {

        this.network=network;
        this.query = network.getVariableByName(queryName);
        this.queryOutCome=queryOutCome;
        evidences = new ArrayList<>();
        for (String s : evidencesName) {
            evidences.add(network.getVariableByName(s));
        }
        this.evidencesOutComes = new ArrayList<>();
        this.evidencesOutComes.addAll(evidencesOutComes);
        this.algorithm=algorithm;

        //Add the current query to our given network
        network.addQuery(this);
    }

    /**
     * Constructor that gets input of the type String:
     * P(q=q_outcome|e1=outcome_e1,e2=outcome_e2,...,ek=outcome_ek),<Algorithm number>
     * This Constructor will pull the data from our query string,
     * and will check if our data exist in our network.
     * In case that our data not found -> Exception will be thrown.
     * @param queryInput
     */
    public Query(String queryInput,BayesianNetwork network) throws Exception {
        ArrayList<String> evidence_names_arr=new ArrayList<>();
        ArrayList<String> evidence_outcomes_arr=new ArrayList<>();
        //Extract Query name
        int index_1=queryInput.indexOf("(");
        int index_2 = queryInput.indexOf("=");
        String query_name = queryInput.substring(index_1 + 1, index_2);
        //Extract Query Outcome
        index_1= queryInput.indexOf("|");
        String query_outcome = queryInput.substring(index_2+1,index_1);
        //Extract Evidences
        queryInput = extractEvidences(queryInput, evidence_names_arr, evidence_outcomes_arr, index_1);
        //Extract algorithm input
        ExtractAlgorithmInput(queryInput, network, query_name, query_outcome, evidence_names_arr, evidence_outcomes_arr);
        //Add the current query to our given network
        network.addQuery(this);
    }


    /**
     * Extract Evidences From Input String
     * Called From String Constructor
     * @param queryInput
     * @param evidence_names_arr
     * @param evidence_outcomes_arr
     * @param index_1
     * @return
     */
    private String extractEvidences(String queryInput, ArrayList<String> evidence_names_arr, ArrayList<String> evidence_outcomes_arr, int index_1) {
        queryInput = queryInput.substring(index_1 +1);
        String evidence_name;
        String evidence_outcome;

        while (queryInput.contains(")"))
        {
            //Extract Evidence Name
            index_1 = queryInput.indexOf("=");
            evidence_name= queryInput.substring(0, index_1);
            if(evidence_name.charAt(0)==',')
            {
                evidence_name=evidence_name.substring(1);
            }
            evidence_names_arr.add(evidence_name);

            //Extract Evidence Outcome
            queryInput = queryInput.substring(index_1);
            index_1 = queryInput.indexOf(",");
            evidence_outcome = queryInput.substring(1, index_1);
            //If we have ')' at our last char of evidence -> we are at the last one
            if(evidence_outcome.contains(")"))
            {
                evidence_outcome=evidence_outcome.substring(0,evidence_outcome.length()-1);
                evidence_outcomes_arr.add(evidence_outcome);
                queryInput = queryInput.substring(index_1);
                break;
            }
            evidence_outcomes_arr.add(evidence_outcome);
            queryInput = queryInput.substring(index_1);
        }
        return queryInput;
    }

    /**
     * Extract Algorithm Input from Input String
     * Called From String Constructor
     * @param queryInput
     * @param network
     * @param query_name
     * @param query_outcome
     * @param evidence_names_arr
     * @param evidence_outcomes_arr
     * @throws Exception
     */
    private void ExtractAlgorithmInput(String queryInput, BayesianNetwork network, String query_name, String query_outcome, ArrayList<String> evidence_names_arr, ArrayList<String> evidence_outcomes_arr) throws Exception {
        int algorithm=-1;
        if(queryInput.contains("1"))
        {
            algorithm =1;
        }
        else if(queryInput.contains("2"))
        {
            algorithm =2;
        }
        else if(queryInput.contains("3"))
        {
            algorithm =3;
        }
        this.network= network;
        this.query = network.getVariableByName(query_name);
        this.queryOutCome= query_outcome;
        evidences = new ArrayList<>();
        for (String s : evidence_names_arr) {
            evidences.add(network.getVariableByName(s));
        }
        this.evidencesOutComes = new ArrayList<>();
        this.evidencesOutComes.addAll(evidence_outcomes_arr);
        this.algorithm=algorithm;
    }

    /**
     * To String method:
     * Return String of the Form:
     * P(q=q_outcome|e1=outcome_e1,e2=outcome_e2,...,ek=outcome_ek),<Algorithm number>
     * @return
     */
    @Override
    public String toString() {
        StringBuilder string= new StringBuilder("P(" + query.getName() + "=" + queryOutCome + '|');
        for (int i = 0; i < evidences.size(); i++) {
            if(i!=0)
            {
                string.append(',');
            }

            string.append(evidences.get(i).getName());
            string.append('=');
            string.append(evidencesOutComes.get(i));
        }
        string.append(')');
        string.append(',');
        string.append(algorithm);
        return string.toString();
    }
}
