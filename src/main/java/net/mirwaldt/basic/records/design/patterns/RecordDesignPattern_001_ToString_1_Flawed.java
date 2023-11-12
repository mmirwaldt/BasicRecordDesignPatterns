package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_001_ToString_1_Flawed {
    sealed interface Expression permits Variable, Not, And, Or {
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
            return "!" + unnegated;
        }
    }

    record And(Expression left, Expression right) implements Expression {
        @Override
        public String toString() {
            return left.toString() + " && " + right.toString();
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

        // prints out "A && !B || !C && D && E" which is wrong because it isn't "(A && !B || !(C && D)) && E"
        System.out.println(expression);
    }
}
