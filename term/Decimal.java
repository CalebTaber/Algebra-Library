package term;

import expression.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static utils.Utils.*;

public class Decimal extends Term {

    public static final Decimal NEG_ONE = new Decimal(new BigDecimal(-1), Decimal.ONE, null);
    public static final Decimal ZERO = new Decimal(BigDecimal.ZERO, Decimal.ONE, null);
    public static final Decimal ONE = new Decimal(BigDecimal.ONE, (Term) null, null);

    private BigDecimal value;
    private ArrayList<Term> exponent;
    private HashMap<Character, ArrayList<Term>> variables;

    public Decimal(BigDecimal val, Term exp, HashMap<Character, ArrayList<Term>> vars) {
        if (val == null) throw new NullPointerException("Cannot pass null BigDecimal into the value of a Decimal");

        value = val;
        exponent = (exp == null) ? null : termToList(exp);
        variables = new HashMap<>();

        if (vars != null) {
            for (char c : vars.keySet()) {
                variables.put(c, vars.get(c));
            }
        }
    }

    public Decimal(BigDecimal val, ArrayList<Term> exp, HashMap<Character, ArrayList<Term>> vars) {
        if (val == null) throw new NullPointerException("Cannot pass null BigDecimal into the value of a Decimal");

        value = val;
        exponent = exp;

        if (vars != null) {
            for (char c : vars.keySet()) {
                variables.put(c, vars.get(c));
            }
        }
    }

    public Decimal(String e) {
        parse(e);
    }

    private void parse(String decimal) {
        if (decimal.startsWith("-") && Character.isAlphabetic(decimal.charAt(1)))
                value = new BigDecimal(-1); // If the term's numerical value is -1, but the 1 is omitted
        else {
            if (Character.isAlphabetic(decimal.charAt(0)))
                value = BigDecimal.ONE; // If the term's numerical value is 1, but the 1 is omitted
            else if (Character.isDigit(decimal.charAt(0)))
                value = new BigDecimal(decimal.substring(0, decimal.indexOf("^"))); // If the term's numerical value is longer than one character
        }

        decimal = decimal.substring(decimal.indexOf("^") + 1); // Delete the numerical value from the String
        String exp = parseParenthetical(decimal);
        exponent = (exp.length() == 1) ? termToList(Decimal.ONE) : new Expression(exp).getTerms();
        decimal = decimal.substring(exp.length() + 2); // Add 2 to account for the opening and closing parentheses

        while (!decimal.isEmpty()) {
            String tmp = parseParenthetical(decimal.substring(2)); // Start at index 2 so that the variable and the carat are skipped over
            variables.put(decimal.charAt(0), new Expression(tmp).getTerms());
            decimal = decimal.substring(tmp.length() + 4); // Add 4 to account for the variable, carat, and opening/closing parentheses
        }
    }

    /*
    private void parse(String e) {
        System.out.println("DECIMAL.JAVA | parse() | input: " + e);
        exponent = new ArrayList<>();
        variables = new HashMap<>();

        for (int i = 0, j = 0; i < e.length(); i++) {
            char c = e.charAt(i);

            if (value == null) { // If the value has not been set yet
                if (Character.isLetter(c)) {
                    if (i == 0) { // If there is no stated coefficient (implying 1 as a coefficient)
                        if (atEnd(i, e)) { // If the term is a single variable with no exponent. Ex: x
                            value = new BigDecimal(1);
                            variables.put(c, termToList(Decimal.ONE));
                        }
                        value = new BigDecimal(1); // If the term has no defined coefficient
                    }
                    else if (i == 1 && e.charAt(0) == '-') value = new BigDecimal(-1); // If the coefficient is -1, stated by a single '-' before the variables
                    else {
                        value = new BigDecimal(e.substring(j, i));
                        i--; // Decrement i so that variable parsing can occur 5x^(2) -> i == 1; next cycle of loop, i == 2, and variable parsing cannot begin
                        j = i;
                    }
                } else if (c == '^') {
                    value = new BigDecimal(e.substring(j, i));
                    j = i;
                    i--;

                    // Find exponent of the value
                    for (int k = i + 3, p = 1; k < e.length(); k++) { // Add 3 to k so that the opening parenthesis can be bypassed
                        char r = e.charAt(k);

                        if (r == '(') p++;
                        else if (r == ')') {
                            p--;
                            if (p == 0) {
                                System.out.println("DECIMAL.JAVA | parse() | exponent: " + e.substring(i + 3, k));
                                exponent = new Expression(e.substring(i + 2,  k)).getTerms(); // When the end of the exponent is found, simplify it and set the value of exponent
                                i = k;
                                j = i + 1; // So that the closing parenthesis is not included in any later parsings
                                break;
                            }
                        }
                    }
                } else if (atEnd(i, e)) { // If the end of the string is reached
                    if (Character.isDigit(c)) value = new BigDecimal(e); // If the term is the whole string
                    else {
                        if(Character.isLetter(c)) { // If the final character in the term is a variable, set it to have an exponent of one
                            ArrayList<Term> exp = new ArrayList<>();
                            exp.add(Decimal.ONE);
                            variables.put(c, exp);
                        }
                    }
                }
            } else { // Parse the variables
                if (Character.isLetter(c)) { // When a variable is encountered, seek ahead and parse the exponent. Then, move on to the next variable
                    // System.out.println("PV: " + e + ", " + i);
                    if (atEnd(i, e)) variables.put(c, termToList(Decimal.ONE)); // If the variable is to the first power and has no caret. Ex: 3x
                    for (int k = i + 3, p = 1; k < e.length(); k++) { // Add 3 to k so that it is at the beginning of the exponent. x^(-1) -> i == 0, k == 3
                        char r = e.charAt(k);

                        if (r == '(') p++;
                        else if (r == ')') p--;
                        if (p == 0) {
                            System.out.println("DECIMAL.JAVA | parse() | variable exponent: " + e.substring(i + 3, k));
                            variables.put(c, new Expression(e.substring(i + 3, k)).getTerms()); // When the end of the exponent is found, add it and the variable to the map
                            i = k;
                            j = i + 1;
                            break;
                        }
                    }
                }
            }
        }

        // Apply exponent if possible
        if (exponent.size() == 1) { // If there is only one term as the exponent
            Term exp = exponent.get(0);
            if (isConstant(exp.toString())) { // If the exponent is a constant
                if (exp.ID().equals("decimal")) value = new BigDecimal(Math.pow(value.doubleValue(), ((Decimal) exp).getValue().doubleValue())); // If the exponent is a decimal
                else if (exp.ID().equals("fraction")) value = new BigDecimal(Math.pow(value.doubleValue(), fractionToDouble((Fraction) exp))); // If the exponent is a fraction
            }
        }
    }
    */

    public static boolean isDecimal(String e) {
        for (int i = 0, p = 0; i < e.length(); i++) {
            char c = e.charAt(i);

            if (c == '(') p++;
            else if (c == ')') p--;

            if (c == '/' && p == 0) return false; // If there is a division symbol that is not part of an exponent
        }

        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(value.toString()); // Add value
        if (exponent != null) s.append("^(").append(termsToString(exponent)).append(")"); // Add exponent
        if (variables != null) { // Add variables
            for (char c : variables.keySet()) {
                s.append(c).append("^(").append(termsToString(variables.get(c))).append(")");
            }
        }

        return s.toString();
    }

    public BigDecimal getValue() {
        return value;
    }

    public ArrayList<Term> getExponent() {
        return exponent;
    }

    public HashMap<Character, ArrayList<Term>> getVariables() {
        if (variables == null) return new HashMap<>();
        HashMap<Character, ArrayList<Term>> ret = new HashMap<>();
        Iterator<Character> iter = variables.keySet().iterator();
        char c = ' ';
        if (iter.hasNext()) {
            c = iter.next();
            ret.put(c, variables.get(c));
        }

        return ret;
    }

    public String ID() {
        return "decimal";
    }

}
