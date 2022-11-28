import java.util.ArrayList;
import java.util.List;

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
}
