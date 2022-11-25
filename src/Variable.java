import java.util.ArrayList;

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
    private ArrayList<Variable> parents;
    private ArrayList<Variable> childrens;
    private ArrayList<String> outcomes;
    private ArrayList<Double> CPT;


    /**
     * Constructor
     * @param name
     * @param outcomes
     */
    public Variable(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
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
     * Setters
     * @param probabilities
     */
    public void SetCPT(ArrayList<Double> probabilities)
    {
        CPT=probabilities;
    }


    /**
     * Add Value to variable functions
     */
    public void addOutcome(String outcome)
    {
        outcomes.add(outcome);
    }
    public void addParent(Variable parent)
    {
        parents.add(parent);
    }
    public void addChild(Variable child)
    {
        parents.add(child);
    }

    /**
     * Print function
     */
    public void printVariable() {
        System.out.println("Variable Name : " + name );
        for (int i = 0; i < outcomes.size(); i++) {
            System.out.println("Outcome : " + outcomes.get(i) );
        }
    }

    @Override
    public String toString()
    {
        String variableString = "Variable Name : " + name + '\n';
        for (int i = 0; i < outcomes.size(); i++) {
            variableString += ("Outcome : " + outcomes.get(i) + '\n');
        }
        return variableString;

    }
}
