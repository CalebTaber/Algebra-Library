package expression;

import exception.MalformedExpressionException;
import term.Decimal;
import term.Term;

import java.util.*;

import static arithmetic.Arithmetic.*;
import static utils.Utils.*;

public class Expression {

    // An ArrayList of Term objects implies that they came from an addition/subtraction expression
    // Ex: {3, 2xy, -5z, 6/7} would come from the expression 3 + 2xy - 5z + 6/7

    private ArrayList<Term> parsedTerms;

    public Expression(String e) {
        parsedTerms = new ArrayList<>();
        System.out.println("EXPRESSION.JAVA | constructor | Expression: " + e);
        simplify(e);
    }

    /**
     * Takes an input String and returns the simplification of the mathematical expression contained by the String.
     * All expressions entered will first be formatted so that parsing is straightforward and consistent. If an input expression cannot
     * be formatted, a malformedExpressionException will be thrown in Expression.format()
     *
     * @param e = An input expression
     */
    private void simplify(String e) {
        String formatted = null;
        try {
            formatted = format(e);
        } catch (MalformedExpressionException ex) {
            ex.printStackTrace();
        }

        System.out.println("EXPRESSION.JAVA | simplify() | Simplify: " + formatted);

        // First: simplify parentheses (leave exponents in parentheses)
        HashMap<Integer, Integer> parentheses = parseParentheses(formatted);

        for (int k : parentheses.keySet()) {
            System.out.println("EXPRESSION.JAVA | simplify() | Parenthetical expression and indices: " + (k + 1) + ", " + parentheses.get(k) + ", " + formatted.substring(k + 1, parentheses.get(k)));
        }

        // Simplify inside
        StringBuilder s = new StringBuilder(formatted);
        for (int k : parentheses.keySet()) {
            // s.replace(k, parentheses.get(k) + 1, new Expression(s.substring(k + 1, parentheses.get(k))).toString()); // TODO REMOVE WHEN BELOW LINE IS UNCOMMENTED
            s.replace(k + 1, parentheses.get(k), new Expression(s.substring(k + 1, parentheses.get(k))).toString());
        }

        System.out.println("EXPRESSION.JAVA | simplify() | Parentheses simplified: " + s.toString());
        parentheses = parseParentheses(s.toString());
        // Distribution TODO
        // Find left and right multiplicands and multiply them. Then, multiply that by the expression
        while (!parentheses.isEmpty()) {
            int lbound, rbound, start, end;

            start = (int) parentheses.keySet().toArray()[0];
            end = (int) parentheses.values().toArray()[0];

            StringBuilder leftDistSB = new StringBuilder(s.toString().substring(0, start));
            leftDistSB.reverse();
            String leftDist = scanDistributand(leftDistSB.toString(), true);
            lbound = start - leftDist.length();

            String rightDist = scanDistributand(s.toString().substring(end + 1), false);
            rbound = end + rightDist.length() + 1; // Add 1 so that the closing parenthesis is replaced in the following lines

            System.out.println("EXPRESSION.JAVA | simplify() | lDist, rDist: " + leftDist + ", " + rightDist);
            ArrayList<Term> leftTerms = (leftDist.isBlank()) ? termToList(Decimal.ONE) : new Expression(leftDist).getTerms();
            ArrayList<Term> rightTerms = (rightDist.isBlank()) ? termToList(Decimal.ONE) : new Expression(rightDist).getTerms();
            s.replace(lbound, rbound, termsToString(distribute(distribute(leftTerms, rightTerms), parseTerms(s.substring(start + 1, end)))));

            parentheses = parseParentheses(s.toString());
        }

        // Second: parse terms
        System.out.println("EXPRESSION.JAVA | simplify() | Parse Terms: " + s.toString());
        ArrayList<Object> parsed = parseTermsAndSymbols(s.toString());

        // Third: simplify multiplication and division

        // Fourth: simplify addition and subtraction

        // parsed = parseTermsAndSymbols(s.toString()); TODO UNCOMMENT
        System.out.println("EXPRESSION.JAVA | simplify() | Before Addition: " + s.toString());
        for (Object o : parsed) {
            if (!o.getClass().equals(Character.class)) parsedTerms.add((Term) o);
        }

        ArrayList<ArrayList<Term>> addLists = new ArrayList<>();

        // Sort the terms by their variables
        for (Term t : parsedTerms) {
            ArrayList<ArrayList<Term>> tmp = new ArrayList<>();
            if (addLists.isEmpty()) addLists.add(termToList(t));
            else {
                tmp = new ArrayList<>();
                for (ArrayList<Term> l : addLists) {
                    if (l.get(0).getVariables().equals(t.getVariables())) l.add(t);
                    else tmp.add(termToList(t));
                }
            }

            if (!tmp.isEmpty()) addLists.addAll(tmp);
        }

        // Add lists
        for (ArrayList<Term> l : addLists) {
            for (int i = 1; i < l.size(); i++) {
                l.set(0, add(l.get(0), l.get(i)));
            }
        }

        // Add terms to term list
        parsedTerms = new ArrayList<>();
        for (ArrayList<Term> list : addLists) {
            parsedTerms.add(list.get(0));
        }

        System.out.println("EXPRESSION.JAVA | simplify() | AFTER ADDITION: " + termsToString(parsedTerms));
    }

    private String scanDistributand(String expression, boolean left) {
        System.out.println("EXPRESSION.JAVA | scanDistributand() | in: " + expression);
        StringBuilder distributand = new StringBuilder("");

        for (int i = 0, p = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') p++;
            if (c == ')') p--;

            if (i != 0 && p == 0 && c == ')' && !atEnd(i, expression) && expression.charAt(i + 1) == '(')
                continue; // If c == ')' and there's an adjacent parenthetical expression, include it in the distributand

            if (i == 0 && (c == '-' || c == '*')) continue; // Skip over an adjacent '-' or '*'

            if (OPERATORS.contains(String.valueOf(c)) && p == 0) {
                distributand = new StringBuilder(expression.substring(0, i));
                break;
            } else if (atEnd(i, expression)) distributand = new StringBuilder(expression);
        }

        if (left) distributand.reverse(); // If left, reverse distributand, then return new Expression
        return distributand.toString();
    }

    /**
     * Returns a map of matching opening and closing parentheses. The returned map excludes parenthetical expressions that are exponents, because those will be evaluated later
     *
     * @param e = the expression in which to search for matching parentheses
     * @return = a map of the indices of matching parentheses in the expression e. This map excludes parentheses that delineate exponents
     */
    private HashMap<Integer, Integer> parseParentheses(String e) {
        HashMap<Integer, Character> indices = new HashMap<>();

        ArrayList<Integer> open = indicesOf(e, '(');
        ArrayList<Integer> close = indicesOf(e, ')');

        // Add opening parentheses to the map
        for (int i = 0; i < open.size(); i++) {
            indices.put(open.get(i), '(');
            indices.put(close.get(i), ')');
        }

        // Pair matching parentheses
        HashMap<Integer, Integer> parentheses = new HashMap<>();

        System.out.println("EXPRESSION.JAVA | parseParentheses() | indices: " + indices.keySet());
        if (indices.isEmpty()) return parentheses; // If the map is empty, don't sort it; just return the empty map

        // Sort set
        Set<Integer> keys = new HashSet<>();
        Object[] ks = indices.keySet().toArray();
        Arrays.sort(ks);
        for (Object i : ks) {
            keys.add((int) i);
        }
        System.out.println("EXPRESSION.JAVA | parseParentheses() | Sorted set: " + keys);

        while (!keys.isEmpty()) {
            int p = 1;
            for (int i = 1; i < keys.size(); i++) {
                int k = (int) keys.toArray()[i];
                boolean isOpen = indices.get(k) == '(';

                if (isOpen) p++;
                else p--;

                if (p == 0) { // When the closing parenthesis is found
                    parentheses.put((int) keys.toArray()[0], k); // Add the opening and closing indices
                    keys.remove(k); // Remove k first, because removing index 0 will shift the array
                    keys.remove(keys.toArray()[0]);
                    break;
                }
            }
        }

        // Determine whether or not a set is an exponent (If it is an exponent, don't add it to the final list)
        HashMap<Integer, Integer> ret = new HashMap<>();
        for (int k : parentheses.keySet()) {
            if (k < 2) { // If the expression is at the beginning of the expression
                ret.put(k, parentheses.get(k));
                continue;
            }
            if (e.charAt(k - 1) == '^' && (Character.isLetter(e.charAt(k - 2)) || Character.isDigit(e.charAt(k - 2))))
                continue; // If the expression is an exponent
            ret.put(k, parentheses.get(k));
        }

        System.out.println("EXPRESSION.JAVA | parseParentheses() | Return: " + ret.keySet());
        return ret;
    }

    public ArrayList<Term> getTerms() {
        return parsedTerms;
    }

    /**
     * Takes an unformatted expression and attempts to format and return it as a String.
     * First, the method will check the expression for unfixable syntax errors and throw a MalformedExpressionException
     * if one is found.
     * In the second half of the method, the expression will be formatted according to the rules outlined in formatRules.
     * Still, if a syntax error is encountered, a MalformedExpressionException will be thrown.
     * A list of syntax errors can be found in inputRules
     *
     * @param unformattedExpression = an expression that has not been formatted
     * @return a formatted version of the input expression
     * @throws MalformedExpressionException when an unformattable expression is entered
     */
    public static String format(String unformattedExpression) throws MalformedExpressionException {
        String expression = unformattedExpression.replace(" ", ""); // Remove spaces from the expression

        String acceptableChars = "0123456789abcdefghijklmnopqrstuvwxyz.^()+-*/=!";
        String operators = "+-*/^=!.";

        // Check to make sure all characters are valid
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!acceptableChars.contains(String.valueOf(c))) throw new MalformedExpressionException(expression, "Index " + i + ". Invalid character.");
        }

        // If an operator begins the expression (excluding a negation symbol)
        char first = expression.charAt(0);
        if (first == '+' || first == '*' || first == '/' || first == '^' || first == '=' || first == '!')
            throw new MalformedExpressionException(expression, "An expression cannot start with an operator.");

        // If an operator ends the expression
        char last = expression.charAt(expression.length() - 1);
        if (last == '+' || last == '*' || last == '/' || last == '^' || last == '=' || last == '-' || last == '.')
            throw new MalformedExpressionException(expression, "An expression cannot end with an operator.");

        // Check for invalid double operators
        for (int i = 0, startIndex = -1; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (!operators.contains(String.valueOf(c))) startIndex = -1;
            else if (operators.contains(String.valueOf(c))) {
                if (startIndex == -1) startIndex = i;
                else if (i - startIndex == 2) {
                    String combination = expression.substring(startIndex, i + 1);
                    // If it is a valid operator combination, continue with the loop. Otherwise, throw an exception
                    if (combination.equals("+-") || combination.equals("--") || combination.equals("*-")
                            || combination.equals("/-") || combination.equals("^-") || combination.equals("-.")
                            || combination.equals("=-") || combination.equals("!=")) continue;
                    else throw new MalformedExpressionException(expression, "Index " + (i - 2) + ". Invalid operator combination: " + combination);
                } else if ((i - startIndex) > 2) throw new MalformedExpressionException(expression, "Index " + (i - (i - startIndex)) + ". Triple operators are not allowed.");
            }
        }

        // Check parentheses frequencies
        int p = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') p++;
            else if (c == ')') {
                /*
                There cannot be a closing parenthesis before an opening parenthesis, so p must be greater than 0 in order
                for the closing parenthesis to be valid
                */

                if (p > 0) p--;
                else throw new MalformedExpressionException(expression, "Index " + i + ". A closing parenthesis cannot be used before a corresponding opening parenthesis.");
            }
        }

        /*
         If the numbers of opening and closing parentheses are not equal, then p will not be zero.
         These frequencies cannot be uneven in a valid expression, so if p is not 0, an exception will be thrown
         */
        if (p != 0) throw new MalformedExpressionException(expression, "Not all opening parentheses are paired with closing parentheses.");

        // FORMAT THE EXPRESSION
        expression = expression.replace("--", "+");
        expression = expression.replace("+-", "-");
        expression = expression.replace("[", "(");
        expression = expression.replace("]", ")");

        StringBuilder formatted = new StringBuilder(expression);
        for (int i = 1, offset = 0, numStart = -1; i < expression.length(); i++) { // Offset == insertion offset for formatted
            char a = expression.charAt(i - 1);
            char b = expression.charAt(i);
            boolean isOne = false; // Whether or not a number is 1. Used for determining where to insert missing exponents

            // Determine if the number == 1 or not
            if (Character.isDigit(b) && !Character.isDigit(a)) numStart = i; // If b is the start of a number
            else if (i == 1 && Character.isDigit(a)) numStart = 0; // If the number begins the expression
            else if (Character.isDigit(a) && !Character.isDigit(b) && b != '.') { // If the end of the number is encountered
                isOne = Double.parseDouble(expression.substring(numStart, i)) == 1.0;
                numStart = -1;
            }

            // Account for reaching the end of the expression
            if (atEnd(i, expression)) {
                if (Character.isDigit(b)) { // If a number ends the expression
                    if (numStart == i) // If it is a one-digit number
                        isOne = Double.parseDouble(expression.substring(i, i + 1)) == 1.0;
                    else // If the end of a multi-digit number ends the expression
                        isOne = Double.parseDouble(expression.substring(numStart, i + 1)) == 1.0;
                }

                if (!isOne && (Character.isAlphabetic(b) || Character.isDigit(b)) && (i - numStart == 0)) // If a one-digit number ends the expression
                    formatted.insert(i + offset + 1, "^(1)");
                else if (!isOne && (Character.isAlphabetic(a) || Character.isDigit(a) && b ==')')) // If there is a number at the end of a parenthetical that ends the expression Ex: ...+6)
                    formatted.insert(i + offset, "^(1)");
            } else if ((Character.isDigit(a) && !Character.isDigit(b) && b != '.') || Character.isAlphabetic(a)) { // If at the end of a number OR at the char after a variable
                // If there is no explicit exponent, add 1 as the exponent
                if (b == '^') continue;
                else if (!isOne) {
                    formatted.insert(i + offset, "^(1)");
                    offset += 4;
                }
            } else if (a == '^') { // If b is the start of the exponent
                if (Character.isDigit(b) || b == '-'){ // If b is the start of the exponent
                    formatted.insert(i + offset, "("); // Add opening parenthesis of the exponent
                    offset++;

                    // Search forward and find next operator/variable and add exponent to exponent if necessary. Then close parentheses and increment offset and i as needed
                    for (int o = i; o < expression.length(); o++) {
                        char d = expression.charAt(o);

                        if (atEnd(o, expression) && Character.isDigit(d)) { // If the exponent ends the expression
                            if (Double.parseDouble(expression.substring(i, o + 1)) != 1.0)
                                formatted.insert(o + offset + 1, "^(1))"); // If the exponent is not 1, add the exponent and the closing parenthesis
                            else formatted.insert(o + offset + 1, ")"); // If the exponent is 1, just add the closing parenthesis
                            i = o; // Set i equal to o so that the outermost loop terminates since we have hit the end of the expression
                            break;
                        } else { // If the exponent does not end the expression
                            if ((operators.contains(String.valueOf(d)) && d != '.') || Character.isAlphabetic(d)) { // If an operator or a variable is encountered
                                if (Double.parseDouble(expression.substring(i, o)) != 1.0) { // If the exponent is not 1, add the exponent and the closing parenthesis
                                    formatted.insert(o + offset, "^(1))");
                                    i = o; // Set i equal to o so that on the next iteration, a will be the first char after the exponent
                                    offset += 5 + (o - i); // Update offset so insertions stay consistent
                                } else {
                                    formatted.insert(o + offset + 1, ")"); // If the exponent is 1, just add the closing parenthesis
                                    i = o; // Set i equal to o so that on the next iteration, a will be the first char after the exponent
                                    o += 1 + (o - i); // Update offset so insertions stay consistent
                                }

                                break;
                            }
                        }
                    }
                    continue;
                } else if (Character.isAlphabetic(b))
                    throw new MalformedExpressionException(formatted.toString(), "Error determining exponent at index " + (i + offset) + ". No exponent given.");
            } else if (a == '(') { // If at the beginning of a parenthetical exponent
                if (b != '-' && b != '(' && b != '.' && !Character.isDigit(b) && !Character.isAlphabetic(b))
                    throw new MalformedExpressionException(formatted.toString(), "Index " + (i + offset) + ". Invalid exponent.");
            }
        }

        return formatted.toString();
    }

    public String toString() {
        return termsToString(parsedTerms);
    }

}
