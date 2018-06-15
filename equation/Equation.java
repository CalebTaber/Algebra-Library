package equation;

import expression.Expression;
import term.Term;

import java.util.ArrayList;

public class Equation {

    /**
     *
     * @param e = equation as a string
     * @param variable = the variable to solve for
     */
    public Equation(String e, char variable) {
        Expression left = new Expression(e.substring(0, e.indexOf("=")));
        Expression right = new Expression(e.substring(e.indexOf("=") + 1, e.length()));
        solve(left.getTerms(), right.getTerms(), variable);
    }

    private void solve(ArrayList<Term> left, ArrayList<Term> right, char variable) {
        // solve equation
    }

}
