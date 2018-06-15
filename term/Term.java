package term;

public abstract class Term {

    public String ID() {
        return "";
    }

    public static Term toTerm(Object o) {
        System.out.println("TO TERM: " + o);

        if (Decimal.isDecimal(o.toString())) return new Decimal(o.toString());
        else if (Fraction.isFraction(o.toString())) return new Fraction(o.toString());

        return Decimal.ERROR;
    }

}
