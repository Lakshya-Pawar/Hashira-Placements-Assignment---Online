import java.math.BigInteger;
import java.util.ArrayList;

class Fraction {
    BigInteger n, d;

    public Fraction(BigInteger num, BigInteger den) {
        if (den.compareTo(BigInteger.ZERO) < 0) {
            num = num.negate();
            den = den.negate();
        }
        BigInteger g = gcd(num.abs(), den.abs());
        n = num.divide(g);
        d = den.divide(g);
    }

    public Fraction add(Fraction b) {
        return new Fraction(n.multiply(b.d).add(b.n.multiply(d)), d.multiply(b.d));
    }

    public Fraction mul(Fraction b) {
        return new Fraction(n.multiply(b.n), d.multiply(b.d));
    }

    public Fraction div(Fraction b) {
        return new Fraction(n.multiply(b.d), d.multiply(b.n));
    }

    public boolean isInt() {
        return d.equals(BigInteger.ONE);
    }

    public String toString() {
        return n.toString();
    }

    private BigInteger gcd(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger t = b;
            b = a.mod(b);
            a = t;
        }
        return a;
    }
}

class Interp {
    public static void main(String[] args) {
        // Select test case (default: 1)
        int testCase = (args.length > 0) ? Integer.parseInt(args[0]) : 1;
        ArrayList<long[]> points = new ArrayList<>();
        int k;

        if (testCase == 1) {
            // Test Case 1: k=3, points (1,4), (2,7), (3,12)
            k = 3;
            points.add(new long[]{1, 4});
            points.add(new long[]{2, 7});
            points.add(new long[]{3, 12});
        } else {
            // Test Case 2: k=7, constant polynomial
            k = 7;
            points.add(new long[]{1, 57357376348801L});
            points.add(new long[]{2, 57357376348801L});
            points.add(new long[]{3, 57357376348801L});
            points.add(new long[]{4, 57357376348801L});
            points.add(new long[]{5, 57357376348801L});
            points.add(new long[]{6, 57357376348801L});
            points.add(new long[]{7, 57357376348801L});
        }

        int m = k - 1; // Degree of polynomial

        // Polynomial coefficients (low to high degree)
        ArrayList<Fraction> coeffs = new ArrayList<>();
        for (int i = 0; i <= m; i++) {
            coeffs.add(new Fraction(BigInteger.ZERO, BigInteger.ONE));
        }

        for (int i = 0; i < points.size(); i++) {
            long xi = points.get(i)[0];
            long yi = points.get(i)[1];
            ArrayList<Fraction> numer = new ArrayList<>();
            numer.add(new Fraction(BigInteger.ONE, BigInteger.ONE));
            Fraction denom = new Fraction(BigInteger.ONE, BigInteger.ONE);

            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                long xj = points.get(j)[0];
                ArrayList<Fraction> term = new ArrayList<>();
                term.add(new Fraction(BigInteger.valueOf(-xj), BigInteger.ONE));
                term.add(new Fraction(BigInteger.ONE, BigInteger.ONE));
                ArrayList<Fraction> tmp = new ArrayList<>();
                for (int p = 0; p < numer.size() + term.size() - 1; p++) {
                    tmp.add(new Fraction(BigInteger.ZERO, BigInteger.ONE));
                }
                for (int p = 0; p < numer.size(); p++) {
                    for (int q = 0; q < term.size(); q++) {
                        tmp.set(p + q, tmp.get(p + q).add(numer.get(p).mul(term.get(q))));
                    }
                }
                numer = tmp;
                denom = denom.mul(new Fraction(BigInteger.valueOf(xi - xj), BigInteger.ONE));
            }

            Fraction factor = new Fraction(BigInteger.valueOf(yi), BigInteger.ONE).div(denom);
            for (int t = 0; t < numer.size() && t < coeffs.size(); t++) {
                coeffs.set(t, coeffs.get(t).add(numer.get(t).mul(factor)));
            }
        }

        // ✅ Only return the constant term
        Fraction constant = coeffs.get(0);
        if (!constant.isInt()) {
            System.err.println("Non-integer constant term");
            System.exit(1);
        }
        System.out.println(constant.toString());
    }
}

public class Main {
    public static void main(String[] args) {
        Interp.main(args); // Delegate to Interp's main method
    }
}
