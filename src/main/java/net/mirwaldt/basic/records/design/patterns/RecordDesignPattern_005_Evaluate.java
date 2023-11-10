package net.mirwaldt.basic.records.design.patterns;

import java.util.Map;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_005_Evaluate {
    sealed interface Expression permits UnaryExpression, BinaryExpression {

    }

    sealed interface UnaryExpression extends Expression permits Variable, Not, Brackets {

    }

    record Variable(String name) implements UnaryExpression {

    }

    record Not(Expression unnegated) implements UnaryExpression {

    }

    record Brackets(Expression withoutBrackets) implements UnaryExpression {

    }

    sealed interface BinaryExpression extends Expression permits And, Or {

    }

    record And(Expression left, Expression right) implements BinaryExpression {

    }

    record Or(Expression left, Expression right) implements BinaryExpression {

    }

    public static boolean evaluate(Expression expression, Map<Variable, Boolean> values) {
        return switch (expression) {
            case Variable variable -> values.get(variable);
            case Brackets brackets -> evaluate(brackets.withoutBrackets(), values);
            case Not not -> !evaluate(not.unnegated(), values);
            case And and -> evaluate(and.left(), values) && evaluate(and.right(), values);
            case Or or -> evaluate(or.left(), values) || evaluate(or.right(), values);
        };
    }

    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");

        // "(A && !B || !(C && D)) && E"
        Expression expression = new And(new Or(new And(A, new Not(B)), new Not(new And(C, D))), E);

        // false
        System.out.println(evaluate(expression, Map.of(A, false, B, false, C, true, D, true, E, true)));

        // true
        System.out.println(evaluate(expression, Map.of(A, true, B, false, C, false, D, true, E, true)));
    }
}
