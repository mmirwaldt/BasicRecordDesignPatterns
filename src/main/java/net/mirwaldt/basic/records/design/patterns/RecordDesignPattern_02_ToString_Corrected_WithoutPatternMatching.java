package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_02_ToString_Corrected_WithoutPatternMatching {
    sealed interface Expression permits Variable, Not, And, Or {
        default String inBrackets(Expression expression) {
            return "(" + expression.toString() + ")";
        }
    }

    record Variable(String name) implements Expression {
        @Override
        public String toString() {
            return name;
        }
    }

    record Not(Expression unnegated) implements Expression {
        @Override
        public String toString() {
            return "!" + ((unnegated instanceof Variable)
                    ? unnegated
                    : inBrackets(unnegated));
        }
    }

    record And(Expression left, Expression right) implements Expression {
        @Override
        public String toString() {
            return ((left instanceof Or or) ? inBrackets(or) : left)
                    + " && "
                    + ((right instanceof Or or) ? inBrackets(or) : right);
        }
    }

    record Or(Expression left, Expression right) implements Expression {
        @Override
        public String toString() {
            return left.toString() + " || " + right.toString();
        }
    }

    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");

        Expression expression = new And(new Or(new And(A, new Not(B)), new Not(new And(C, D))), E);

        // prints out "(A && !B || !(C && D)) && E" which is right
        System.out.println(expression.toString());
    }
}
