package net.mirwaldt.basic.records.design.patterns;

import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_006_Evaluate.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_006_Evaluate.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_006_Evaluate {
    sealed interface Expression permits UnaryExpression, BinaryExpression {

    }

    sealed interface UnaryExpression extends Expression permits Value, Variable, Not, Brackets {

    }

    enum Value implements UnaryExpression {
        TRUE, FALSE;
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

    public static boolean evaluate(Expression expression, Map<Variable, Value> values) {
        return switch (expression) {
            case Value value -> value == TRUE;
            case Variable variable -> evaluate(values.get(variable), values);
            case Brackets brackets -> evaluate(brackets.withoutBrackets(), values);
            case Not not -> !evaluate(not.unnegated(), values);
            case And and -> evaluate(and.left(), values) && evaluate(and.right(), values);
            case Or or -> evaluate(or.left(), values) || evaluate(or.right(), values);
        };
    }

    public static void main(String[] args) {
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");

        // "(FALSE && !B || !(C && D)) && TRUE"
        Expression expression = new And(new Or(new And(FALSE, new Not(B)), new Not(new And(C, D))), TRUE);

        // false
        System.out.println(evaluate(expression, Map.of(B, FALSE, C, TRUE, D, TRUE)));

        // true
        System.out.println(evaluate(expression, Map.of(B, TRUE, C, FALSE, D, TRUE)));
    }
}
