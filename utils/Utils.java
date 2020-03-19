package utils;

import exception.EmptyInputException;
import exception.MalformedExpressionException;
import expression.Expression;
import term.Decimal;
import term.Fraction;
import term.Term;

import java.util.ArrayList;

import static term.Term.toTerm;

public class Utils {

    /** Returns true if i is the final index in the given string **/
    public static boolean atEnd(int i, String s) {
        return i == s.length() - 1;
    }

    /** Returns the string equivalent of the given Term list **/
    public static String termsToString(ArrayList<Term> terms) {
        StringBuilder s = new StringBuilder();

        for (Term t : terms) {
            if (terms.indexOf(t) == 0) s.append(t.toString());
            else if (t.toString().startsWith("-")) s.append(t.toString());
            else s.append("+").append(t.toString());
        }

        return s.toString();
    }

    /**
     * Parses the first parenthetical from the given expression.
     * Note: the expression must begin with an opening parenthesis
     * @param expression the expression from which to parse a parenthetical
     * @return the parsed parenthetical as a String
     */
    public static String parseParenthetical(String expression) {
        for (int i = 1, p = 1; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') p++;
            else if (c == ')') {
                p--;
                if (p == 0) // When p == 0, that signals the end of the parenthetical since the number of opening and the number of closing parentheses are equal
                    return expression.substring(1, i);
            }
        }

        return null;
    }

    /** Returns a list containing the given term **/
    public static ArrayList<Term> termToList(Term t) {
        ArrayList<Term> list = new ArrayList<>();
        list.add(t);
        return list;
    }

    /**
     * Parses the Terms not inside parentheticals from the given expression
     * @param expression = The expression from which to parse the Terms
     * @return An ArrayList<Term> of Terms in the expression
     * @throws Exception if a parsed Term is not a valid Decimal or Fraction
     * @throws EmptyInputException if the given expression String is empty
     */
    public static ArrayList<Term> parseTerms(String expression) throws Exception {
        if (expression.isEmpty()) throw new EmptyInputException();
        ArrayList<Term> parsed = new ArrayList<>();

        for (int i = 0, j = 0, p = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') p++;
            else if (c == ')') p--;

            if (p == 0 && j != i) { // Make sure that the terms in exponents are not parsed separately from their numerical/variable parts

                if (atEnd(i, expression)) { // If the end of the String has been reached
                    String t = expression.substring(j, i + 1); // Parse the string
                    if (Decimal.isDecimal(t)) parsed.add(new Decimal(t)); // Determine if Decimal
                    else if (Fraction.isFraction(t)) parsed.add(new Fraction(t)); // Determine if Fraction
                    else throw new Exception("This term does not have a type"); // If it doesn't fit in either category (it should), throw an Exception
                }

                if (c == '*' || c == '/' || c == '+') { // When an operator is encountered
                    String t = expression.substring(j, i); // Parse the string
                    if (Decimal.isDecimal(t)) parsed.add(new Decimal(t)); // Determine if Decimal
                    else if (Fraction.isFraction(t)) parsed.add(new Fraction(t)); // Determine if Fraction
                    else throw new Exception("This term does not have a type"); // If it doesn't fit in either category (it should), throw an Exception

                    j = i + 1; // Set j one char ahead of i so that it does not parse the operator with the next term
                    i++; // Increment i so that it is one character ahead of j on the next iteration
                }
            }
        }

        return parsed;
    }

    /** Returns a list of indices of the given character in the given string **/
    public static ArrayList<Integer> indicesOf(String e, char c) {
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < e.length(); i++) {
            if(e.charAt(i) == c) indices.add(i);
        }

        return indices;
    }

    /**
     * Parses the operators not inside parentheticals in the given expression
     * @param expression = The expression from which to parse the operators
     * @return An ArrayList<Character> of operators in the given expression
     * @throws EmptyInputException If the input String is empty since the input should never be an empty String
     */
    public static ArrayList<Character> parseOperators(String expression) throws EmptyInputException {
        if (expression.isEmpty()) throw new EmptyInputException();
        ArrayList<Character> parsed = new ArrayList<>();

        int p = 0;
        for (char c : expression.toCharArray()) {

            if (c == '(') p++;
            else if (c == ')') p--;

            if (p == 0 && (c == '*' || c == '/' || c == '+'))
                parsed.add(c); // If an operator is encountered outside of any parentheticals
        }

        return parsed;
    }

    /** Returns true if the given string is an integer constant. Returns false otherwise **/
    public static boolean isConstant(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c) && c != '/') return false;
        }

        return true;
    }

    /** Compares two arraylists of terms. Returns true if they contain the same terms. Returns false otherwise */
    public static boolean termsAreEqual(ArrayList<Term> one, ArrayList<Term> two) {
        if (one.size() != two.size()) return false;
        for (Term t : one) {
            if (!two.contains(t)) return false;
        }

        return true;
    }

    /** Returns the double value of a constant fraction.
     *  The given fraction may not contain variables **/
    public static double fractionToDouble(Fraction f) {
        ArrayList<Double> values = new ArrayList<>();
        Term n = f.getNumerator().get(0);
        if (n.ID().equals("decimal")) values.add(((Decimal) n).getValue().doubleValue()); // If the numerator is a decimal, add it to the list
        else if (n.ID().equals("fraction")) values.add(fractionToDouble((Fraction) n)); // If the numerator is a fraction, add its doubleValue to the list

        Term d = f.getDenominator().get(0);
        if (d.ID().equals("decimal")) values.add(((Decimal) d).getValue().doubleValue());
        else if (d.ID().equals("fraction")) values.add(fractionToDouble((Fraction) d));

        double total = values.get(0);
        for (double v : values.subList(1, values.size())) {
            total /= v;
        }

        return total;
    }

    public static ArrayList<Term> subList(ArrayList<Term> list, int start, int end) {
        ArrayList<Term> ret = new ArrayList<>();
        for (int i = start; i < end; i++) {
            ret.add(list.get(i));
        }

        return ret;
    }
}
