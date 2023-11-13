package net.mirwaldt.basic.records.design.patterns;


import java.util.Arrays;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Varargs.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Varargs.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_10_Varargs {
    sealed interface Expression permits UnaryExpression, ManyExpression {

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

    sealed interface ManyExpression extends Expression permits And, Or {
        Expression withoutLast();

        Expression last();

        boolean isBinary();

        static Expression[] rightWithoutLast(Expression[] right) {
            return Arrays.copyOf(right, right.length - 1);
        }

        static Expression last(Expression[] right) {
            return right[right.length - 1];
        }

    }

    record And(Expression left, Expression middle, Expression...right) implements ManyExpression {
        And(Expression left, Expression middle, Expression...right) {
            this.left = left;
            this.middle = middle;
            this.right = Arrays.copyOf(right, right.length);
        }

        @Override
        public And withoutLast() {
            return new And(left, middle, ManyExpression.rightWithoutLast(right));
        }

        @Override
        public Expression last() {
            return ManyExpression.last(right);
        }

        @Override
        public Expression[] right() {
            return Arrays.copyOf(right, right.length);
        }

        @Override
        public boolean isBinary() {
            return right.length == 0;
        }
    }

    record Or(Expression left, Expression middle, Expression...right) implements ManyExpression {
        Or(Expression left, Expression middle, Expression...right) {
            this.left = left;
            this.middle = middle;
            this.right = Arrays.copyOf(right, right.length);
        }

        @Override
        public Or withoutLast() {
            return new Or(left, middle, ManyExpression.rightWithoutLast(right));
        }

        @Override
        public Expression last() {
            return ManyExpression.last(right);
        }

        @Override
        public Expression[] right() {
            return Arrays.copyOf(right, right.length);
        }

        @Override
        public boolean isBinary() {
            return right.length == 0;
        }
    }

    public static Expression addBrackets(Expression child, Expression parent) {
        return switch (child) {
            case ManyExpression binary when parent instanceof Not -> new Brackets(addBrackets(binary, child));
            case Or or when parent instanceof And -> new Brackets(addBrackets(or, child));
            case Not not -> new Not(addBrackets(not.unnegated(), not));
            case And and when and.isBinary() -> addBrackets((new And(and.withoutLast(), and.last())), child);
            case Or or  when or.isBinary() -> addBrackets((new Or(or.withoutLast(), or.last())), child);
            case And and -> new And(addBrackets(and.left(), child), addBrackets(and.middle(), child));
            case Or or -> new Or(addBrackets(or.left(), child), addBrackets(or.middle(), child));
            default -> child;
        };
    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Value value -> value.name();
            case Variable variable -> variable.name();
            case Not not -> "!" + toString(not.unnegated());
            case Brackets inBrackets -> "(" + toString(inBrackets.withoutBrackets()) + ")";
            case And and when and.isBinary() -> toString(new And(and.withoutLast(), and.last()));
            case And and -> toString(and.left()) + " && " + toString(and.middle());
            case Or or when or.isBinary() -> toString(new Or(or.withoutLast(), or.last()));
            case Or or -> toString(or.left()) + " || " + toString(or.middle());
        };
    }

    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        
        Expression expression = new And(new Or(new And(A, FALSE, new Not(B)), new Not(new And(C, D)), FALSE), TRUE);
        Expression withBrackets = addBrackets(expression, null);
        System.out.println(toString(withBrackets)); // prints out "(A || FALSE && !B || !(C && D) || FALSE) && TRUE"
    }
}
