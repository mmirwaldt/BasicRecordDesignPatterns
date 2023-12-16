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

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_04_BracketsForString {
    sealed interface Expression permits Variable, Not, And, Or, Brackets {

    }

    record Variable(String name) implements Expression {

    }

    record Brackets(Expression withoutBrackets) implements Expression {

    }

    record Not(Expression unnegated) implements Expression {

    }

    record And(Expression left, Expression right) implements Expression {

    }

    record Or(Expression left, Expression right) implements Expression {

    }

    public static Expression withBrackets(Expression expression) {
        return switch (expression) {
            case Not(And and) -> new Not(new Brackets(withBrackets(and)));
            case Not(Or or) -> new Not(new Brackets(withBrackets(or)));

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
            case Variable(var name) -> name;
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
