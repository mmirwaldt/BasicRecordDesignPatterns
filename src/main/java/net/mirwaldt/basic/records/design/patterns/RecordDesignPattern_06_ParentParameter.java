package net.mirwaldt.basic.records.design.patterns;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_06_ParentParameter {
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

    public static Expression addBrackets(Expression child, Expression parent) {
        return switch (child) {
            case Not(BinaryExpression binaryExpression) -> new Not(new Brackets(addBrackets(binaryExpression, child)));

            case Or or when parent instanceof And -> new Brackets(addBrackets(or, child));

            case Not not -> new Not(addBrackets(not.unnegated(), not));
            case And and -> new And(addBrackets(and.left(), child), addBrackets(and.right(), child));
            case Or or -> new Or(addBrackets(or.left(), child), addBrackets(or.right(), child));

            default -> child;
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
        Expression withBrackets = addBrackets(expression, null);
        System.out.println(toString(withBrackets)); // prints out "(A && !B || !(C && D)) && E"
    }
}
