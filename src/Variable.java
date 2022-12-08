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



    /**
     * Constructor
     * @param name
     * @param outcomes
     */
    public Variable(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        parents = new ArrayList<>();
        childrens = new ArrayList<>();
        CPT= new ArrayList<>();
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
     * @param CPT_String
     */
    public void SetCPT(String CPT_String)
    {
        String[] numbers = CPT_String.split(" ");
        CPT = new ArrayList<>();
        for(String number : numbers){
            CPT.add(Double.parseDouble(number));
        }
    }

    /**
     * Add Value to variable functions
     */
    public void addOutcome(String outcome)
    {
        outcomes.add(outcome);
    }

    /**
     * Add parrent - Called from addEdge function from BayesianNetwork Class.
     */
    public void addParent(Variable parent)
    {
        parents.add(parent);
    }

    /**
     * Add Child - Called from addEdge function from BayesianNetwork Class.
     */
    public void addChild(Variable child)
    {
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
    public String toString()
    {
        String variableString = "Variable Name : " + name + '\n';
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
     * @param given_outcomes
     * @return
     */
    public double getElementFromCPT(ArrayList<String> given_outcomes)
    {
        int index =0;
        //List Represent a list of our given outcomes by number instead of strings
        // [first_outcome,first_outcome,second_outcome,third_outcome] -> [0,0,1,2]
        ArrayList <Integer> list = new ArrayList<>();
        //First we will initilize the first element of 'list' by finding his index in current_Varuable.outcome
        for (int i = 0; i < outcomes.size(); i++) {
            if (given_outcomes.get(0).equals(outcomes.get(i)))
            {
                list.add(i);
            }
        }
        //Then initialize all the other given outcome indexes in given_outcome to list.
        for (int i = 1; i < given_outcomes.size(); i++) {
            for (int j = 0; j < parents.size(); j++) {
                for (int k = 0; k < parents.get(j).outcomes.size(); k++) {
                    if(given_outcomes.get(i).equals(parents.get(j).outcomes.get(k)))
                    {
                        list.add(k);
                        //if we found one-> set it to "" to remove duplicates
                        given_outcomes.set(i,"");
                    }
                }
            }
        }
        //Calculate our index
        //Formula -> First index + SUM (list[i]*number_of_variables*i) i=1 to list.size
        index+=list.get(0);
        for (int i = 1; i < list.size(); i++) {
            index+= list.get(i)*outcomes.size()*(i);
        }
        return CPT.get(index);
    }
}
