import javax.crypto.spec.PSource;
import java.util.*;

public class Factor {
    /**
     * name variable form is: "v1,v2,...,vn"
     * Where v1,v2,...vn are the variables in the given factor.
     */
    public String name;
    /**
     * table is the Truth Table of the Factor.
     * The key is String of the form:
     * P(v1=outcome_1,v2=outcome_2,...,vn=outcome_n)
     * the value is the probability of this query to happen.
     */
    public HashMap<String,Double> table;
    public ArrayList<String> variables_names;
    public ArrayList<Variable> variablesOfCurrentFactor;
    private static int index_count=1;
    private int index;

    /**
     * Constructor that called from Variable class
     * @param CPT
     * @param variables
     */
    public Factor(ArrayList<Double> CPT,  ArrayList<Variable> variables)
    {
        this.variablesOfCurrentFactor=variables;
        table=new HashMap<>();
        variables_names=new ArrayList<>();
        //Indexing the factor
        index=index_count;
        index_count++;
        //Create the name of the Factor
        //Initialize Variables names arraylist
        name="";
        for (int i = 0; i < variables.size(); i++) {
            name+=variables.get(i).getName();
            if(i!=variables.size()-1)
            {
                name+=",";
            }
            variables_names.add(variables.get(i).getName());
        }


        //To create the hashmap -> we first need to iterate over all the combinations of the possible outcomes of our variables.
        //To to this we first create a table of size product_of_outcomes * numbe.

        int product =1;
        for (int i = 0; i < variables.size(); i++) {
            product*=variables.get(i).getOutcomes().size();
        }
        String [][] matrix =new String[product][variables.size()];

        //After we create the matrix: we need to fill in the possible outcomes.
        ArrayList<Integer> outcomes_count_per_var=new ArrayList<>();
        for (int i = 0; i < variables.size(); i++) {
            outcomes_count_per_var.add(variables.get(i).getOutcomes().size());
        }
        fillMatrix(matrix,outcomes_count_per_var,variables,0,variables.size(),1);
        for (int i = 0; i < matrix.length; i++) {
            String key="";
            for (int j = 0; j < matrix[i].length; j++) {
                key+=matrix[i][j];
                if(j!= matrix[i].length-1)
                {
                    key+=",";
                }
            }
            table.put(key,CPT.get(i));
        }
    }

    /**
     * Called From Elimination function
     */
    public Factor(ArrayList<Variable> variables,HashMap<String,Double> table){

        variables_names=new ArrayList<>();
        this.table=table;
        this.variablesOfCurrentFactor=variables;
        //Indexing the factor
        index=index_count;
        index_count++;
        //Generate Variable arraylist of names
        name="";
        for (int i = 0; i < variables.size(); i++) {
            name+=variables.get(i).getName();
            if(i!=variables.size()-1)
            {
                name+=",";
            }
            variables_names.add(variables.get(i).getName());
        }
    }

    /**
     * Private function that fills out matrix of variables with all their possible combination
     * @param matrix
     * @param outcomes_count_per_var
     * @param variables
     * @param start
     * @param end
     * @param product
     */
    static void fillMatrix(String[][] matrix, ArrayList<Integer> outcomes_count_per_var, ArrayList<Variable> variables, int start, int end, int product) {
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
                    matrix[iteration][start] = variables.get(start ).getName() + "=" + variables.get(start ).getOutcomes().get(j);
                    iteration++;
                    if (iteration == matrix.length) {
                        break;
                    }
                }
                j++;
                j = j % outcomes_count_per_var.get(start );
            }
            product *= outcomes_count_per_var.get(start );

        }
        //Recursively call the function to fill the next coll
        fillMatrix(matrix, outcomes_count_per_var, variables, start + 1, end, product);
    }


    /**
     * Static function that get two factors and operate "Join" on them.
     * return the result as new Factor variable.
     * @param factor1
     * @param factor2
     * @return
     */
    public static Factor Join(Factor factor1,Factor factor2,Query query)
    {
        if(factor1.table.size()<=1 )
        {
            return factor2;
        }
        if(factor2.table.size()<=1)
        {
            return factor1;
        }
        //Get variables for the new Factor.
        ArrayList<Variable> variablesOfNewFactor=new ArrayList<>();
        variablesOfNewFactor.addAll(factor1.variablesOfCurrentFactor);
        for (int i = 0; i < factor2.variablesOfCurrentFactor.size(); i++) {
            if(!variablesOfNewFactor.contains(factor2.variablesOfCurrentFactor.get(i)))
            {
                variablesOfNewFactor.add(factor2.variablesOfCurrentFactor.get(i));
            }
        }

        //Construct Matrix represent all values of variables outcomes.
        int product =1;
        for (int i = 0; i < variablesOfNewFactor.size(); i++) {

            product*=variablesOfNewFactor.get(i).getOutcomes().size();
        }
        String [][] matrix = new String[product][variablesOfNewFactor.size()];

        ArrayList<Integer> outcomes_count_per_var=new ArrayList<>();
        for (int i = 0; i < variablesOfNewFactor.size(); i++) {
            outcomes_count_per_var.add(variablesOfNewFactor.get(i).getOutcomes().size());
        }

        fillMatrix(matrix,outcomes_count_per_var,variablesOfNewFactor,0,variablesOfNewFactor.size(),1);

        ArrayList<Double> probabilities=new ArrayList<>();

        //Probabilities of the i row
        for (int i = 0; i < matrix.length; i++) {
            //Get prob from first Factor
            String key_first_factor = "";
            for (int j = 0; j < factor1.variables_names.size(); j++) {
                for (int k = 0; k < matrix[i].length; k++) {
                    String nameOfCurrentMatrixVar=matrix[i][k].substring(0,matrix[i][k].indexOf("="));
                    if(factor1.variables_names.get(j).equals(nameOfCurrentMatrixVar))
                    {
                        key_first_factor+=factor1.variables_names.get(j)+matrix[i][k].substring(matrix[i][k].indexOf("="));

                        key_first_factor+=",";
                    }
                }
            }
            key_first_factor=key_first_factor.substring(0,key_first_factor.length()-1);



            //Get prob from second Factor
            String key_sec_factor = "";
            for (int j = 0; j < factor2.variables_names.size(); j++) {
                for (int k = 0; k < matrix[i].length; k++) {
                    String nameOfCurrentMatrixVar=matrix[i][k].substring(0,matrix[i][k].indexOf("="));
                    if(factor2.variables_names.get(j).equals(nameOfCurrentMatrixVar))
                    {
                        key_sec_factor+=factor2.variables_names.get(j)+matrix[i][k].substring(matrix[i][k].indexOf("="));
                        key_sec_factor+=",";
                    }
                }
            }



            key_sec_factor=key_sec_factor.substring(0,key_sec_factor.length()-1);
            Double firstProb=factor1.table.get(key_first_factor);
            Double secProb=factor2.table.get(key_sec_factor);
            if(firstProb==null)
            {
                return factor2;
            }
            if(secProb==null)
            {
                return factor1;
            }
            else
            {
                probabilities.add(firstProb*secProb);
                query.algorithm_2_multi_count++;

            }
        }


        System.out.println();
        System.out.println(probabilities);
        System.out.println();
        return new Factor(probabilities,variablesOfNewFactor);
    }

    /**
     * Eliminate variable from our factor.
     * Return a new Factor - after elimination of @variableToEliminate
     * @param variableToEliminate
     */
    public Factor Eliminate(String variableToEliminate,Query query)
    {
        //Create a list of all the variables that will stay after elimination
        ArrayList<Variable> variablesAfterElimination=new ArrayList<>();
        //Index representing the variableToEliminate Index of our variable list
        int indexOfVariableToEliminate=-1;
        Variable eliminatedVariable = new Variable("NULL",new ArrayList<>());
        for (int i = 0; i < variablesOfCurrentFactor.size(); i++) {
            if (!variablesOfCurrentFactor.get(i).getName().equals(variableToEliminate))
            {
                variablesAfterElimination.add(variablesOfCurrentFactor.get(i));
            }
            else {
                indexOfVariableToEliminate=i;
                eliminatedVariable=variablesOfCurrentFactor.get(i);
            }
        }
        if(indexOfVariableToEliminate==-1)
        {
            return this;
        }

        //Create a matrix representing all the combination of our remaining variables

        //To create the matrix we need to know the matrix size which is product*var_size
        int product =1;
        for (int i = 0; i < variablesAfterElimination.size(); i++) {
            product*=variablesAfterElimination.get(i).getOutcomes().size();
        }

        String [][] matrix = new String[product][variablesAfterElimination.size()];

        //To fill the matrix we need Arraylist of size of outcomes of each variable
        ArrayList<Integer> outcomes_count_per_var=new ArrayList<>();
        for (int i = 0; i < variablesAfterElimination.size(); i++) {
            outcomes_count_per_var.add(variablesAfterElimination.get(i).getOutcomes().size());
        }
        fillMatrix(matrix,outcomes_count_per_var,variablesAfterElimination,0,variablesAfterElimination.size(),1);



        HashMap<String,Double> newMap=new HashMap<>();


        for (int i = 0; i < matrix.length; i++) {
            //For each combination -> we will sum all possible outcome for the variable we eliminated.
            Double sum=0.0;
            //Calculate sum
            for (int j = 0; j < eliminatedVariable.getOutcomes().size(); j++) {
                String key="";
                for (int k = 0; k < matrix[i].length; k++) {
                    if(k==indexOfVariableToEliminate)
                    {
                        key+=eliminatedVariable.getName();
                        key+="=";
                        key+=eliminatedVariable.getOutcomes().get(j);
                        key+=",";
                    }
                    key+=matrix[i][k];
                    if(k!= matrix[i].length-1){
                        key+=",";
                    }
                }
                if(indexOfVariableToEliminate==matrix[i].length)
                {
                    key+=",";
                    key+=eliminatedVariable.getName();
                    key+="=";
                    key+=eliminatedVariable.getOutcomes().get(j);
                }




                if(sum!=0 && variablesAfterElimination.size()>0)
                {
                    query.algorithm_2_add_count++;

                }
                sum+=table.get(key);


            }
            //Generate new key
            String newKey="";
            for (int j = 0; j < matrix[0].length; j++) {
                newKey+=matrix[i][j];
                if(j!=matrix[0].length-1)
                {
                    newKey+=",";
                }
            }
            newMap.put(newKey,sum);
        }


        return new Factor(variablesAfterElimination,newMap);
    }
    /**
     * Input is of the form evidence=output.
     * Return new factor without the evidence.
     * @param evidence
     * @return
     */
    public Factor placeEvidence(String evidence)
    {
        HashMap<String,Double> newMap=new HashMap<>();
        for (Map.Entry<String, Double> set :
                table.entrySet()) {
            //Check if the current key contains our evidence
            if(set.getKey().contains(evidence))
            {
                String currentKey = set.getKey();
                ArrayList<String> varAfterPlace=new ArrayList<>();
                List<String> items = Arrays.asList(currentKey.split("\\s*,\\s*"));
                for (int i = 0; i <items.size() ; i++) {
                    if (!items.get(i).equals(evidence))
                    {
                        varAfterPlace.add(items.get(i));
                    }
                }


                //Construct key
                String key="";
                for (int i = 0; i < varAfterPlace.size(); i++) {
                    key+=varAfterPlace.get(i);
                    if(i!=varAfterPlace.size()-1)
                    {
                        key+=",";
                    }
                }
                newMap.put(key, set.getValue());
            }
            else if(!set.getKey().contains(evidence.substring(0,evidence.indexOf("="))))
            {
                newMap.put(set.getKey(),set.getValue());
            }
        }


        //Construct new variables to new factor
        ArrayList<Variable> newFactorVariables=new ArrayList<>();
        String PlaceVarName = evidence.substring(0,evidence.indexOf("="));
        for (int i = 0; i < variablesOfCurrentFactor.size(); i++) {
            if(!variablesOfCurrentFactor.get(i).getName().equals(PlaceVarName))
            {
                newFactorVariables.add(variablesOfCurrentFactor.get(i));
            }
        }


        return new Factor(newFactorVariables,newMap);
    }

    /**
     * Private function to print matrix
     * @param matrix
     */
    private static void printMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
