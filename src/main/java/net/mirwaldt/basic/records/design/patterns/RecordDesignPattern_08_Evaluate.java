package net.mirwaldt.basic.records.design.patterns;

import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_08_Evaluate.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_08_Evaluate.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_08_Evaluate {
    sealed interface Expression permits WithNoOperand, WithOneOperand, WithTwoOperands {

    }

    sealed interface WithNoOperand extends Expression permits Variable, Value {

    }

    sealed interface WithOneOperand extends Expression permits Not {

    }

    enum Value implements WithNoOperand {
        TRUE, FALSE;
    }

    record Variable(String name) implements WithNoOperand {

    }

    record Not(Expression unnegated) implements WithOneOperand {

    }

    sealed interface WithTwoOperands extends Expression permits And, Or {

    }

    record And(Expression left, Expression right) implements WithTwoOperands {

    }

    record Or(Expression left, Expression right) implements WithTwoOperands {

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

    /*
    Output:
    false
    true
     */
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
