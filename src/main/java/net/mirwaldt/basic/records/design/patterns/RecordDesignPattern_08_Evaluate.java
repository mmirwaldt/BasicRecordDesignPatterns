package net.mirwaldt.basic.records.design.patterns;

import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_08_Evaluate.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_08_Evaluate.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_08_Evaluate {
    sealed interface Expression permits Value, UnaryExpression, BinaryExpression {

    }

    sealed interface UnaryExpression extends Expression permits Variable, Not {

    }

    enum Value implements Expression {
        TRUE, FALSE;
    }

    record Variable(String name) implements UnaryExpression {

    }

    record Not(Expression unnegated) implements UnaryExpression {

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
            case Not(var unnegated) -> !evaluate(unnegated, values);
            case And(var left, var right) -> evaluate(left, values) && evaluate(right, values);
            case Or(var left, var right) -> evaluate(left, values) || evaluate(right, values);
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
