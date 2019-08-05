package term;

import expression.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Utils.*;

public class Decimal extends Term {

    public static final Decimal ERROR = new Decimal(String.valueOf(Integer.MAX_VALUE)); // Used as a return value when no other return cases are met
    public static final Decimal NEG_ONE = new Decimal("-1");
    public static final Decimal ZERO = new Decimal("0");
    public static final Decimal ONE = new Decimal("1");

    private BigDecimal value;
    private ArrayList<Term> exponent;
    private HashMap<Character, ArrayList<Term>> variables;

    public Decimal(BigDecimal val, ArrayList<Term> exp, HashMap<Character, ArrayList<Term>> vars) {
        value = val;
        exponent = exp;
        variables = vars;
    }

    public Decimal(String e) {
        parse(e);
    }

    private void parse(String e) {
        System.out.println("Decimal PARSE INPUT: " + e);
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
                                System.out.println("EXPONENT: " + e.substring(i + 3, k));
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
                            System.out.println("VAR EXPONENT: " + e.substring(i + 3, k));
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
        if (!exponent.isEmpty()) s.append("^(").append(termsToString(exponent)).append(")"); // Add exponent
        if (!variables.isEmpty()) { // Add variables
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
        return variables;
    }

    public String ID() {
        return "decimal";
    }

}
