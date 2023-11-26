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

    }

    record And(Expression left, Expression middle, Expression... rights) implements ManyExpression {
        And(Expression left, Expression middle, Expression... rights) {
            this.left = left;
            this.middle = middle;
            this.rights = Arrays.copyOf(rights, rights.length);
        }

        @Override
        public And withoutLast() {
            return new And(left, middle, Arrays.copyOf(rights, rights.length - 1));
        }

        @Override
        public Expression last() {
            return rights[rights.length - 1];
        }

        public Expression[] rights() {
            return Arrays.copyOf(rights, rights.length);
        }

        @Override
        public boolean isBinary() {
            return rights.length == 0;
        }
    }

    record Or(Expression left, Expression middle, Expression... rights) implements ManyExpression {
        Or(Expression left, Expression middle, Expression... rights) {
            this.left = left;
            this.middle = middle;
            this.rights = Arrays.copyOf(rights, rights.length);
        }

        @Override
        public Or withoutLast() {
            return new Or(left, middle, Arrays.copyOf(rights, rights.length - 1));
        }

        @Override
        public Expression last() {
            return rights[rights.length - 1];
        }

        public Expression[] rights() {
            return Arrays.copyOf(rights, rights.length);
        }

        @Override
        public boolean isBinary() {
            return rights.length == 0;
        }
    }

    public static Expression addBrackets(Expression child, Expression parent) {
        return switch (child) {
            case ManyExpression binary when parent instanceof Not -> new Brackets(addBrackets(binary, child));
            case Or or when parent instanceof And -> new Brackets(addBrackets(or, child));
            case Not(var left) -> new Not(addBrackets(left, child));
            case And and when and.isBinary() -> new And(addBrackets(and.left(), child), addBrackets(and.middle(), child));
            case And and -> addBrackets((new And(and.withoutLast(), and.last())), child);
            case Or or when or.isBinary() -> new Or(addBrackets(or.left(), child), addBrackets(or.middle(), child));
            case Or or-> addBrackets((new Or(or.withoutLast(), or.last())), child);
            default -> child;
        };
    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Value value -> value.name();
            case Variable variable -> variable.name();
            case Not(var unnegated) -> "!" + toString(unnegated);
            case Brackets(var withoutBrackets) -> "(" + toString(withoutBrackets) + ")";
            case And and when and.isBinary() -> toString(and.left()) + " && " + toString(and.middle());
            case And and -> toString(new And(and.withoutLast(), and.last()));
            case Or or when or.isBinary() -> toString(or.left()) + " || " + toString(or.middle());
            case Or or -> toString(new Or(or.withoutLast(), or.last()));
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
