package net.mirwaldt.basic.records.design.patterns;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Generics.BitValue._0;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Generics.BitValue._1;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Generics.BooleanValue.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_Generics.BooleanValue.TRUE;


@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_10_Generics {
    sealed interface Value<V extends Value<V>> extends Expression<V> permits BooleanValue, BitValue {
        V getTrue();
        V getFalse();
        default V negate() {
            return (this == getTrue()) ? getFalse() : getTrue();
        }
        default V and(V right) {
            return (this == getTrue() && right == getTrue()) ? getTrue() : getFalse();
        }
        default V or(V right) {
            return (this == getTrue() || right == getTrue()) ? getTrue() : getFalse();
        }
    }

    // ...
    enum BooleanValue implements Value<BooleanValue> {
        TRUE, FALSE;

        @Override
        public BooleanValue getTrue() {
            return TRUE;
        }

        @Override
        public BooleanValue getFalse() {
            return FALSE;
        }
    }

    enum BitValue implements Value<BitValue> {
        _1, _0;

        @Override
        public BitValue getTrue() {
            return _1;
        }

        @Override
        public BitValue getFalse() {
            return _0;
        }
    }

    sealed interface Expression<V extends Value<V>> permits Value, UnaryExpression, ManyExpression {

    }

    sealed interface UnaryExpression<V extends Value<V>> extends Expression<V> permits Variable, Not, Brackets {

    }

    record Variable<V extends Value<V>>(String name) implements UnaryExpression<V> {

    }

    record Not<V extends Value<V>>(Expression<V> unnegated) implements UnaryExpression<V> {

    }

    record Brackets<V extends Value<V>>(Expression<V> withoutBrackets) implements UnaryExpression<V> {

    }

    sealed interface ManyExpression<V extends Value<V>> extends Expression<V> permits And, Or {
        boolean isBinary();
    }

    record And<V extends Value<V>>(Expression<V> left, Expression<V> middle, List<Expression<V>> rights) implements ManyExpression<V> {
        And { // for libraries
            rights = List.copyOf(rights);
        }

        @SafeVarargs
        And(Expression<V> left, Expression<V> middle, Expression<V>... last) {
            this(left, middle, List.copyOf(Arrays.asList(last)));
        }

        public And<V> withoutLast() {
            return new And<>(left, middle, rights.subList(0, rights.size() - 1));
        }

        public Expression<V> last() {
            return rights.get(rights.size() - 1);
        }

        public boolean isBinary() {
            return rights.isEmpty();
        }
    }

    record Or<V extends Value<V>>(Expression<V> left, Expression<V> middle, List<Expression<V>> rights) implements ManyExpression<V> {
        Or {  // for libraries
            rights = List.copyOf(rights);
        }

        @SafeVarargs
        Or(Expression<V> left, Expression<V> middle, Expression<V>... last) {
            this(left, middle, List.copyOf(Arrays.asList(last)));
        }

        public Or<V> withoutLast() {
            return new Or<>(left, middle, rights.subList(0, rights.size() - 1));
        }

        public Expression<V> last() {
            return rights.get(rights.size() - 1);
        }

        public boolean isBinary() {
            return rights.isEmpty();
        }
    }

    public static <V extends Value<V>> Expression<V> withBrackets(Expression<V> child, Expression<V> parent) {
        return switch (child) {
            case ManyExpression<V> binary when parent instanceof Not -> new Brackets<V>(withBrackets(binary, child));
            case Or<V> or when parent instanceof And -> new Brackets<V>(withBrackets(or, child));
            case Not<V>(var unnegated) -> new Not<V>(withBrackets(unnegated, child));
            case And<V> and when and.isBinary() ->
                    new And<>(withBrackets(and.left(), child), withBrackets(and.middle(), child));
            case And<V> and -> withBrackets((new And<>(and.withoutLast(), and.last())), child);
            case Or<V> or when or.isBinary() ->
                    new Or<>(withBrackets(or.left(), child), withBrackets(or.middle(), child));
            case Or<V> or -> withBrackets((new Or<>(or.withoutLast(), or.last())), child);
            default -> child;
        };
    }

    public static <V extends Value<V>> String toString(Expression<V> expression) {
        return switch (expression) {
            case BooleanValue booleanValue -> booleanValue.toString();
            case BitValue bitValue -> bitValue.toString().substring(1);
            case Variable<V>(var name) -> name;
            case Not<V>(var unnegated) -> "!" + toString(unnegated);
            case Brackets<V>(var withoutBrackets) -> "(" + toString(withoutBrackets) + ")";
            case And<V> and when and.isBinary() -> toString(and.left()) + " && " + toString(and.middle());
            case And<V> and -> toString(new And<>(and.withoutLast(), and.last()));
            case Or<V> or when or.isBinary() -> toString(or.left()) + " || " + toString(or.middle());
            case Or<V> or -> toString(new Or<>(or.withoutLast(), or.last()));
        };
    }

    public static <V extends Value<V>> V evaluate(Expression<V> expression, Map<Variable<V>, Expression<V>> values) {
        return switch (expression) {
            case Variable<V> variable -> evaluate(values.get(variable), values);
            case Not<V>(var unnegated) -> evaluate(unnegated, values).negate();
            case And<V> and when and.isBinary()  -> evaluate(and.left(), values).and(evaluate(and.middle(), values));
            case And<V> and -> evaluate(new And<>(and.withoutLast(), and.last()), values);
            case Or<V> or when or.isBinary()  -> evaluate(or.left(), values).or(evaluate(or.middle(), values));
            case Or<V> or -> evaluate(new Or<>(or.withoutLast(), or.last()), values);
            case Expression<V> expr -> (V) expr;
        };
    }

    public static void main(String[] args) {
        Variable<BitValue> A = new Variable<>("A");
        Variable<BitValue> B = new Variable<>("B");
        Variable<BitValue> C = new Variable<>("C");
        Variable<BitValue> D = new Variable<>("D");

        Expression<BitValue> bitExpression =
                new And<>(new Or<>(new And<>(A, _1, new Not<>(B)), new Not<>(new And<>(C, D)), _0), _1);
        Expression<BitValue> bitExpressionWithBrackets = withBrackets(bitExpression, null);
        // prints out "(A && 1 && !B || !(C && D) || 0) && 1"
        System.out.println(toString(bitExpressionWithBrackets));
        // prints out _0
        System.out.println(evaluate(bitExpression, Map.of(A, _0, B, _0, C, _1, D, _1)));
        // prints out _1
        System.out.println(evaluate(bitExpression, Map.of(A, _1, B, _0, C, _1, D, _0)));

        Variable<BooleanValue> a = new Variable<>("A");
        Variable<BooleanValue> b = new Variable<>("B");
        Variable<BooleanValue> c = new Variable<>("C");
        Variable<BooleanValue> d = new Variable<>("D");

        Expression<BooleanValue> booleanExpression =
                new And<>(new Or<>(new And<>(a, TRUE, new Not<>(b)), new Not<>(new And<>(c, d)), FALSE), TRUE);
        Expression<BooleanValue> booleanExpressionWithBrackets = withBrackets(booleanExpression, null);

        // prints out "(A && TRUE && !B || !(C && C) || FALSE) && TRUE"
        System.out.println(toString(booleanExpressionWithBrackets));
        // prints out FALSE
        System.out.println(evaluate(booleanExpression, Map.of(a, FALSE, b, FALSE, c, TRUE, d, TRUE)));
        // prints out TRUE
        System.out.println(evaluate(booleanExpression, Map.of(a, TRUE, b, FALSE, c, TRUE, d, FALSE)));
    }
}
