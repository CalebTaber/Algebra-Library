package expression;

import term.Decimal;
import term.Term;

import java.util.*;

import static arithmetic.Arithmetic.add;
import static arithmetic.Arithmetic.multiply;
import static utils.Utils.indicesOf;
import static utils.Utils.parseTermsAndSymbols;
import static utils.Utils.termsToString;

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
            s.replace(k + 1, parentheses.get(k), new Expression(s.substring(k, parentheses.get(k))).toString());
        }

        System.out.println("Parentheses simplified: " + s.toString());
        // Distribution TODO


        // Second: parse terms
        System.out.println("Parse Terms: " + e);
        ArrayList<Object> parsed = parseTermsAndSymbols(e);

        // Third: simplify multiplication and division

        // Fourth: simplify addition and subtraction

        // parsed = parseTermsAndSymbols(s.toString()); TODO UNCOMMENT
        for (Object o : parsed) {
            if (!o.getClass().equals(Character.class)) terms.add((Term) o);
        }
        for (int i = 1; i < terms.size(); i++) {
            terms.set(0, add(terms.get(0), terms.get(i)));
        }
        terms.removeAll(terms.subList(1, terms.size()));

        System.out.println("OUT: " + termsToString(terms));
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
        if (parentheses.isEmpty()) return parentheses; // If the map is empty, don't sort it; just return the empty map

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
