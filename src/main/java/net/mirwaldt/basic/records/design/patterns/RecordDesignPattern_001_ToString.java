package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_001_ToString {
    sealed interface Expression permits Variable, Not, And, Or {
    }

    record Variable(String name) implements Expression {
    }

    record Not(Expression unnegated) implements Expression {

    }

    record And(Expression left, Expression right) implements Expression {

    }

    record Or(Expression left, Expression right) implements Expression {

    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Variable variable -> variable.name();
            case Not not -> "!" + toString(not.unnegated());
            case And and -> toString(and.left()) + " && " + toString(and.right());
            case Or or -> toString(or.left()) + " || " + toString(or.right());
        };
    }

    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");

        Expression expression = new And(new Or(new And(A, new Not(B)), new Not(new And(C, D))), E);

        // prints out "A && !B || !C && D && E" which is wrong because it isn't "(A && !B || !(C && D)) && E"
        System.out.println(toString(expression));
    }
}
