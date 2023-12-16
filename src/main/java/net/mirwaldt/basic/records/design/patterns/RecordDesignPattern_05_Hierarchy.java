package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_05_Hierarchy {
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

    public static Expression withBrackets(Expression expression) {
        return switch (expression) {
            case Not(WithTwoOperands withTwoOperands) -> new Not(new Brackets(withBrackets(withTwoOperands)));

            case And(Or left, Or right) -> new And(new Brackets(withBrackets(left)), new Brackets(withBrackets(right)));
            case And(Or or, Expression e) -> new And(new Brackets(withBrackets(or)), withBrackets(e));
            case And(Expression e, Or or) -> new And(withBrackets(e), new Brackets(withBrackets(or)));

            case Not(var unnegated) -> new Not(withBrackets(unnegated));
            case And(var left, var right) -> new And(withBrackets(left), withBrackets(right));
            case Or(var left, var right) -> new Or(withBrackets(left), withBrackets(right));

            default -> expression;
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
        Expression withBrackets = withBrackets(expression);
        System.out.println(toString(withBrackets));
    }
}
