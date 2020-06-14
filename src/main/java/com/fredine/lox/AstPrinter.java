package com.fredine.lox;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

// Creates an unambiguous, if ugly, string representation of AST nodes.
class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<Void> {
    private int depth = 0;
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
    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize(expr.name.lexeme + " =", expr.value);
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
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        depth += 1;
        format(stmt.statements);
        depth -= 1;
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        add("Break");
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        add("Expr", stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        nested("if", stmt.condition, stmt.thenBranch);
        if (stmt.elseBranch != null) {
            nested("else", null, stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        add("Print", stmt.expression);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        add("var", new Expr.Variable(stmt.name), stmt.initializer);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        nested("while", stmt.condition, stmt.body);
        return null;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        addExpressions(builder, exprs);
        builder.append(")");
        return builder.toString();
    }

    private void addExpressions(StringBuilder builder, Expr...exprs) {
        for (Expr expr : exprs) {
            if (expr == null) {
                continue;
            }
            builder.append(" ");
            builder.append(expr.accept(this));
        }
    }

    private Void add(String name, Expr... exprs) {
        add(parenthesize(name, exprs));
        return null;
    }

    private Void add(String formatted) {
        formattedStatements.add(spaces(depth * 2) + formatted);
        return null;
    }

    private String spaces(int spaces) {
        return CharBuffer.allocate( spaces ).toString().replace( '\0', ' ' );
    }

    private void nested(String name, Expr condition, Stmt statement) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        if (condition != null) {
            addExpressions(builder, condition);
        }
        builder.append(" [");
        add(builder.toString());
        depth += 1;
        format(statement);
        depth -= 1;
        add("]");
    }

    private void format(List<Stmt> statements) {
        for (Stmt statement : statements) {
            format(statement);
        }
    }

    private void format(Stmt statement) {
        statement.accept(this);
    }
}
