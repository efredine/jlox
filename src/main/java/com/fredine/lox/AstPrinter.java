package com.fredine.lox;

import java.util.ArrayList;
import java.util.List;

// Creates an unambiguous, if ugly, string representation of AST nodes.
class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<Void> {
    private List<String> formattedStatements = new ArrayList<>();

    AstPrinter(List<Stmt> statements) {
        format(statements);
    }

    public Void print() {
        for (String formattedStatement : formattedStatements) {
            System.out.println(formattedStatement);
        }
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value == null ? "nil" : expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        formattedStatements.add(parenthesize("Expr", stmt.expression));
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        formattedStatements.add(parenthesize("Print", stmt.expression));
        return null;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private Void format(List<Stmt> statements) {
        for (Stmt statement : statements) {
            statement.accept(this);
        }
        return null;
    }
}
