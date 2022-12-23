import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    int algorithm_1_add_count = 0;
    int algorithm_1_multi_count = 0;
    int algorithm_2_add_count = 0;
    int algorithm_2_multi_count = 0;

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
    public Query(String queryName, String queryOutCome, ArrayList<String> evidencesName, ArrayList<String> evidencesOutComes, int algorithm, BayesianNetwork network) throws Exception {

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
        try {


            String originalInput = queryInput;
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

            System.out.println(originalInput.substring(0,originalInput.length()-2));
            if (query.CPT_Table.containsKey(originalInput.substring(0, originalInput.length() - 2))) {
                Double probibility = query.CPT_Table.get(originalInput.substring(0, originalInput.length() - 2));
                network.outputFileWriter.println(probibility + "," + 0 + "," + 0);
                return;

            }
//            boolean answerIsKnowen = true;
//            if(query.getParents().size() == evidences.size())
//            {
//                for (int i = 0; i < evidences.size(); i++) {
//                    if(!evidences.get(i).getName().equals(query.getParents().get(i).getName()))
//                    {
//                        answerIsKnowen = false;
//                    }
//                }
//                if( answerIsKnowen)
//                {
//                    ArrayList<String> evidencesWithOutcomes = new ArrayList<>();
//                    for (int i = 0; i < evidences.size(); i++) {
//                        evidencesWithOutcomes.add(evidences.get(i).getName() + "=" + evidence_outcomes_arr.get(i));
//                    }
//                    Double probibility = query.getElementFromCPT(evidencesWithOutcomes);
//                    network.outputFileWriter.println(probibility + "," + 0 + "," + 0);
//
//
//                    return;
//                }
//            }

            if (algorithm == 1) {
                algorithm_1();
            }
            if (algorithm == 2) {
                algorithm_2();
            }
            if (algorithm == 3) {
                algorithm_3();
            }
        } catch (Exception E) {
            System.out.println(E);
        }
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


    /**
     * Algorithm 1:
     * For Example
     * P(A|B,C) = P(A,B,C) / P(B,C)
     * 1.Calculate Numerator = P(A,B,C)
     * 2.Calculate Dominator = P(B,C)
     * return Numerator/Dominator
     *
     * @return
     * @throws Exception
     */
    public double algorithm_1() throws Exception {
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
        fillMatrix(matrix, arrayList, 0, arrayList.size(), 1);

        //After the calculations in fill matrix
        //We will have matrix that each line represent the variables outcome we need to iterate for each
        //calculation

        double sum_numerator = 0;
        double sum_domintor = 0;

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

            sum_numerator += calculateProbability(fixed_outcomes);
            //System.out.println(fixed_outcomes);
            if (row != matrix[0]) {
                algorithm_1_add_count++;
            }

            //Create a deep copy of the fixed outcome List - to check after this if we did this calc or not
            ArrayList<String> originalFixedOutcome = new ArrayList<>();
            for (String element : fixed_outcomes) {
                originalFixedOutcome.add(new String(element));
            }
            //For each other outcome of the first var,
            //Calculate the other options of the first outcome
            //and add them to sum_dominator
            for (int i = 0; i < query.getOutcomes().size(); i++) {
                if (!query.getOutcomes().get(i).equals(fixed_outcomes.get(0).substring(fixed_outcomes.get(0).indexOf('=') + 1))) {

                    fixed_outcomes.set(0, query.getName() + "=" + query.getOutcomes().get(i));

                    //If the new fixed outcome equal to original - we don't want to calculate this.
                    //This If statement created to prevent bugs.
                    if (!fixed_outcomes.equals(originalFixedOutcome)) {
                        sum_domintor += calculateProbability(fixed_outcomes);
                        algorithm_1_add_count++;
                    }
                }
            }
        }
        //Domintor = Numerator + sum_domintor variable
        denominator = sum_numerator + sum_domintor;

        double final_calc = sum_numerator / denominator;

        //Normlize calculation to 0.00000 Form
        final_calc = final_calc * 100000;
        final_calc = Math.round(final_calc);
        final_calc = final_calc / 100000;
        System.out.println("Numerator = " + sum_numerator);
        System.out.println("Domintor = " + sum_domintor);
        System.out.println("Final calculation : " + final_calc);
        network.outputFileWriter.println(final_calc + "," + algorithm_1_add_count + "," + algorithm_1_multi_count);
        return final_calc;
    }

    /**
     * Fill the matrix represent each variables' outcome permutation
     *
     * @param matrix                 -> matrix size of Variable_count * (product of outcomes sizes of each variable)
     * @param outcomes_count_per_var outcomes_count_per_var[i] = number of outcomes variable i have.
     * @param start                  -> currect col we are working at
     * @param end                    ->number of col
     * @param product                -> each time we move to new col -> we need to know how many var we need to fill in sequence.
     *                               at each iteration -> we multiply the product count by outcomes_count_per_var.get[previus col index]
     *                               DO NOT ASK QUESTIONS IT WORKS.
     */
    static void fillMatrix(int[][] matrix, ArrayList<Integer> outcomes_count_per_var, int start, int end, int product) {
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
        fillMatrix(matrix, outcomes_count_per_var, start + 1, end, product);
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

        ArrayList<Double> elementsFromCPT = new ArrayList<>();
        double product = 1;
        String variable_name;
        String variable_outcome;
        Variable currentVar;

        //We know that P(J=T,B=T,E=T,A=T,M=F) = P(J=T | His parents) * P(B=T | His parents) * .. * P(M=F | His parents)
        //So we import each calculation from the Variable CPT table, and multiply all of them.
        for (int i = 0; i < input.size(); i++) {
            variable_name = input.get(i).substring(0, input.get(i).indexOf('='));
            variable_outcome = input.get(i).substring(input.get(i).indexOf('=') + 1);
            currentVar = network.getVariableByName(variable_name);
            ArrayList<String> outputs_for_var_parents = new ArrayList<>();
            outputs_for_var_parents.add(variable_name + "=" + variable_outcome);


            for (int j = currentVar.getParents().size() - 1; j >= 0; j--) {
                for (int k = 0; k < input.size(); k++) {
                    if (currentVar.getParents().get(j).getName().equals(input.get(k).substring(0, input.get(i).indexOf('=')))) {
                        String outcome = input.get(k);
                        outputs_for_var_parents.add(outcome);
                    }
                }
            }
            //Add element to the CPT table
            elementsFromCPT.add(currentVar.getElementFromCPT(outputs_for_var_parents));

        }

        //Multiply all the imported probabilities from the CPT tables
        for (int i = 0; i < elementsFromCPT.size(); i++) {
            product = product * elementsFromCPT.get(i);
            if (i != 0) {
                algorithm_1_multi_count++;
            }
        }
        //The product equal P(J=T,B=T,E=T,A=T,M=F) of the given input
        return product;
    }

    /**
     * Function to print matrix for debugging
     *
     * @param matrix
     */
    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }


    /**
     * Check is son is Ancestor of father.
     *
     * @param evidence
     * @param hidden
     * @return
     */
    private static boolean isAncestor(Variable evidence, Variable hidden) {
        ArrayList<Variable> visitedNodes = new ArrayList<>();

        // Create a queue for BFS
        LinkedList<Variable> queue = new LinkedList<Variable>();
        visitedNodes.add(hidden);
        queue.add(hidden);
        while (queue.size() != 0) {
            // Dequeue a vertex from queue
            Variable current = queue.poll();
            // Get all adjacent vertices of the dequeued
            // vertex s If a adjacent has not been visited,
            // then mark it visited and enqueue it
            ArrayList<Variable> currentLeafs = current.getChildrens();
            for (int i = 0; i < currentLeafs.size(); i++) {
                if (!visitedNodes.contains(currentLeafs.get(i))) {
                    visitedNodes.add(currentLeafs.get(i));
                    queue.add(currentLeafs.get(i));
                }

            }

        }
        for (int i = 0; i < visitedNodes.size(); i++) {
            if (visitedNodes.get(i).getName().equals(evidence.getName())) {
                return true;
            }
        }
        return false;
    }


    // Takes an arraylist as a parameter and returns
    // a reversed arraylist
    private ArrayList<Variable> reverseArrayList(ArrayList<Variable> alist) {
        // Arraylist for storing reversed elements
        ArrayList<Variable> revArrayList = new ArrayList<Variable>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }

    /**
     * Variable Elimination Algorithm use to calculate our Query Probability
     * The Algorithm:
     * 1.Construct an array of all the variables Factors in the network.
     * 2.Place all the given query evidence in every CPT of the array.
     * 3.If a CPT table is empty -> remove it from the array.
     * 4.Get a List of all the Hidden variables.
     * 5.For i=0 -> i<Hidden.Size
     * 5.1.Join all the Factors contain Hidden[i] into one Joined Factor
     * 5.2.Eliminate H from the final Joined Factor
     * 6.Join all the remaining Factors.
     * 7.Normalize the remaining single Factor
     * 8.Extract final result
     *
     * @throws Exception
     */
    public void algorithm_2() throws Exception {
        try {
            //Start by making an array of all the Variables in the network
            ArrayList<Factor> factors = new ArrayList<>();
            ArrayList<Variable> hidden = new ArrayList<>();
            //Construct Hidden array
            for (int i = 0; i < BayesianNetwork.getNetwork().size(); i++) {
                boolean exist = false;
                for (int j = 0; j < evidences.size(); j++) {
                    if (BayesianNetwork.getNetwork().get(i).getName().equals(evidences.get(j).getName())) {
                        exist = true;
                    }
                }
                if (!exist && !Objects.equals(BayesianNetwork.getNetwork().get(i).getName(), query.getName())) {
                    hidden.add(BayesianNetwork.getNetwork().get(i));
                }
            }

            ArrayList<Variable> relevantHidden = new ArrayList<>();
            //Remove unnecessary hidden variables
            for (int i = 0; i < hidden.size(); i++) {
                boolean isAncestor = false;
                if (isAncestor(query, hidden.get(i))) {
                    isAncestor = true;
                }
                for (int j = 0; j < evidences.size(); j++) {
                    if (isAncestor(evidences.get(j), hidden.get(i))) {
                        isAncestor = true;
                    }
                }
                if (isAncestor) {
                    relevantHidden.add(hidden.get(i));
                }
            }


            //Construct factors Array
            factors.add(query.factor);

            for (int i = 0; i < evidences.size(); i++) {
                factors.add(evidences.get(i).factor);
            }
            for (int i = 0; i < relevantHidden.size(); i++) {
                factors.add(relevantHidden.get(i).factor);
            }

            //Place evidences in factors into newFactor
            ArrayList<Factor> newFactors = new ArrayList<>();
            for (int i = 0; i < factors.size(); i++) {
                Factor f = factors.get(i);
                for (int j = 0; j < evidences.size(); j++) {
                    String key = evidences.get(j).getName() + "=" + evidencesOutComes.get(j);
                    f = f.placeEvidence(key);

                }
                if (f.table.size() > 1) {
                    newFactors.add(f);
                }
            }


            //Sort Hidden variables by alphabet
            //In Algorithm 3 - We use different sort to better running time
            relevantHidden.sort(Comparator.comparing(Variable::getName));


            for (int i = 0; i < newFactors.size(); i++) {
                System.out.println(newFactors.get(i).table);
            }
            //For each H in Hidden
            for (int i = 0; i < relevantHidden.size(); i++) {


                Collections.sort(newFactors, new NameComparator());


                for (int j = 0; j < newFactors.size(); j++) {
                    if (newFactors.get(j).table.size() < 2) {
                        newFactors.remove(j);
                    }
                }
                //Classify Factors - Contain H / Not Contain H
                ArrayList<Factor> factorsWithH = new ArrayList<>();
                ArrayList<Factor> factorsWithoutH = new ArrayList<>();
                Variable H = relevantHidden.get(i);
                for (int j = 0; j < newFactors.size(); j++) {
                    boolean containH = false;
                    Factor currentFactor = newFactors.get(j);
                    for (int k = 0; k < currentFactor.variables_names.size(); k++) {
                        if (H.getName().equals(currentFactor.variables_names.get(k))) {
                            containH = true;
                        }
                    }
                    if (containH) {
                        factorsWithH.add(currentFactor);
                    } else {
                        factorsWithoutH.add(currentFactor);
                    }
                }


                //Join all Factors containing H and  Eliminate H
                if (factorsWithH.size() != 0) {
                    //Join all Factors containing H
                    Factor joinedFactor = factorsWithH.get(0);
                    for (int j = 1; j < factorsWithH.size(); j++) {
                        joinedFactor = Factor.Join(joinedFactor, factorsWithH.get(j), this);

                    }

                    //Construct again newFactor - Insert all factor after elimination
                    newFactors = new ArrayList<>();

                    //Eliminate H
                    joinedFactor = joinedFactor.Eliminate(H.getName(), this);

                    //add newFactors all remaining factors that H was not effecting them

                    //Add to the new factor list


                    newFactors.addAll(factorsWithoutH);
                    newFactors.add(joinedFactor);


                    //Go back to eliminate More H's


                }

            }

            //for bugs i think?
            for (int i = 0; i < newFactors.size(); i++) {
                if (newFactors.get(i).table.containsKey("")) {
                    newFactors.remove(i);
                }
            }
            Factor factor = newFactors.get(0);

            //Join all remaining Factors
            for (int i = 1; i < newFactors.size(); i++) {
                factor = Factor.Join(factor, newFactors.get(i), this);
            }
            //Factor is our final factor
            //Now we need to normalize it

            Double sum = 0.0;
            for (Map.Entry<String, Double> set : factor.table.entrySet()) {

                if (sum != 0) {
                    algorithm_2_add_count++;
                }
                // Printing all elements of a Map
                sum += set.getValue();
            }
            double alpha = 1 / sum;

            double final_calc = factor.table.get(query.getName() + "=" + queryOutCome) * alpha;
            final_calc = final_calc * 100000;
            final_calc = Math.round(final_calc);
            final_calc = final_calc / 100000;
            network.outputFileWriter.println(final_calc + "," + algorithm_2_add_count + "," + algorithm_2_multi_count);

            //System.out.println(final_calc);

        } catch (Exception e) {
            System.out.println("ERROR");
            algorithm_1();
        }

    }


    /**
     * Just like algorithm 2, but we sort Hidden variables by Different Heuristics.
     * In Each iteration we choose to join and eliminate the variable with
     * the smallest heuristicFactor.
     * heuristicFactor represent how many times the variable appears in different factors in our current time of the algorithm.
     * We can see that the running time of our algorithm decrees by 20% in average in each iteration.
     */
    private void algorithm_3() throws Exception {
        try {

            //Start by making an array of all the Variables in the network
            ArrayList<Factor> factors = new ArrayList<>();
            ArrayList<Variable> hidden = new ArrayList<>();
            //Construct Hidden array
            for (int i = 0; i < BayesianNetwork.getNetwork().size(); i++) {
                boolean exist = false;
                for (int j = 0; j < evidences.size(); j++) {
                    if (BayesianNetwork.getNetwork().get(i).getName().equals(evidences.get(j).getName())) {
                        exist = true;
                    }
                }
                if (!exist && !Objects.equals(BayesianNetwork.getNetwork().get(i).getName(), query.getName())) {
                    hidden.add(BayesianNetwork.getNetwork().get(i));
                }
            }
            ArrayList<Variable> relevantHidden = new ArrayList<>();
            //Remove unnecessary hidden variables
            for (int i = 0; i < hidden.size(); i++) {
                boolean isAncestor = false;
                if (isAncestor(query, hidden.get(i))) {
                    isAncestor = true;
                }
                for (int j = 0; j < evidences.size(); j++) {
                    if (isAncestor(evidences.get(j), hidden.get(i))) {
                        isAncestor = true;
                    }
                }
                if (isAncestor) {
                    relevantHidden.add(hidden.get(i));
                }
            }
            //Construct factors Array
            factors.add(query.factor);

            for (int i = 0; i < evidences.size(); i++) {
                factors.add(evidences.get(i).factor);
            }
            for (int i = 0; i < relevantHidden.size(); i++) {
                factors.add(relevantHidden.get(i).factor);
            }
            //Place evidences in factors into newFactor
            ArrayList<Factor> newFactors = new ArrayList<>();
            for (int i = 0; i < factors.size(); i++) {
                Factor f = factors.get(i);
                for (int j = 0; j < evidences.size(); j++) {
                    String key = evidences.get(j).getName() + "=" + evidencesOutComes.get(j);
                    f = f.placeEvidence(key);

                }
                if (f.table.size() > 1) {
                    newFactors.add(f);
                }
            }
            //In Each iteration we choose to join and eliminate the variable with
            //the smallest heuristicFactor.
            //heuristicFactor represent how many times the variable apears in different factors in our current time of the algorithm.
            for (int i = 0; i < relevantHidden.size(); i++) {
                for (int j = 0; j < newFactors.size(); j++) {
                    for (int k = 0; k < newFactors.get(j).variables_names.size(); k++) {
                        if (relevantHidden.get(i).getName().equals(newFactors.get(j).variables_names.get(k))) {
                            relevantHidden.get(i).heuristicFactor += newFactors.get(j).table.size();
                        }
                    }
                }
            }
            relevantHidden.sort(Comparator.comparing(Variable::getHeuristicFactor));

            for (int i = 0; i < newFactors.size(); i++) {
                System.out.println(newFactors.get(i).table);
            }
            //For each H in Hidden
            for (int i = 0; i < relevantHidden.size(); i++) {
                Collections.sort(newFactors, new NameComparator());
                for (int j = 0; j < relevantHidden.size(); j++) {
                    relevantHidden.get(j).heuristicFactor = 0;
                }
                for (int p = 0; p < relevantHidden.size(); p++) {
                    for (int j = 0; j < newFactors.size(); j++) {
                        for (int k = 0; k < newFactors.get(j).variables_names.size(); k++) {
                            if (relevantHidden.get(p).getName().equals(newFactors.get(j).variables_names.get(k))) {
                                relevantHidden.get(p).heuristicFactor += newFactors.get(j).table.size();
                            }
                        }
                    }
                }
                relevantHidden.sort(Comparator.comparing(Variable::getHeuristicFactor));

                for (int j = 0; j < newFactors.size(); j++) {
                    if (newFactors.get(j).table.size() < 2) {
                        newFactors.remove(j);
                    }
                }
                //Classify Factors - Contain H / Not Contain H
                ArrayList<Factor> factorsWithH = new ArrayList<>();
                ArrayList<Factor> factorsWithoutH = new ArrayList<>();
                Variable H = relevantHidden.get(i);
                for (int j = 0; j < newFactors.size(); j++) {
                    boolean containH = false;
                    Factor currentFactor = newFactors.get(j);
                    for (int k = 0; k < currentFactor.variables_names.size(); k++) {
                        if (H.getName().equals(currentFactor.variables_names.get(k))) {
                            containH = true;
                        }
                    }
                    if (containH) {
                        factorsWithH.add(currentFactor);
                    } else {
                        factorsWithoutH.add(currentFactor);
                    }
                }


                //Join all Factors containing H and  Eliminate H
                if (factorsWithH.size() != 0) {
                    //Join all Factors containing H
                    Factor joinedFactor = factorsWithH.get(0);
                    for (int j = 1; j < factorsWithH.size(); j++) {
                        joinedFactor = Factor.Join(joinedFactor, factorsWithH.get(j), this);

                    }

                    //Construct again newFactor - Insert all factor after elimination
                    newFactors = new ArrayList<>();

                    //Eliminate H
                    joinedFactor = joinedFactor.Eliminate(H.getName(), this);

                    //add newFactors all remaining factors that H was not effecting them

                    //Add to the new factor list


                    newFactors.addAll(factorsWithoutH);
                    newFactors.add(joinedFactor);


                    //Go back to eliminate More H's


                }

            }

            //for bugs i think?
            for (int i = 0; i < newFactors.size(); i++) {
                if (newFactors.get(i).table.containsKey("")) {
                    newFactors.remove(i);
                }
            }
            Factor factor = newFactors.get(0);

            //Join all remaining Factors
            for (int i = 1; i < newFactors.size(); i++) {
                factor = Factor.Join(factor, newFactors.get(i), this);
            }
            //Factor is our final factor
            //Now we need to normalize it

            Double sum = 0.0;
            for (Map.Entry<String, Double> set : factor.table.entrySet()) {

                if (sum != 0) {
                    algorithm_2_add_count++;
                }
                // Printing all elements of a Map
                sum += set.getValue();
            }
            double alpha = 1 / sum;

            double final_calc = factor.table.get(query.getName() + "=" + queryOutCome) * alpha;
            final_calc = final_calc * 100000;
            final_calc = Math.round(final_calc);
            final_calc = final_calc / 100000;
            network.outputFileWriter.println(final_calc + "," + algorithm_2_add_count + "," + algorithm_2_multi_count);


        } catch (Exception e) {
            System.out.println("ERROR");
            algorithm_1();
        }
    }

    private void algorithm_2_second_try() throws Exception {

        //Find out hiddens variables.
        ArrayList<Variable> hidden = new ArrayList<>();
        //Construct Hidden array
        for (int i = 0; i < BayesianNetwork.getNetwork().size(); i++) {
            boolean exist = false;
            for (int j = 0; j < evidences.size(); j++) {
                if (BayesianNetwork.getNetwork().get(i).getName().equals(evidences.get(j).getName())) {
                    exist = true;
                }
            }
            if (!exist && !Objects.equals(BayesianNetwork.getNetwork().get(i).getName(), query.getName())) {
                hidden.add(BayesianNetwork.getNetwork().get(i));
            }
        }

        //Remove Irrelevant Hiddens
        ArrayList<Variable> relevantHidden = new ArrayList<>();
        //Remove unnecessary hidden variables
        for (int i = 0; i < hidden.size(); i++) {
            boolean isAncestor = false;
            if (isAncestor(query, hidden.get(i))) {
                isAncestor = true;
            }
            for (int j = 0; j < evidences.size(); j++) {
                if (isAncestor(evidences.get(j), hidden.get(i))) {
                    isAncestor = true;
                }
            }
            if (isAncestor) {
                relevantHidden.add(hidden.get(i));
            }
        }

        ArrayList<Factor> factors = new ArrayList<>();
        factors.add(query.factor);
        for (int i = 0; i < evidences.size(); i++) {
            factors.add(evidences.get(i).factor);
        }

        //Place evidences
        for (int i = 0; i < factors.size(); i++) {
            Factor currentFactor = factors.get(i);
            String evidenceAndOutcome;
            for (int j = 0; j < evidences.size(); j++) {
                evidenceAndOutcome = evidences.get(j).getName() + "=" + evidencesOutComes.get(j);
                currentFactor = currentFactor.placeEvidence(evidenceAndOutcome);
            }
            factors.set(i, currentFactor);
        }

        //Remove all Factors that after placing evidences became 1 line
        ArrayList<Factor> newFactors = new ArrayList<>();
        for (int i = 0; i < factors.size(); i++) {
            if (factors.get(i).variablesOfCurrentFactor.size() != 0) {
                newFactors.add(factors.get(i));
            }
        }


        //Sort Hidden variables by alphabet
        //In Algorithm 3 - We use different sort to better running time
        relevantHidden.sort(Comparator.comparing(Variable::getName));

        for (int i = 0; i < relevantHidden.size(); i++) {
            //Current H
            Variable H = relevantHidden.get(i);
            //Classify Factors - Contain H / Not Contain H
            ArrayList<Factor> factorsWithH = new ArrayList<>();
            ArrayList<Factor> factorsWithoutH = new ArrayList<>();
            for (int j = 0; j < newFactors.size(); j++) {
                boolean containH = false;
                Factor currentFactor = newFactors.get(j);
                for (int k = 0; k < currentFactor.variables_names.size(); k++) {
                    if (H.getName().equals(currentFactor.variables_names.get(k))) {
                        containH = true;
                    }
                }
                if (containH) {
                    factorsWithH.add(currentFactor);
                } else {
                    factorsWithoutH.add(currentFactor);
                }
            }
            factorsWithH.sort(Comparator.comparing(Factor::getIndex));
            Factor joinedFactor;
            if (factorsWithH.size() == 1) {
                joinedFactor = factorsWithH.get(0);
                joinedFactor = joinedFactor.Eliminate(H.getName(), this);
                newFactors = new ArrayList<>();
                newFactors.addAll(factorsWithoutH);
                newFactors.add(joinedFactor);

            } else if (factorsWithH.size() > 1) {
                joinedFactor = factorsWithH.get(0);
                for (int j = 1; j < factorsWithH.size(); j++) {
                    joinedFactor = Factor.Join(joinedFactor, factorsWithH.get(j), this);
                }
                joinedFactor = joinedFactor.Eliminate(H.getName(), this);
                newFactors = new ArrayList<>();
                newFactors.addAll(factorsWithoutH);
                newFactors.add(joinedFactor);

            }
        }
        //Join all remeining factors
        Factor factor = newFactors.get(0);
        for (int i = 1; i < newFactors.size(); i++) {
            factor = Factor.Join(factor, newFactors.get(i), this);
        }
        //Normilaze

        Double sum = 0.0;
        for (Map.Entry<String, Double> set : factor.table.entrySet()) {

            if (sum != 0) {
                algorithm_2_add_count++;
            }
            // Printing all elements of a Map
            sum += set.getValue();
        }
        double alpha = 1 / sum;

        System.out.println(factor.table);
        System.out.println(query.getName() + "=" + queryOutCome);
        double final_calc = factor.table.get(query.getName() + "=" + queryOutCome) * alpha;
        final_calc = final_calc * 100000;
        final_calc = Math.round(final_calc);
        final_calc = final_calc / 100000;
        network.outputFileWriter.println(final_calc + "," + algorithm_2_add_count + "," + algorithm_2_multi_count);


    }

}


//Sort Hidden variables by alphabet
//In Algorithm 3 - We use different sort to better running time
//            hidden.sort(Comparator.comparing(Variable::getParentCount));