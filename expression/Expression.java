package expression;

import term.Decimal;
import term.Term;

import java.util.*;

import static arithmetic.Arithmetic.*;
import static utils.Utils.*;

public class Expression {

    // An ArrayList of Term objects implies that they came from an addition/subtraction expression
    // Ex: {3, 2xy, -5z, 6/7} would come from the expression 3 + 2xy - 5z + 6/7

    private ArrayList<Term> terms;

    public Expression(String e) {
        terms = new ArrayList<>();
        System.out.println("Expression: " + e);
        simplify(e);
    }

    private void simplify(String e) {
        System.out.println("Simplify: " + e);

        // First: simplify parentheses (leave exponents in parentheses)
        HashMap<Integer, Integer> parentheses = parseParentheses(e);

        for (int k : parentheses.keySet()) {
            System.out.println((k + 1) + ", " + parentheses.get(k) + ", " + e.substring(k + 1, parentheses.get(k)));
        }

        // Simplify inside
        StringBuilder s = new StringBuilder(e);
        for (int k : parentheses.keySet()) {
            s.replace(k, parentheses.get(k) + 1, new Expression(s.substring(k + 1, parentheses.get(k))).toString()); // TODO REMOVE WHEN BELOW LINE IS UNCOMMENTED
            // s.replace(k + 1, parentheses.get(k), new Expression(s.substring(k + 1, parentheses.get(k))).toString()); TODO UNCOMMENT
        }

        System.out.println("Parentheses simplified: " + s.toString());
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

            String rightDist = scanDistributand(s.toString().substring(end), false);
            rbound = end + rightDist.length();

            s.replace(lbound, rbound, termsToString(distribute(distribute(new Expression(leftDist).getTerms(), new Expression(rightDist).getTerms()), parseTerms(s.substring(start, end)))));

            parentheses = parseParentheses(s.toString());
        }

        // Second: parse terms
        System.out.println("Parse Terms: " + s.toString());
        ArrayList<Object> parsed = parseTermsAndSymbols(s.toString());

        // Third: simplify multiplication and division

        // Fourth: simplify addition and subtraction

        // parsed = parseTermsAndSymbols(s.toString()); TODO UNCOMMENT
        System.out.println("Before Addition: " + s.toString());
        for (Object o : parsed) {
            if (!o.getClass().equals(Character.class)) terms.add((Term) o);
        }

        ArrayList<ArrayList<Term>> addLists = new ArrayList<>();

        // Sort the terms by their variables
        for (Term t : terms) {
            if (addLists.isEmpty()) {
                ArrayList<Term> newList = new ArrayList<>();
                newList.add(t);
                addLists.add(newList);
            }

            ArrayList<ArrayList<Term>> tmp = new ArrayList<>();
            for (ArrayList<Term> l : addLists) {
                if (l.get(0).getVariables().equals(t.getVariables())) l.add(t);
                else {
                    ArrayList<Term> newList = new ArrayList<>();
                    newList.add(t);
                    tmp.add(newList);
                }
            }

            addLists.addAll(tmp);
        }
        System.out.println("ADD 1");

        // Add lists
        for (ArrayList<Term> l : addLists) {
            System.out.println(termsToString(l));
            for (int i = 1; i < l.size(); i++) {
                l.set(0, add(l.get(0), l.get(i)));
            }
        }

        System.out.println("ADD 2");

        // Add terms to term list
        terms = new ArrayList<>();
        for (ArrayList<Term> list : addLists) {
            terms.add(list.get(0));
        }

        System.out.println("AFTER ADDITION: " + termsToString(terms));
    }

    private String scanDistributand(String expression, boolean left) {
        StringBuilder distributand = new StringBuilder("");

        for (int i = 0, p = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') p++;
            if (c == ')') p--;

            if (i != 0 && p == 0 && c == ')' && !atEnd(i, expression) && expression.charAt(i + 1) == '(') continue; // If c == ')' and there's an adjacent parenthetical expression, include it in the distributand

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

        System.out.println("INDICES: " + indices.keySet());
        if (indices.isEmpty()) return parentheses; // If the map is empty, don't sort it; just return the empty map

        // Sort set
        Set<Integer> keys = new LinkedHashSet<>();
        Object[] ks = indices.keySet().toArray();
        Arrays.sort(ks);
        for (Object i : ks) {
            keys.add((int) i);
        }
        System.out.println("Sorted set: " + keys);

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
            if (e.charAt(k - 1) == '^' && (Character.isLetter(e.charAt(k - 2)) || Character.isDigit(e.charAt(k - 2)))) continue; // If the expression is an exponent
            ret.put(k, parentheses.get(k));
        }

        return ret;
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public String toString() {
        return termsToString(terms);
    }

}
