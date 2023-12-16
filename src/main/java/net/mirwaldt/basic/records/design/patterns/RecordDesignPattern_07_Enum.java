package net.mirwaldt.basic.records.design.patterns;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_07_Enum.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_07_Enum.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_07_Enum {
    sealed interface Expression permits WithNoOperand, WithOneOperand, WithTwoOperands {

    }

    sealed interface WithNoOperand extends Expression permits Variable, Value {

    }

    sealed interface WithOneOperand extends Expression permits Not, Brackets {

    }

    enum Value implements WithNoOperand {
        TRUE, FALSE;
    }

    record Variable(String name) implements WithNoOperand {

    }

    record Not(Expression unnegated) implements WithOneOperand {

    }

    record Brackets(Expression withoutBrackets) implements WithOneOperand {

    }

    sealed interface WithTwoOperands extends Expression permits And, Or {

    }

    record And(Expression left, Expression right) implements WithTwoOperands {

    }

    record Or(Expression left, Expression right) implements WithTwoOperands {

    }

    public static Expression withBrackets(Expression child, Expression parent) {
        return switch (child) {
            case Not(WithTwoOperands withTwoOperands) -> new Not(new Brackets(withBrackets(withTwoOperands, child)));
            case Or or when parent instanceof And -> new Brackets(withBrackets(or, child));
            case Not(var unnegated) -> new Not(withBrackets(unnegated, child));
            case And(var left, var right) -> new And(withBrackets(left, child), withBrackets(right, child));
            case Or(var left, var right) -> new Or(withBrackets(left, child), withBrackets(right, child));
            default -> child;
        };
    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Value value -> value.toString();
            case Variable variable -> variable.name();
            case Not(var unnegated) -> "!" + toString(unnegated);
            case Brackets(var withoutBrackets) -> "(" + toString(withoutBrackets) + ")";
            case And(var left, var right) -> toString(left) + " && " + toString(right);
            case Or(var left, var right) -> toString(left) + " || " + toString(right);
        };
    }

    /*
    Output:
    (FALSE && !B || !(C && D)) && TRUE
     */
    public static void main(String[] args) {
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        
        Expression expression = new And(new Or(new And(FALSE, new Not(B)), new Not(new And(C, D))), TRUE);
        Expression withBrackets = withBrackets(expression, null);
        System.out.println(toString(withBrackets));
    }
}
