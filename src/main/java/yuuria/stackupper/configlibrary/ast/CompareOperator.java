package yuuria.stackupper.configlibrary.ast;

import java.util.Locale;

public enum CompareOperator {
    EQUAL {
        public boolean test(Number a, Number b) { return a.longValue() == b.longValue(); }
    },
    NOT_EQUAL {
        public boolean test(Number a, Number b) { return a.longValue() != b.longValue(); }
    },
    GREATER {
        public boolean test(Number a, Number b) { return a.longValue() > b.longValue(); }
    },
    GREATER_EQUAL {
        public boolean test(Number a, Number b) { return a.longValue() >= b.longValue(); }
    },
    LESSER {
        public boolean test(Number a, Number b) { return a.longValue() < b.longValue(); }
    },
    LESSER_EQUAL {
        public boolean test(Number a, Number b) { return a.longValue() <= b.longValue(); }
    };

    public boolean test(Number a, Number b) { return this.test(a, b); }

    public static CompareOperator from(String text)
    {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "EQUAL", "=" -> EQUAL;
            case "OP_NE", "!=" -> NOT_EQUAL;
            case "OP_GT", ">" -> GREATER;
            case "OP_GT_EQ", ">=" -> GREATER_EQUAL;
            case "OP_LT", "<" -> LESSER;
            case "OP_LT_EQ", "<=" -> LESSER_EQUAL;
            default -> throw new IllegalArgumentException("Unsupported Operator: " + text);
        };
    }

    public static String toString(CompareOperator compareOperator)
    {
        return switch (compareOperator) {
            case EQUAL -> "=";
            case NOT_EQUAL -> "!=";
            case GREATER -> ">";
            case GREATER_EQUAL -> ">=";
            case LESSER -> "<";
            case LESSER_EQUAL -> "<=";
        };
    }
}
