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
public class RecordDesignPattern_03_ToString_Flawed_WithPatternMatching {
    sealed interface Expression permits Variable, Not, And, Or {
    }

    record Variable(String name) implements Expression {

    }

    record Not(Expression unnegated) implements Expression {

    }

    record And(Expression left, Expression right) implements Expression {

    }

    record Or(Expression left, Expression right) implements Expression {

    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Variable(var name) -> name;
            case Not(var unnegated) -> "!" + toString(unnegated);
            case And(var left, var right) -> toString(left) + " && " + toString(right);
            case Or(var left, var right) -> toString(left) + " || " + toString(right);
        };
    }

    /*
    Output:
    A && !B || !C && D && E
     */
    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");

        Expression expression = new And(new Or(new And(A, new Not(B)), new Not(new And(C, D))), E);

        // prints out "A && !B || !C && D && E" which is wrong because it isn't "(A && !B || !(C && D)) && E"
        System.out.println(toString(expression));
    }
}
