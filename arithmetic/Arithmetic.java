package arithmetic;

import expression.Expression;
import term.Decimal;
import term.Fraction;
import term.Term;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Utils.termToList;
import static utils.Utils.termsToString;

public class Arithmetic {

    public static Term add(Term addend, Term augend) {
        // It is assumed that both terms have the same exponents/variables
        boolean aDec = addend.getClass().equals(Decimal.class);
        boolean bDec = augend.getClass().equals(Decimal.class);

        if (aDec && bDec) {
            return new Decimal(((Decimal) addend).getValue().add(((Decimal) augend).getValue()), ((Decimal) addend).getExponent(), ((Decimal) addend).getVariables());
        } else if ((aDec && !bDec) || (!aDec && bDec)) {

        } else if (!aDec && !bDec) {

        }

        return Decimal.ERROR;
    }

    public static Term multiply(Term a, Term b) {
        boolean aFrac = a.ID().equals("fraction");
        boolean bFrac = b.ID().equals("fraction");

        if (aFrac && !bFrac) { // If Term a is a fraction and Term b is a decimal
            // I know downcasting is dangerous, but it works in this case because the Term objects are identified as either fractions or decimals before they are used as such
            // Term cannot be instantiated, so it is impossible for a Term to be passed into this method
            ArrayList<Term> numerator = distribute(((Fraction) a).getNumerator(), termToList(b));
            ArrayList<Term> denominator = ((Fraction) a).getDenominator();

            return new Fraction(numerator, denominator);
        } else if (!aFrac && bFrac) { // If Term a is a decimal and Term b is a fraction
            ArrayList<Term> numerator = distribute(((Fraction) b).getNumerator(), termToList(a));
            ArrayList<Term> denominator = ((Fraction) b).getDenominator();

            return new Fraction(numerator, denominator);
        } else if (!aFrac && !bFrac) { // If both Term a and Term b are decimals
            Decimal m1 = (Decimal) a;
            Decimal m2 = (Decimal) b;

            BigDecimal value = m1.getValue().multiply(m2.getValue()); // Multiply the decimal values

            // Exponent math
            ArrayList<Term> exponent = new ArrayList<>();
            if (m1.getValue().equals(m2.getValue())) exponent = new Expression("(" + termsToString(m1.getExponent()) + ")+(" + termsToString(m2.getExponent()) + ")").getTerms();

            // Multiply variables
            HashMap<Character, ArrayList<Term>> vars = new HashMap<>();
            HashMap<Character, ArrayList<Term>> vars2 = new HashMap<>();
            if (!m1.getVariables().isEmpty() || !m2.getVariables().isEmpty()) { // If one or both terms has variables
                if (!m1.getVariables().isEmpty()) {
                    vars = m1.getVariables();
                    vars2 = m2.getVariables();
                } else {
                    vars = m2.getVariables();
                    vars2 = m1.getVariables();
                }
                System.out.println(vars + " | " + m2.getVariables());
                for (int i = 0; i < vars2.size(); i++) {
                    char c = (Character) vars.keySet().toArray()[i];
                    char d = (Character) vars2.keySet().toArray()[i];
                    if (vars.keySet().contains(d)) vars.put(c, new Expression(termsToString(vars.get(c)) + "+(" + termsToString(vars2.get(c)) + ")").getTerms()); // If both terms have the same variable, add their exponents
                    else if (!vars.keySet().contains(d)) vars.put(d, vars2.get(d));
                }
            }

            return new Decimal(value, exponent, vars);
        } else if (aFrac && bFrac) { // If both Term a and Term b are fractions
            ArrayList<Term> numerator = distribute(((Fraction) a).getNumerator(), ((Fraction) b).getNumerator());
            ArrayList<Term> denominator = distribute(((Fraction) a).getDenominator(), ((Fraction) b).getDenominator());

            return new Fraction(numerator, denominator);
        }

        return Decimal.ERROR;
    }

    public static Term divide(Term dividend, Term divisor) {
        return Decimal.ONE;
    }

    public static Term reciprocate(Term t) {
        return Decimal.ONE;
    }

    public static ArrayList<Term> distribute(ArrayList<Term> distributors, ArrayList<Term> distributand) {
        return termToList(Decimal.ERROR);
    }

    public static Term lcd(Term one, Term two) {

        return Decimal.ERROR;
    }

    public static int compare(Term a, Term b) {
        // TODO
        // Return 1 if a > b
        // Return 0 if a == b
        // Return -1 if a < b
        return 0;
    }

}
