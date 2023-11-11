package net.mirwaldt.basic.records.design.patterns;


import java.util.Arrays;
import java.util.List;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_009_Generics.BitValue._0;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_009_Generics.BitValue._1;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_009_Generics.BooleanValue.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_009_Generics.BooleanValue.TRUE;


@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_009_Generics {
    sealed interface Value permits BooleanValue, BitValue {

    }

    enum BooleanValue implements Value, UnaryExpression<BooleanValue> {
        TRUE, FALSE;
    }

    enum BitValue implements Value, UnaryExpression<BitValue> {
        _0, _1;
    }

    sealed interface Expression<V extends Value> permits UnaryExpression, ManyExpression {

    }

    sealed interface UnaryExpression<V extends Value> extends Expression<V> permits BooleanValue, BitValue, Variable, Not, Brackets {

    }

    record Variable<V extends Value>(String name) implements UnaryExpression<V> {

    }

    record Not<V extends Value>(Expression<V> unnegated) implements UnaryExpression<V> {

    }

    record Brackets<V extends Value>(Expression<V> withoutBrackets) implements UnaryExpression<V> {

    }

    sealed interface ManyExpression<V extends Value> extends Expression<V> permits And, Or {
        List<Expression<V>> right();

        default List<Expression<V>> rightWithoutLast() {
            List<Expression<V>> rights = right();
            return List.copyOf(rights.subList(0, rights.size() - 1));
        }

        default Expression<V> last() {
            return right().get(right().size() - 1);
        }

        default boolean isBinary() {
            return right().isEmpty();
        }
    }

    record And<V extends Value>(Expression<V> left, Expression<V> middle, List<Expression<V>> right) implements ManyExpression<V> {
        @SafeVarargs
        And(Expression<V> left, Expression<V> middle, Expression<V>... last) {
            this(left, middle, List.copyOf(Arrays.asList(last)));
        }

        public And<V> withoutLast() {
            return new And<>(left, middle, rightWithoutLast());
        }
    }

    record Or<V extends Value>(Expression<V> left, Expression<V> middle, List<Expression<V>> right) implements ManyExpression<V> {
        @SafeVarargs
        Or(Expression<V> left, Expression<V> middle, Expression<V>... last) {
            this(left, middle, List.copyOf(Arrays.asList(last)));
        }

        public Or<V> withoutLast() {
            return new Or<>(left, middle, rightWithoutLast());
        }
    }

    public static <V extends Value> Expression<V> addBrackets(Expression<V> child, Expression<V> parent) {
        return switch (child) {
            case ManyExpression<V> binary when parent instanceof Not -> new Brackets<V>(addBrackets(binary, child));
            case Or<V> or when parent instanceof And -> new Brackets<V>(addBrackets(or, child));
            case Not<V> not -> new Not<V>(addBrackets(not.unnegated(), not));
            case And<V> and when and.isBinary() -> new And<>(addBrackets(and.left(), child), addBrackets(and.middle(), child));
            case Or<V> or when or.isBinary() -> new Or<>(addBrackets(or.left(), child), addBrackets(or.middle(), child));
            case And<V> and -> addBrackets((new And<>(and.withoutLast(), and.last())), child);
            case Or<V> or -> addBrackets((new Or<>(or.withoutLast(), or.last())), child);
            default -> child;
        };
    }

    public static <V extends Value> String toString(Expression<V> expression) {
        return switch (expression) {
            case BooleanValue booleanValue -> booleanValue.name();
            case BitValue bitValue -> bitValue.name().substring(1);
            case Variable<V> variable -> variable.name();
            case Not<V> not -> "!" + toString(not.unnegated());
            case Brackets<V> inBrackets -> "(" + toString(inBrackets.withoutBrackets()) + ")";
            case And<V> and when and.isBinary() -> toString(and.left()) + " && " + toString(and.middle());
            case Or<V> or when or.isBinary() -> toString(or.left()) + " || " + toString(or.middle());
            case And<V> and -> toString(new And<>(and.withoutLast(), and.last()));
            case Or<V> or -> toString(new Or<>(or.withoutLast(), or.last()));
        };
    }

    public static void main(String[] args) {
        Variable<BitValue> A = new Variable<>("A");
        Variable<BitValue> B = new Variable<>("B");
        Variable<BitValue> C = new Variable<>("C");
        Variable<BitValue> D = new Variable<>("D");

        Expression<BitValue> bitExpression = new And<>(new Or<>(new And<>(A, _1, new Not<>(B)), new Not<>(new And<>(C, D)), _0), _1);
        Expression<BitValue> bitExpressionWithBrackets = addBrackets(bitExpression, null);
        System.out.println(toString(bitExpressionWithBrackets)); // prints out "(A && 1 && !B || !(C && D) || 0) && 1"


        Variable<BooleanValue> a = new Variable<>("A");
        Variable<BooleanValue> b = new Variable<>("B");
        Variable<BooleanValue> c = new Variable<>("C");
        Variable<BooleanValue> d = new Variable<>("D");

        Expression<BooleanValue> booleanExpression = new And<>(new Or<>(new And<>(a, TRUE, new Not<>(b)), new Not<>(new And<>(c, d)), FALSE), FALSE);
        Expression<BooleanValue> booleanExpressionWithBrackets = addBrackets(booleanExpression, null);
        System.out.println(toString(booleanExpressionWithBrackets)); // prints out "(A && TRUE && !B || !(C && C) || FALSE) && FALSE"
    }
}
