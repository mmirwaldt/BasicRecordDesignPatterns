/*
 * Copyright (c) 2023, Michael Mirwaldt. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/4.0/">
 * <img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-nd/4.0/88x31.png" />
 * </a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/4.0/">
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License</a>.
 */

package net.mirwaldt.basic.records.design.patterns;


import java.util.List;
import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_ListsAndGenerics.BitValue._0;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_ListsAndGenerics.BitValue._1;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_ListsAndGenerics.BoolValue.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_10_ListsAndGenerics.BoolValue.TRUE;


@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_10_ListsAndGenerics {
    sealed interface Value<V extends Value<V>> extends WithNoOperand<V> permits BoolValue, BitValue {
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

    enum BoolValue implements Value<BoolValue> {
        TRUE, FALSE;

        @Override
        public BoolValue getTrue() {
            return TRUE;
        }

        @Override
        public BoolValue getFalse() {
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

        @Override
        public String toString() {
            return name().substring(1);
        }
    }

    sealed interface Expression<V extends Value<V>> permits WithNoOperand, WithOneOperand, WithManyOperands {

    }

    sealed interface WithNoOperand<V extends Value<V>> extends Expression<V> permits Value, Variable {

    }

    sealed interface WithOneOperand<V extends Value<V>> extends Expression<V> permits Not, Brackets {

    }

    record Variable<V extends Value<V>>(String name) implements WithNoOperand<V> {

    }

    record Not<V extends Value<V>>(Expression<V> unnegated) implements WithOneOperand<V> {

    }

    record Brackets<V extends Value<V>>(Expression<V> withoutBrackets) implements WithOneOperand<V> {

    }

    sealed interface WithManyOperands<V extends Value<V>> extends Expression<V> permits And, Or {
        boolean isBinary();
    }

    record And<V extends Value<V>>(Expression<V> first, Expression<V> second,
                                   List<Expression<V>> tail) implements WithManyOperands<V> {
        And { // for libraries
            tail = List.copyOf(tail);
        }

        @SafeVarargs
        And(Expression<V> first, Expression<V> second, Expression<V>... tail) {
            this(first, second, List.of(tail));
        }

        public And<V> withoutLast() {
            return new And<>(first, second, tail.subList(0, tail.size() - 1));
        }

        public Expression<V> last() {
            return tail.get(tail.size() - 1);
        }

        public boolean isBinary() {
            return tail.isEmpty();
        }
    }

    record Or<V extends Value<V>>(Expression<V> first, Expression<V> second,
                                  List<Expression<V>> tail) implements WithManyOperands<V> {
        Or {  // for libraries
            tail = List.copyOf(tail);
        }

        @SafeVarargs
        Or(Expression<V> first, Expression<V> second, Expression<V>... tail) {
            this(first, second, List.of(tail));
        }

        public Or<V> withoutLast() {
            return new Or<>(first, second, tail.subList(0, tail.size() - 1));
        }

        public Expression<V> last() {
            return tail.get(tail.size() - 1);
        }

        public boolean isBinary() {
            return tail.isEmpty();
        }
    }

    public static <V extends Value<V>> Expression<V> withBrackets(Expression<V> child, Expression<V> parent) {
        return switch (child) {
            case WithManyOperands<V> many when parent instanceof Not -> new Brackets<V>(withBrackets(many, child));
            case Or<V> or when parent instanceof And -> new Brackets<V>(withBrackets(or, child));
            case Not<V>(var unnegated) -> new Not<V>(withBrackets(unnegated, child));
            case And<V> and when and.isBinary() ->
                    new And<>(withBrackets(and.first(), child), withBrackets(and.second(), child));
            case And<V> and -> withBrackets((new And<>(and.withoutLast(), and.last())), child);
            case Or<V> or when or.isBinary() ->
                    new Or<>(withBrackets(or.first(), child), withBrackets(or.second(), child));
            case Or<V> or -> withBrackets((new Or<>(or.withoutLast(), or.last())), child);
            default -> child;
        };
    }

    public static <V extends Value<V>> String toString(Expression<V> expression) {
        return switch (expression) {
            case BoolValue boolValue -> boolValue.toString();
            case BitValue bitValue -> bitValue.toString();
            case Variable<V>(var name) -> name;
            case Not<V>(var unnegated) -> "!" + toString(unnegated);
            case Brackets<V>(var withoutBrackets) -> "(" + toString(withoutBrackets) + ")";
            case And<V> and when and.isBinary() -> toString(and.first()) + " && " + toString(and.second());
            case And<V> and -> toString(new And<>(and.withoutLast(), and.last()));
            case Or<V> or when or.isBinary() -> toString(or.first()) + " || " + toString(or.second());
            case Or<V> or -> toString(new Or<>(or.withoutLast(), or.last()));
        };
    }

    public static <V extends Value<V>> V evaluate(Expression<V> expression, Map<Variable<V>, Expression<V>> values) {
        return switch (expression) {
            case Variable<V> variable -> evaluate(values.get(variable), values);
            case Not<V>(var unnegated) -> evaluate(unnegated, values).negate();
            case And<V> and when and.isBinary() -> evaluate(and.first(), values).and(evaluate(and.second(), values));
            case And<V> and -> evaluate(new And<>(and.withoutLast(), and.last()), values);
            case Or<V> or when or.isBinary() -> evaluate(or.first(), values).or(evaluate(or.second(), values));
            case Or<V> or -> evaluate(new Or<>(or.withoutLast(), or.last()), values);
            case Expression<V> expr -> (V) expr;
        };
    }

    /*
    Output:
    (A && 1 && !B || !(C && D) || 0) && 1
    0
    1
    (A && TRUE && !B || !(C && D) || FALSE) && TRUE
    FALSE
    TRUE
     */
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
        // prints out 0
        System.out.println(evaluate(bitExpression, Map.of(A, _0, B, _0, C, _1, D, _1)));
        // prints out 1
        System.out.println(evaluate(bitExpression, Map.of(A, _1, B, _0, C, _1, D, _0)));

        Variable<BoolValue> a = new Variable<>("A");
        Variable<BoolValue> b = new Variable<>("B");
        Variable<BoolValue> c = new Variable<>("C");
        Variable<BoolValue> d = new Variable<>("D");

        Expression<BoolValue> booleanExpression =
                new And<>(new Or<>(new And<>(a, TRUE, new Not<>(b)), new Not<>(new And<>(c, d)), FALSE), TRUE);
        Expression<BoolValue> booleanExpressionWithBrackets = withBrackets(booleanExpression, null);

        // prints out "(A && TRUE && !B || !(C && C) || FALSE) && TRUE"
        System.out.println(toString(booleanExpressionWithBrackets));
        // prints out FALSE
        System.out.println(evaluate(booleanExpression, Map.of(a, FALSE, b, FALSE, c, TRUE, d, TRUE)));
        // prints out TRUE
        System.out.println(evaluate(booleanExpression, Map.of(a, TRUE, b, FALSE, c, TRUE, d, FALSE)));
    }
}
