package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_003_Hierarchy {
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

    public static Expression addBrackets(Expression expression) {
        return switch (expression) {
            case Not(BinaryExpression binaryExpression) -> new Not(new Brackets(addBrackets(binaryExpression)));

            case And(Or left, Or right) -> new And(new Brackets(addBrackets(left)), new Brackets(addBrackets(right)));
            case And(Or or, Expression e) -> new And(new Brackets(addBrackets(or)), addBrackets(e));
            case And(Expression e, Or or) -> new And(addBrackets(e), new Brackets(addBrackets(or)));

            case And and -> new And(addBrackets(and.left()), addBrackets(and.right()));
            case Or or -> new Or(addBrackets(or.left()), addBrackets(or.right()));
            default -> expression;
        };
    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Variable variable -> variable.name();
            case Not not -> "!" + toString(not.unnegated());
            case Brackets inBrackets -> "(" + toString(inBrackets.withoutBrackets()) + ")";
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
        Expression withBrackets = addBrackets(expression);
        System.out.println(toString(withBrackets)); // prints out "(A && !B || !(C && D)) && E"
    }
}
