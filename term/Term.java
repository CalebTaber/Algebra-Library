package term;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Term {

    public String ID() {
        return "";
    }

    public static Term toTerm(Object o) {
        System.out.println("TERM.JAVA | toTerm() | input: " + o);

        if (Decimal.isDecimal(o.toString())) return new Decimal(o.toString());
        else if (Fraction.isFraction(o.toString())) return new Fraction(o.toString());

        return Decimal.ZERO;
    }

    public HashMap<Character, ArrayList<Term>> getVariables() {
        return new HashMap<>();
    }

}
