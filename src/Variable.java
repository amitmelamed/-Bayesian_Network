import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Variable class is representing Variable in a Bayesian Network.
 * We can Illustrate our network by Directed graph represented by Adjacency List Graph.
 * Each Variable represent a node in our graph.
 * Each Variable has:
 * 1.Name.
 * 2.Parents (Inner edges) -> Stored in an Arraylist of Variables.
 * 3.Children's (Outer edges) -> Stored in an Arraylist of Variables.
 * 4.Possible outcomes.
 * 5.CPT Table (conditional probability table).
 */
public class Variable {
    private final String name;
    private final ArrayList<Variable> parents;
    private final ArrayList<Variable> childrens;
    private final ArrayList<String> outcomes;
    private ArrayList<Double> CPT;
    public HashMap<String, Double> CPT_Table;

    /**
     * Constructor
     *
     * @param name
     * @param outcomes
     */
    public Variable(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        parents = new ArrayList<>();
        childrens = new ArrayList<>();
        CPT = new ArrayList<>();
        CPT_Table = new HashMap<>();
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public ArrayList<Variable> getParents() {
        return parents;
    }

    public ArrayList<Variable> getChildrens() {
        return childrens;
    }

    public ArrayList<String> getOutcomes() {
        return outcomes;
    }

    public ArrayList<Double> getCPT() {
        return CPT;
    }

    /**
     * Input is string in the form "Double Double ... Double"
     * The function will take the input from the string,
     * and inset the values into the CPT table of our current variable.
     *
     * @param CPT_String
     */
    public void SetCPT(String CPT_String) {
        String[] numbers = CPT_String.split(" ");
        CPT = new ArrayList<>();
        for (String number : numbers) {
            CPT.add(Double.parseDouble(number));
        }
        BuildCPT_Table();

    }

    /**
     * Add parrent - Called from addEdge function from BayesianNetwork Class.
     */
    public void addParent(Variable parent) {
        parents.add(parent);

    }

    /**
     * Add Child - Called from addEdge function from BayesianNetwork Class.
     */
    public void addChild(Variable child) {
        childrens.add(child);
    }

    /**
     * Convert variable data into string:
     * Variable Name:
     * Outcomes:
     * Parents:
     * Children's:
     * CPT:
     */
    @Override
    public String toString() {
        String variableString = "Variable Name : " + name + " ";
        for (int i = 0; i < outcomes.size(); i++) {
            variableString += ("Outcome : " + outcomes.get(i) + '\n');
        }
        variableString +=("Parents:");
        for (int i = 0; i < parents.size(); i++) {
            variableString +=(parents.get(i).getName() + ' ') ;
        }
        variableString += '\n';
        variableString +=("Children's:");
        for (int i = 0; i < childrens.size(); i++) {
            variableString +=(childrens.get(i).getName()+ ' ');
        }
        variableString += "\nCPT:";
        for (int i = 0; i < CPT.size(); i++) {
            variableString+=CPT.get(i).toString();
            variableString+=' ';
        }
        variableString+='\n';
        return variableString;
    }


    /**
     * This function input is List of Strings representing our given outcome.
     * Output is the probability of the outcome to happen according to our CPT.
     *
     * @param given_outcomes
     * @return
     */
    public double getElementFromCPT(ArrayList<String> given_outcomes) {
        //Build CPT_Table key from given_outcomes.
        String key = "P(";
        if (given_outcomes.size() == 1) {
            key += given_outcomes.get(0);
            key += ")";
        } else {
            key += given_outcomes.get(0);
            key += "|";
            for (int j = 1; j <given_outcomes.size(); j++) {
                key+=given_outcomes.get(j);
                if(j==given_outcomes.size()-1)
                {
                    key+=")";
                }else
                {
                    key+=",";
                }

            }
        }
        //Return Key
        return CPT_Table.get(key);
        /**
         * DO NOT TOUCH
         */
//        int index = 0;
//        //List Represent a list of our given outcomes by number instead of strings
//        // [first_outcome,first_outcome,second_outcome,third_outcome] -> [0,0,1,2]
//        ArrayList<Integer> list = new ArrayList<>();
//        //First we will initilize the first element of 'list' by finding his index in current_Varuable.outcome
//        for (int i = 0; i < outcomes.size(); i++) {
//            if (given_outcomes.get(0).equals(outcomes.get(i))) {
//                list.add(i);
//            }
//        }
//
//        //Then initialize all the other given outcome indexes in given_outcome to list.
//        for (int i = 1; i < given_outcomes.size(); i++) {
//            for (int j = 0; j < parents.size(); j++) {
//                for (int k = 0; k < parents.get(j).outcomes.size(); k++) {
//                    if (given_outcomes.get(i).equals(parents.get(j).outcomes.get(k))) {
//                        list.add(k);
//                        //if we found one-> set it to "" to remove duplicates
//                        given_outcomes.set(i, "");
//                    }
//                }
//            }
//        }
//
//
//        //Calculate our index
//        //Formula -> First index + SUM (list[i]*number_of_variables*i) i=1 to list.size
//        index += list.get(0);
//        for (int i = 1; i < list.size(); i++) {
//            index += (list.get(i) * outcomes.size() * (i));
//        }
        //return CPT.get(index);
    }

    private void BuildCPT_Table() {
        CPT_Table = new HashMap<>();
        int product = outcomes.size();
        for (int i = 0; i < parents.size(); i++) {
            product *= parents.get(i).outcomes.size();
        }
        String[][] matrix = new String[product][parents.size() + 1];
        //Fill in first row represent the variable outcomes
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][0] = name + "=" + outcomes.get(i % outcomes.size());
        }
        int parentIndex = 0;
        product = outcomes.size();
        //For each col expect from first
        //System.out.println(parents);
        ArrayList<Variable> reverseParents = new ArrayList<>();
        for (int i = parents.size() - 1; i >= 0; i--) {
            reverseParents.add(parents.get(i));
        }
        //System.out.println(reverseParents);
        ArrayList<Integer> countList = new ArrayList<>();
        for (int i = 0; i < reverseParents.size(); i++) {
            countList.add(reverseParents.get(i).getOutcomes().size());
        }
        //System.out.println(countList);
        if (countList.size() != 0) {
            fillMatrix(matrix, countList, reverseParents, 1, reverseParents.size() + 1, outcomes.size());
        }
        //Fill key from table to String form
        int CPT_Array_Index=0;
        for (int i = 0; i < matrix.length; i++) {
            String key = "P(";
            if (matrix[i].length == 1) {
                key += matrix[i][0];
                key += ")";
            } else {
                key += matrix[i][0];
                key += "|";
                for (int j = 1; j <matrix[i].length; j++) {
                    key+=matrix[i][j];
                    if(j==matrix[i].length-1)
                    {
                        key+=")";
                    }else
                    {
                        key+=",";
                    }
                }
            }
            CPT_Table.put(key,CPT.get(CPT_Array_Index));
            CPT_Array_Index++;
        }
    }


    /**
     * Fill matrix function is a helper function to fill up matrix,
     * representing all the combinations of our CPT table by the correct order.
     * This function fill the matrix recursively, each iteration - different column.
     * @param matrix - matrix to be filled
     * @param outcomes_count_per_var - each index of the array represent the outcomes count of the variables[i] variable.
     * @param variables - variables to be filled in the correct order.
     * @param start - the current column to fill
     * @param end - Stop condition
     * @param product - how many indexes to fill with same symbol each coll in a row.
     */
    void fillMatrix(String[][] matrix, ArrayList<Integer> outcomes_count_per_var, ArrayList<Variable> variables, int start, int end, int product) {
        //Stop Condition -> In this case we finished filling our table
        if (start == end) {
            return;
        } else
        //Case 2: not first col->
        //first we need to know how many outcomes we want to fill in sequence
        // we need to fill product number each col in sequence
        //then fill them
        {
            //calculate product by product *= last variable number of outcomes

            int iteration = 0;
            int j = 0;
            while (iteration < matrix.length) {
                //Fill
                for (int i = 0; i < product; i++) {
                    matrix[iteration][start] = variables.get(start - 1).name + "=" + variables.get(start - 1).getOutcomes().get(j);
                    iteration++;
                    if (iteration == matrix.length) {
                        break;
                    }
                }
                j++;
                j = j % outcomes_count_per_var.get(start - 1);
            }
            product *= outcomes_count_per_var.get(start - 1);

        }
        //Recursively call the function to fill the next coll
        fillMatrix(matrix, outcomes_count_per_var, variables, start + 1, end, product);
    }


    private void printMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
