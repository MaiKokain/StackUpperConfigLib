package yuuria.stackupper.configlibrary.ast;

import java.util.Locale;

public enum AssignOperator {
    ADD {
        public Long apply(Number a, Number b) {
            return a.longValue() + b.longValue();
        }
    },
    SUB {
        public Long apply(Number a, Number b) {
            if (b.longValue() > a.longValue()) return b.longValue() - a.longValue();
            return a.longValue() - b.longValue();
        }
    },
    MULTI {
        public Long apply(Number a, Number b) {
            return a.longValue() * b.longValue();
        }
    },
    DIV {
        public Long apply(Number a, Number b) {
            if (a.longValue() == 0 || b.longValue() == 0) throw new ArithmeticException("Cannot divide by zero");
            return a.longValue() / b.longValue();
        }
    },
    POW {
        public Long apply(Number a, Number b) {
            return (long) Math.pow(b.doubleValue(), a.doubleValue());
        }
    },
    EQUAL;

    public Long apply(Number a, Number b) { return this.apply(a, b); }
    public Long apply(Number a) { return a.longValue(); }

    public static AssignOperator from(String text)
    {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "OP_EQ", "->" -> EQUAL;
            case "OP_MULTI_EQ", "*=" -> MULTI;
            case "OP_PLUS_EQ", "+=" -> ADD;
            case "OP_MINUS_EQ", "-=" -> SUB;
            case "OP_DIV_EQ", "/=" -> DIV;
            case "OP_POW_EQ", "^=" -> POW;
            default -> throw new IllegalArgumentException("Unknown assign operator: " + text);
        };
    }
}
