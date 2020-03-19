package term;

import expression.Expression;

import java.util.ArrayList;

import static utils.Utils.*;

public class Fraction extends Term {

    private ArrayList<Term> numerator, denominator;

    public Fraction(String e) {
        parse(e);
    }

    public Fraction(ArrayList<Term> numerator, ArrayList<Term> denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    private void parse(String e) {
        // Find middle division symbol
        ArrayList<Integer> div = indicesOf(e, '/');
        int d = div.size() / 2;
        // Parse numerator and denominator, and simplify them
        String num = termsToString(new Expression(e.substring(0, div.get(d))).getTerms());
        String denom = termsToString(new Expression(e.substring(div.get(d) + 1)).getTerms());

        // Check if compound fraction   (3x+2)/7 OR 3x/(7-y)
        if (div.size() == 1 && (e.contains("+") || (e.substring(1).contains("-")))) { // If there is one division symbol and there is addition or subtraction
            try {
                numerator = parseTerms(num);
                denominator = parseTerms(denom);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else { // If it is a complex fraction OR a simple fraction
            // Set numerator and denominator
            this.numerator = (isFraction(num)) ? termToList(new Fraction(num)) : termToList(new Decimal(num));
            this.denominator = (isFraction(denom)) ? termToList(new Fraction(denom)) : termToList(new Decimal(denom));
        }
        // Eventually, all parts of any fraction will break down into decimals, so no additional parsing is needed
    }

    public ArrayList<Term> getNumerator() {
        return numerator;
    }

    public ArrayList<Term> getDenominator() {
        return denominator;
    }

    public static boolean isFraction(String e) {
        if(!e.contains("/")) return false;

        for (int i = 0, p = 0, j = 0; i < e.length(); i++) {
            char c = e.charAt(i);

            if (c == '(') p++;
            else if (c == ')') p--;

            if (c == '/' && p == 0) j++;

            if (atEnd(i, e) && j == 0) return false; // If there are only division symbols that are parts of exponents
        }

        return true;
    }

    public String toString() {
        return termsToString(numerator) + "/" + termsToString(denominator);
    }

    public String ID() {
        return "fraction";
    }
}
