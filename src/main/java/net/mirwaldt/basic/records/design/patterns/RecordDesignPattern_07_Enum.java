package net.mirwaldt.basic.records.design.patterns;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_07_Enum.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_07_Enum.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_07_Enum {
    sealed interface Expression permits Value, UnaryExpression, BinaryExpression {

    }

    sealed interface UnaryExpression extends Expression permits Variable, Not, Brackets {

    }

    enum Value implements Expression {
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

    public static Expression withBrackets(Expression child, Expression parent) {
        return switch (child) {
            case Not(BinaryExpression binary) -> new Not(new Brackets(withBrackets(binary, child)));
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

    public static void main(String[] args) {
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        
        Expression expression = new And(new Or(new And(FALSE, new Not(B)), new Not(new And(C, D))), TRUE);
        Expression withBrackets = withBrackets(expression, null);
        System.out.println(toString(withBrackets)); // prints out "(FALSE && !B || !(C && D)) && TRUE"
    }
}
