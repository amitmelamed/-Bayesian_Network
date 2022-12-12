import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
 * <p>
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

    static int algorithm_1_add_count=0;
    static int algorithm_1_multi_count=0;


    private static final DecimalFormat decfor = new DecimalFormat("0.00000");



    /**
     * Constructor
     * 1.Get Inputs By strings
     * 2.Pull Info from Given Network
     *
     * @param queryName         ->Pull from given network the Query Variable into main Query variable
     * @param queryOutCome
     * @param evidencesName     ->Pull from given network the Evidences Variable into Evidences List
     * @param evidencesOutComes
     * @param algorithm
     * @param network
     * @throws Exception
     */
    public Query(String queryName, String queryOutCome, ArrayList<String> evidencesName,
                 ArrayList<String> evidencesOutComes, int algorithm, BayesianNetwork network) throws Exception {

        this.network = network;
        this.query = network.getVariableByName(queryName);
        this.queryOutCome = queryOutCome;
        evidences = new ArrayList<>();
        for (String s : evidencesName) {
            evidences.add(network.getVariableByName(s));
        }
        this.evidencesOutComes = new ArrayList<>();
        this.evidencesOutComes.addAll(evidencesOutComes);
        this.algorithm = algorithm;

        //Add the current query to our given network
        network.addQuery(this);
    }

    /**
     * Constructor that gets input of the type String:
     * P(q=q_outcome|e1=outcome_e1,e2=outcome_e2,...,ek=outcome_ek),<Algorithm number>
     * This Constructor will pull the data from our query string,
     * and will check if our data exist in our network.
     * In case that our data not found -> Exception will be thrown.
     *
     * @param queryInput
     */
    public Query(String queryInput, BayesianNetwork network) throws Exception {
        ArrayList<String> evidence_names_arr = new ArrayList<>();
        ArrayList<String> evidence_outcomes_arr = new ArrayList<>();
        //Extract Query name
        int index_1 = queryInput.indexOf("(");
        int index_2 = queryInput.indexOf("=");
        String query_name = queryInput.substring(index_1 + 1, index_2);
        //Extract Query Outcome
        index_1 = queryInput.indexOf("|");
        if (index_1 == -1) {
            return;
        }
        String query_outcome = queryInput.substring(index_2 + 1, index_1);
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
     *
     * @param queryInput
     * @param evidence_names_arr
     * @param evidence_outcomes_arr
     * @param index_1
     * @return
     */
    private String extractEvidences(String queryInput, ArrayList<String> evidence_names_arr, ArrayList<String> evidence_outcomes_arr, int index_1) {
        queryInput = queryInput.substring(index_1 + 1);
        String evidence_name;
        String evidence_outcome;

        while (queryInput.contains(")")) {
            //Extract Evidence Name
            index_1 = queryInput.indexOf("=");
            evidence_name = queryInput.substring(0, index_1);
            if (evidence_name.charAt(0) == ',') {
                evidence_name = evidence_name.substring(1);
            }
            evidence_names_arr.add(evidence_name);

            //Extract Evidence Outcome
            queryInput = queryInput.substring(index_1);
            index_1 = queryInput.indexOf(",");
            evidence_outcome = queryInput.substring(1, index_1);
            //If we have ')' at our last char of evidence -> we are at the last one
            if (evidence_outcome.contains(")")) {
                evidence_outcome = evidence_outcome.substring(0, evidence_outcome.length() - 1);
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
     *
     * @param queryInput
     * @param network
     * @param query_name
     * @param query_outcome
     * @param evidence_names_arr
     * @param evidence_outcomes_arr
     * @throws Exception
     */
    private void ExtractAlgorithmInput(String queryInput, BayesianNetwork network, String query_name, String query_outcome, ArrayList<String> evidence_names_arr, ArrayList<String> evidence_outcomes_arr) throws Exception {
        int algorithm = -1;
        if (queryInput.contains("1")) {
            algorithm = 1;
        } else if (queryInput.contains("2")) {
            algorithm = 2;
        } else if (queryInput.contains("3")) {
            algorithm = 3;
        }
        this.network = network;
        this.query = network.getVariableByName(query_name);
        this.queryOutCome = query_outcome;
        evidences = new ArrayList<>();
        for (String s : evidence_names_arr) {
            evidences.add(network.getVariableByName(s));
        }
        this.evidencesOutComes = new ArrayList<>();
        this.evidencesOutComes.addAll(evidence_outcomes_arr);
        this.algorithm = algorithm;
    }

    /**
     * We know that the numerator is:
     * P(q=q
     */
    void setNumerator() {

    }

    /**
     * To String method:
     * Return String of the Form:
     * P(q=q_outcome|e1=outcome_e1,e2=outcome_e2,...,ek=outcome_ek),<Algorithm number>
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("P(" + query.getName() + "=" + queryOutCome + '|');
        for (int i = 0; i < evidences.size(); i++) {
            if (i != 0) {
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


    public double algorithm_1() throws Exception {
        double numerator;
        double denominator;

        // Numerator = P( q = q_outcome , e1 = outcome_e1 , ... , ek = outcome_ek)

        //Find what variables not in query
        ArrayList<Variable> variablesNotInQuery = new ArrayList<>();
        for (int i = 0; i < BayesianNetwork.getNetwork().size(); i++) {
            boolean exist = false;
            for (int j = 0; j < evidences.size(); j++) {
                if (BayesianNetwork.getNetwork().get(i).getName().equals(evidences.get(j).getName())) {
                    exist = true;
                }
            }
            if (!exist && !Objects.equals(BayesianNetwork.getNetwork().get(i).getName(), query.getName())) {
                variablesNotInQuery.add(BayesianNetwork.getNetwork().get(i));
            }
        }


        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < variablesNotInQuery.size(); i++) {
            arrayList.add(variablesNotInQuery.get(i).getOutcomes().size());
        }

        int product = 1;
        for (int i = 0; i < arrayList.size(); i++) {
            product *= arrayList.get(i);
        }

        int[][] matrix = new int[product][arrayList.size()];

        fillMatrix(matrix, arrayList, 0, arrayList.size(), variablesNotInQuery.size(), 1);


        //After the calculations in fill matrix
        //We will have matrix that each line represent the variables outcome we need to iterate for each
        //calculation


        double sum_numerator = 0;
        double sum_domintor=0;


        ArrayList<String> fixed_outcomes = new ArrayList<>();
        //Each row represent the values of the current
        for (int[] row : matrix) {
            fixed_outcomes = new ArrayList<>();
            fixed_outcomes.add(query.getName() + "=" + queryOutCome);
            for (int i = 0; i < evidences.size(); i++) {
                fixed_outcomes.add(evidences.get(i).getName() + "=" + evidencesOutComes.get(i));
            }
            for (int i = 0; i < variablesNotInQuery.size(); i++) {
                fixed_outcomes.add(variablesNotInQuery.get(i).getName() + "=" + variablesNotInQuery.get(i).getOutcomes().get(row[i]));
            }

            //Now in Fixed outcome in the form of:[J=T, B=T, E=T, A=T, M=T]
            //Now We need to calculate each posible fixed outcome and sum_numerator them together to get the Dominatior
            sum_numerator+=calculateProbability(fixed_outcomes);
            if (row!=matrix[0])
            {
                algorithm_1_add_count++;
            }



            //For each other outcome of the first var,
            //Calculate the other options of the first outcome
            //and add them to sum_domintor



            for (int i = 0; i < query.getOutcomes().size(); i++) {
                if (!query.getOutcomes().get(i).equals(fixed_outcomes.get(0).substring(fixed_outcomes.get(0).indexOf('=')+1)))
                {
                    fixed_outcomes.set(0,query.getName()+"="+query.getOutcomes().get(i));
                    sum_domintor+=calculateProbability(fixed_outcomes);
                    algorithm_1_add_count++;


                }
            }
        }

        denominator=sum_numerator+sum_domintor;
        algorithm_1_add_count++;


//        //Find domintor list
//        ArrayList<String> dominator_list=new ArrayList<>();
//        for (int i = 0; i < evidences.size(); i++) {
//            dominator_list.add(evidences.get(i).getName()+"="+evidencesOutComes.get(i));
//        }
//        System.out.println("DOMINTOR LIST: "+ dominator_list);
//
//
//
//


//        //Calculate donimtor
//        denominator=calculateProbability(dominator_list);
        double final_calc=sum_numerator/denominator;
//
//
//
        final_calc = final_calc*100000;
        final_calc = Math.round(final_calc);
        final_calc = final_calc /100000;
//
//        System.out.println("denominator: "+denominator+" numerator: "+numerator+" final:"+final_calc);
        System.out.println(final_calc);
        System.out.println("ADD COUNT: "+ algorithm_1_add_count+" MULTI COUNT:"+ algorithm_1_multi_count);
        return final_calc;
    }

    /**
     * Fill the matrix represent each variables' outcome permutation
     *
     * @param matrix                 -> matrix size of Variable_count * (product of outcomes sizes of each variable)
     * @param outcomes_count_per_var outcomes_count_per_var[i] = number of outcomes variable i have.
     * @param start                  -> currect col we are working at
     * @param end                    ->number of col
     * @param numberOfVar
     * @param product                -> each time we move to new col -> we need to know how many var we need to fill in sequence.
     *                               at each iteration -> we multiply the product count by outcomes_count_per_var.get[previus col index]
     *                               DO NOT ASK QUESTIONS IT WORKS.
     */
    static void fillMatrix(int[][] matrix, ArrayList<Integer> outcomes_count_per_var, int start, int end, int numberOfVar, int product) {
        //Stop Condition -> In this case we finished filling our table
        if (start == end) {
            return;
        }
        //Case 1: we are in the first col -> fill the first col with 0,1,..Var1.outcome_size
        if (start == 0) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][start] = i % outcomes_count_per_var.get(0);
            }
        } else
        //Case 2: not first col->
        //first we need to know how many outcomes we want to fill in sequence
        // we need to fill product number each col in sequence
        //then fill them
        {
            //calculate product by product *= last variable number of outcomes
            product *= outcomes_count_per_var.get(start - 1);
            int iteration = 0;
            int j = 0;
            while (iteration < matrix.length) {
                //Fill
                for (int i = 0; i < product; i++) {
                    matrix[iteration][start] = j;
                    iteration++;
                    if (iteration == matrix.length) {
                        break;
                    }
                }
                j++;
                j = j % outcomes_count_per_var.get(start);
            }

        }
        //Recursively call the function to fill the next coll
        fillMatrix(matrix, outcomes_count_per_var, start + 1, end, numberOfVar, product);
    }

    /**
     * The input is Arraylist full of strings of the form:
     * [J=T, B=T, E=T, A=T, M=F]
     * Representing the probability of:
     * P(J=T,B=T,E=T,A=T,M=F)
     * The function will return this probability.
     *
     * @param input
     */
    private double calculateProbability(ArrayList<String> input) throws Exception {


        ArrayList<Double> elementsFromCPT=new ArrayList<>();
        double product = 1;
        String variable_name;
        String variable_outcome;
        Variable currentVar;
        for (int i = 0; i < input.size(); i++) {
            variable_name = input.get(i).substring(0, input.get(i).indexOf('='));
            variable_outcome = input.get(i).substring(input.get(i).indexOf('=') + 1);
            currentVar = network.getVariableByName(variable_name);
            ArrayList<String> outputs_for_var_parents = new ArrayList<>();
            outputs_for_var_parents.add(variable_outcome);

            for (int j = currentVar.getParents().size()-1; j >=0; j--) {
                for (int k = 0; k < input.size(); k++) {
                    if (currentVar.getParents().get(j).getName().equals(input.get(k).substring(0, input.get(i).indexOf('=')))) {
                        outputs_for_var_parents.add(input.get(k).substring(input.get(k).indexOf('=') + 1));
                    }
                }
            }


            elementsFromCPT.add(currentVar.getElementFromCPT(outputs_for_var_parents));

        }

        System.out.println(input);
        System.out.println(elementsFromCPT);


        for (int i = 0; i < elementsFromCPT.size(); i++) {
            product = product * elementsFromCPT.get(i);
            if(i!=0)
            {
                algorithm_1_multi_count++;
            }

        }





        System.out.println("PRODUCT OF "+ elementsFromCPT + " IS "+product+'\n');

        return product;
    }

    public static void main(String[] args) {

    }

    public static boolean hasEqualRows(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (Arrays.equals(matrix[i], matrix[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }



}
