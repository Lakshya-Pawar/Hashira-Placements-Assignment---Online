import java.math.BigInteger;
import java.util.*;

class Fraction {
    BigInteger n, d;

    public Fraction(BigInteger num, BigInteger den) {
        if (den.signum() < 0) {
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

    public Fraction sub(Fraction b) {
        return new Fraction(n.multiply(b.d).subtract(b.n.multiply(d)), d.multiply(b.d));
    }

    public Fraction mul(Fraction b) {
        return new Fraction(n.multiply(b.n), d.multiply(b.d));
    }

    public Fraction div(Fraction b) {
        return new Fraction(n.multiply(b.d), d.multiply(b.n));
    }

    private static BigInteger gcd(BigInteger a, BigInteger b) {
        return b.equals(BigInteger.ZERO) ? a : gcd(b, a.mod(b));
    }
}

public class Secret {
    static BigInteger parseBig(String baseStr, String valStr) {
        BigInteger b = new BigInteger(baseStr);
        BigInteger r = BigInteger.ZERO;
        for (char ch : valStr.toCharArray()) {
            int v;
            if (ch >= '0' && ch <= '9') v = ch - '0';
            else v = Character.toLowerCase(ch) - 'a' + 10;
            r = r.multiply(b).add(BigInteger.valueOf(v));
        }
        return r;
    }

    public static void main(String[] args) {
        // JSON-like data
        Map<String, Map<String, String>> data = new HashMap<>();
        data.put("1", Map.of("base", "6", "value", "13444211440455345511"));
        data.put("2", Map.of("base", "15", "value", "aed7015a346d635"));
        data.put("3", Map.of("base", "15", "value", "6aeeb69631c227c"));
        data.put("4", Map.of("base", "16", "value", "e1b5e05623d881f"));
        data.put("5", Map.of("base", "8", "value", "316034514573652620673"));
        data.put("6", Map.of("base", "3", "value", "2122212201122002221120200210011020220200"));
        data.put("7", Map.of("base", "3", "value", "20120221122211000100210021102001201112121"));
        data.put("8", Map.of("base", "6", "value", "20220554335330240002224253"));
        data.put("9", Map.of("base", "12", "value", "45153788322a1255483"));
        data.put("10", Map.of("base", "7", "value", "1101613130313526312514143"));

        int k = 7; // from "keys.k"

        // Collect points
        List<BigInteger[]> pts = new ArrayList<>();
        for (String key : data.keySet()) {
            BigInteger x = new BigInteger(key);
            BigInteger y = parseBig(data.get(key).get("base"), data.get(key).get("value"));
            pts.add(new BigInteger[]{x, y});
        }

        // Sort by x to ensure consistent order, then take first k
        pts.sort(Comparator.comparing(a -> a[0]));
        pts = pts.subList(0, k);

        // Interpolation coefficients
        Fraction[] coeffs = new Fraction[k];
        for (int i = 0; i < k; i++) coeffs[i] = new Fraction(BigInteger.ZERO, BigInteger.ONE);

        for (int i = 0; i < k; i++) {
            BigInteger xi = pts.get(i)[0];
            BigInteger yi = pts.get(i)[1];

            List<Fraction> numer = new ArrayList<>();
            numer.add(new Fraction(BigInteger.ONE, BigInteger.ONE));
            Fraction denom = new Fraction(BigInteger.ONE, BigInteger.ONE);

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xj = pts.get(j)[0];

                // term = (x - xj)
                List<Fraction> term = Arrays.asList(
                        new Fraction(xj.negate(), BigInteger.ONE),
                        new Fraction(BigInteger.ONE, BigInteger.ONE)
                );

                // multiply numer * term
                List<Fraction> tmp = new ArrayList<>(Collections.nCopies(numer.size() + term.size() - 1,
                        new Fraction(BigInteger.ZERO, BigInteger.ONE)));

                for (int p = 0; p < numer.size(); p++) {
                    for (int q = 0; q < term.size(); q++) {
                        tmp.set(p + q, tmp.get(p + q).add(numer.get(p).mul(term.get(q))));
                    }
                }

                numer = tmp;
                denom = denom.mul(new Fraction(xi.subtract(xj), BigInteger.ONE));
            }

            Fraction factor = new Fraction(yi, BigInteger.ONE).div(denom);

            for (int t = 0; t < numer.size(); t++) {
                coeffs[t] = coeffs[t].add(numer.get(t).mul(factor));
            }
        }

        // Convert to integers
        List<BigInteger> integers = new ArrayList<>();
        for (Fraction c : coeffs) {
            if (!c.d.equals(BigInteger.ONE)) {
                throw new RuntimeException("Non-integer coefficient!");
            }
            integers.add(c.n);
        }
        Collections.reverse(integers);

        System.out.println("Polynomial coeffs (highest→lowest): " + integers);
        System.out.println("Secret (constant term): " + integers.get(integers.size() - 1));
    }
}
