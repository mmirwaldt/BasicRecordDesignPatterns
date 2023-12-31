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

import java.util.Map;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_09_VisitorPattern.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_09_VisitorPattern.Value.TRUE;

public class RecordDesignPattern_09_VisitorPattern {

    sealed interface Expression permits WithNoOperand, WithOneOperand, WithTwoOperands {
        <V> V accept(ExpressionVisitor<V> visitor);
    }

    sealed interface WithNoOperand extends Expression permits Value, Variable {

    }

    sealed interface WithOneOperand extends Expression permits Not, Brackets {

    }

    enum Value implements WithNoOperand {
        TRUE, FALSE;

        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    record Variable(String name) implements WithNoOperand {
        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    record Not(Expression unnegated) implements WithOneOperand {
        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    record Brackets(Expression withoutBrackets) implements WithOneOperand {
        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    sealed interface WithTwoOperands extends Expression permits And, Or {

    }

    record And(Expression left, Expression right) implements WithTwoOperands {
        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    record Or(Expression left, Expression right) implements WithTwoOperands {
        @Override
        public <V> V accept(ExpressionVisitor<V> visitor) {
            return visitor.visit(this);
        }
    }

    interface ExpressionVisitor<V> {
        V visit(Value value);

        V visit(Variable variable);

        V visit(Not not);

        V visit(And and);

        V visit(Or or);

        V visit(Brackets brackets);

        default V visit(Expression expression) {
            return switch (expression) {
                case Value value -> visit(value);
                case Variable variable -> visit(variable);
                case Not not -> visit(not);
                case Brackets brackets -> visit(brackets);
                case And and -> visit(and);
                case Or or -> visit(or);
            };
        }
    }

    static class Bracketeer implements ExpressionVisitor<Expression> {
        @Override
        public Expression visit(Value value) {
            return value;
        }

        @Override
        public Expression visit(Variable variable) {
            return variable;
        }

        @Override
        public Expression visit(Not not) {
            Expression visitedUnnegated = not.unnegated().accept(this);
            return new Not((not.unnegated() instanceof WithTwoOperands)
                    ? new Brackets(visitedUnnegated)
                    : new Not(visitedUnnegated)
            );
        }

        @Override
        public Expression visit(And and) {
            Expression visitedLeft = and.left().accept(this);
            Expression left = (and.left() instanceof Or) ? new Brackets(visitedLeft) : visitedLeft;

            Expression visitedRight = and.right().accept(this);
            Expression right = (and.right() instanceof Or) ? new Brackets(visitedRight) : visitedRight;

            return new And(left, right);
        }

        @Override
        public Expression visit(Or or) {
            return new Or(or.left().accept(this), or.right().accept(this));
        }

        @Override
        public Expression visit(Brackets brackets) {
            return brackets.withoutBrackets().accept(this);
        }
    }

    static class Stringifier implements ExpressionVisitor<String> {
        @Override
        public String visit(Value value) {
            return value.name();
        }

        @Override
        public String visit(Variable variable) {
            return variable.name();
        }

        @Override
        public String visit(Not not) {
            return "!" + not.unnegated().accept(this);
        }

        @Override
        public String visit(And and) {
            return and.left().accept(this) + " && " + and.right().accept(this);
        }

        @Override
        public String visit(Or or) {
            return or.left().accept(this) + " || " + or.right().accept(this);
        }

        @Override
        public String visit(Brackets brackets) {
            return "(" + brackets.withoutBrackets().accept(this) + ")";
        }
    }

    static class Evaluator implements ExpressionVisitor<Boolean> {
        private final Map<Variable, Value> values;

        public Evaluator(Map<Variable, Value> values) {
            this.values = Map.copyOf(values);
        }

        @Override
        public Boolean visit(Value value) {
            return value == TRUE;
        }

        @Override
        public Boolean visit(Variable variable) {
            return visit(values.get(variable));
        }

        @Override
        public Boolean visit(Not not) {
            return !not.unnegated().accept(this);
        }

        @Override
        public Boolean visit(And and) {
            return and.left().accept(this) && and.right().accept(this);
        }

        @Override
        public Boolean visit(Or or) {
            return or.left().accept(this) || or.right().accept(this);
        }

        @Override
        public Boolean visit(Brackets brackets) {
            return brackets.withoutBrackets().accept(this);
        }
    }

    /*
    Output:
    (FALSE && !!B || !(C && D)) && TRUE
    true
     */
    public static void main(String[] args) {
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");

        Expression expression = new And(new Or(new And(FALSE, new Not(B)), new Not(new And(C, D))), TRUE);

        Bracketeer bracketeer = new Bracketeer();
        Expression withBrackets = bracketeer.visit(expression);

        Stringifier stringifier = new Stringifier();
        System.out.println(stringifier.visit(withBrackets)); // prints out "(FALSE && !B || !(C && D)) && TRUE"

        Evaluator evaluator = new Evaluator(Map.of(B, TRUE, C, FALSE, D, TRUE));
        System.out.println(expression.accept(evaluator)); // print true
    }
}
