#include <iostream>
#include <vector>

// Simplified Fraction class using long long
struct Fr {
    long long n, d;
    Fr(long long num = 0, long long den = 1) {
        if (den < 0) { num = -num; den = -den; }
        long long g = gcd(std::abs(num), std::abs(den));
        n = num / g;
        d = den / g;
    }
    Fr add(const Fr& b) const { return Fr(n * b.d + b.n * d, d * b.d); }
    Fr mul(const Fr& b) const { return Fr(n * b.n, d * b.d); }
    Fr div(const Fr& b) const { return Fr(n * b.d, d * b.n); }
    bool isInt() const { return d == 1; }
private:
    long long gcd(long long a, long long b) {
        while (b) {
            long long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
};

int main() {
    // Hardcoded points for Test Case 2: k=7
    long long y = 57357376348801LL;
    std::vector<std::pair<long long, long long>> pts = {
        {1, y}, {2, y}, {3, y}, {4, y}, {5, y}, {6, y}, {7, y}
    };
    int k = 7;
    int m = k - 1; // Degree of polynomial

    // Polynomial coefficients (low to high degree)
    std::vector<Fr> coeffs(m + 1, Fr(0, 1));
    for (size_t i = 0; i < pts.size(); ++i) {
        long long xi = pts[i].first;
        long long yi = pts[i].second;
        std::vector<Fr> numer = { Fr(1, 1) };
        Fr denom(1, 1);

        for (size_t j = 0; j < pts.size(); ++j) {
            if (i == j) continue;
            long long xj = pts[j].first;
            std::vector<Fr> term = { Fr(-xj, 1), Fr(1, 1) };
            std::vector<Fr> tmp(numer.size() + term.size() - 1, Fr(0, 1));
            for (size_t p = 0; p < numer.size(); ++p) {
                for (size_t q = 0; q < term.size(); ++q) {
                    tmp[p + q] = tmp[p + q].add(numer[p].mul(term[q]));
                }
            }
            numer = tmp;
            denom = denom.mul(Fr(xi - xj, 1));
        }

        Fr factor = Fr(yi, 1).div(denom);
        for (size_t t = 0; t < numer.size() && t < coeffs.size(); ++t) {
            coeffs[t] = coeffs[t].add(numer[t].mul(factor));
        }
    }

    // Get constant term (c_0)
    Fr c = coeffs[0]; // c_0 is at index 0 (low to high degree)
    if (!c.isInt()) {
        std::cerr << "Non-integer coefficient\n";
        return 1;
    }
    std::cout << c.n << std::endl;
    return 0;
}
