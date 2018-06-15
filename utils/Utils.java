package utils;

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
            if (t.toString().startsWith("-")) s.append(t.toString());
            else s.append("+").append(t.toString());
        }

        return s.toString();
    }

    /** Returns a list containing the given term **/
    public static ArrayList<Term> termToList(Term t) {
        ArrayList<Term> list = new ArrayList<>();
        list.add(t);
        return list;
    }

    /** Returns list of terms in the string without the arithmetic symbols **/
    public static ArrayList<Term> parseTerms(String e) {
        ArrayList<Object> list = parseTermsAndSymbols(e);
        ArrayList<Term> ret = new ArrayList<>();

        for (Object o : list) {
            if (!o.getClass().equals(Character.class)) ret.add(toTerm(o));
        }

        return ret;
    }

    /** Returns a list of indices of the given character in the given string **/
    public static ArrayList<Integer> indicesOf(String e, char c) {
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < e.length(); i++) {
            if(e.charAt(i) == c) indices.add(i);
        }

        return indices;
    }

    /** Returns a list of terms and arithmetic symbols in the given string **/
    public static ArrayList<Object> parseTermsAndSymbols(String e) {
        ArrayList<Object> parsed = new ArrayList<>();

        for (int i = 0, j = 0, p = 0; i < e.length(); i++) {
            char c = e.charAt(i);

            if (c == '(') p++;
            else if (c == ')') p--;

            if (p == 0) { // Make sure that the exponent of a term is not parsed separately
                if (atEnd(i, e)) parsed.add(e.substring(j, e.length())); // If the end of the string is reached, add the final term to the list

                // Parse the terms when an arithmetic operator is encountered
                switch (c) {
                    case '+':
                        parsed.add(e.substring(j, i));
                        parsed.add(c);
                        j = i + 1;
                        break;
                    case '-':
                        if (i == 0) break; // If the subtraction symbol begins the expression (-5), it will try to parse the '-' as a term, so break to avoid that

                        if (e.charAt(i + 1) == '-') { // If there is an instance such as 12--3x, add a plus sign and move past the subtraction symbols
                            parsed.add(e.substring(j, i));
                            parsed.add('+');
                            i++; // Increment by one, because the loop will add one to i so that it can move past the double negative (5--2)
                            j = i;
                        } else { // If there is only a single subtraction symbol
                            parsed.add(e.substring(j, i));
                            parsed.add('+');
                            j = i; // Do not move j past i, because the subtraction symbol will be included in the next term
                        }
                        break;
                    case '*':
                        parsed.add(e.substring(j, i));
                        parsed.add(c);
                        j = i + 1;
                        break;
                }
            }
        }

        System.out.print("After Parsing: ");
        for (Object o : parsed) {
            System.out.print(o + " | ");
        }
        System.out.println();

        // Determine whether or not a term is a decimal or fraction
        for (Object o : parsed) {
            if (o.getClass() == String.class) parsed.set(parsed.indexOf(o), toTerm(o));
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
}
