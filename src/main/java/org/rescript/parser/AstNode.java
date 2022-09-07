package org.rescript.parser;

public class AstNode {

  private Statement stmt;

  private AstNode trueNode;

  private AstNode falseNode;

  public AstNode(Statement stmt, AstNode trueNode, AstNode falseNode) {
    super();
    this.stmt = stmt;
    this.trueNode = trueNode;
    this.falseNode = falseNode;
  }

  public Statement getStmt() {
    return stmt;
  }

  public AstNode next(boolean path) {
    return path?trueNode:falseNode;
  }

  public AstNode getTrueNode() {
    return trueNode;
  }

  public AstNode getFalseNode() {
    return falseNode;
  }

  @Override
  public String toString() {
    return "AstNode [stmt=" + stmt + "]";
  }

}
