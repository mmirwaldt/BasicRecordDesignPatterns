package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_06_ParentParameter {
    sealed interface Expression permits WithNoOperand, WithOneOperand, WithTwoOperands {

    }

    sealed interface WithNoOperand extends Expression permits Variable {

    }

    sealed interface WithOneOperand extends Expression permits Not, Brackets {

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
            case Variable variable -> variable.name();
            case Not(var unnegated) -> "!" + toString(unnegated);
            case Brackets(var withoutBrackets) -> "(" + toString(withoutBrackets) + ")";
            case And(var left, var right) -> toString(left) + " && " + toString(right);
            case Or(var left, var right) -> toString(left) + " || " + toString(right);
        };
    }

    /*
    Output:
    (A && !B || !(C && D)) && E
     */
    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");
        
        Expression expression = new And(new Or(new And(A, new Not(B)), new Not(new And(C, D))), E);
        Expression withBrackets = withBrackets(expression, null);
        System.out.println(toString(withBrackets));
    }
}
