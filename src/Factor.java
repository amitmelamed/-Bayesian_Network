import java.util.ArrayList;
import java.util.HashMap;

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
        name="";
        for (int i = 0; i < variables.size(); i++) {
            name+=variables.get(i).getName();
            if(i!=variables.size()-1)
            {
                name+=",";
            }
        }
        //Initialize Variables names arraylist
        for (int i = 0; i < variables.size(); i++) {
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
    public static Factor Join(Factor factor1,Factor factor2)
    {
        //Get variables for the new Factor.
        ArrayList<Variable> variablesOfNewFactor=new ArrayList<>();
        variablesOfNewFactor.addAll(factor1.variablesOfCurrentFactor);
        for (int i = 0; i < factor2.variablesOfCurrentFactor.size(); i++) {
            if(!variablesOfNewFactor.contains(factor2.variablesOfCurrentFactor.get(i)))
            {
                variablesOfNewFactor.add(factor2.variablesOfCurrentFactor.get(i));
            }
        }
        for (int i = 0; i < variablesOfNewFactor.size(); i++) {
            System.out.println(variablesOfNewFactor.get(i).getName());
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
        System.out.println();
        //printMatrix(matrix);

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
            //System.out.println(key_first_factor);



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


            probabilities.add(firstProb*secProb);

        }
        Factor factor = new Factor(probabilities,variablesOfNewFactor);
        System.out.println(factor.table);

        return factor;
    }

    /**
     * Eliminate variable from our factor.
     * @param variable
     */
    public void Eliminate(String variable)
    {

    }




    private static void printMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
