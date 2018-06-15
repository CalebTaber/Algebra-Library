package arithmetic;

import term.Decimal;
import term.Fraction;
import term.Term;

import java.math.BigDecimal;
import java.util.ArrayList;

import static utils.Utils.termToList;

public class Arithmetic {

    public static Term add(Term a, Term b) {
        return Decimal.ONE;
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
        } else if (aFrac && bFrac) { // If both Term a and Term b are decimals
            BigDecimal value = ((Decimal) a).getValue().multiply(((Decimal) b).getValue()); // Multiply the decimal values

            // Exponent math


            // Multiply variables

        } else if (!aFrac && !bFrac) { // If both Term a and Term b are fractions
            ArrayList<Term> numerator = distribute(((Fraction) a).getNumerator(), ((Fraction) b).getNumerator());
            ArrayList<Term> denominator = distribute(((Fraction) a).getDenominator(), ((Fraction) b).getDenominator());

            return new Fraction(numerator, denominator);
        }

        return Decimal.ONE;
    }

    public static Term divide(Term dividend, Term divisor) {
        return Decimal.ONE;
    }

    public static Term reciprocate(Term t) {
        return Decimal.ONE;
    }

    public static ArrayList<Term> distribute(ArrayList<Term> distributors, ArrayList<Term> distributand) {
        return new ArrayList<>();
    }

    public static int compare(Term a, Term b) {
        // TODO
        // Return 1 if a > b
        // Return 0 if a == b
        // Return -1 if a < b
        return 0;
    }

}
