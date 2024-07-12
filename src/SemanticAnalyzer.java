import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SemanticAnalyzer {
    private final List<Token> tokens;
    private int pos;
    private Token currentToken;
    private final Map<String, String> symbolTable;

    public SemanticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.symbolTable = new HashMap<>();
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = null;
        }
    }

    public void analyze() {
        while (currentToken != null) {
            switch (currentToken.type) {
                case "CONST":
                    handleConst();
                    break;
                case "IDENTIFIER":
                    handleIdentifier();
                    break;
                default:
                    advance();
                    break;
            }
        }
    }

    private void handleConst() {
        advance();
        if (currentToken.type.equals("IDENTIFIER")) {
            String varName = currentToken.value;
            advance();
            if (currentToken.type.equals("ASSIGN")) {
                advance();
                if (currentToken.type.equals("LPAREN")) {
                    handleFunctionDeclaration(varName);
                } else if (currentToken.type.equals("INTEGER") || currentToken.type.equals("IDENTIFIER")) {
                    symbolTable.put(varName, currentToken.type);
                    advance();
                } else {
                    throw new RuntimeException("Invalid assignment value for variable: " + varName);
                }
            } else {
                throw new RuntimeException("Expected '=' after 'const' declaration");
            }
        } else {
            throw new RuntimeException("Expected identifier after 'const'");
        }
    }

    private void handleFunctionDeclaration(String functionName) {
        advance();
        while (currentToken != null && !currentToken.type.equals("RPAREN")) {
            if (currentToken.type.equals("IDENTIFIER")) {
                String paramName = currentToken.value;
                advance();
                if (currentToken.type.equals("COLON")) {
                    advance();
                    if (currentToken.type.equals("INTEGER")) {
                        symbolTable.put(paramName, "INTEGER");
                        advance();
                        if (currentToken.type.equals("COMMA")) {
                            advance();
                        }
                    } else {
                        throw new RuntimeException("Expected type for parameter: " + paramName);
                    }
                } else {
                    throw new RuntimeException("Expected ':' after parameter name");
                }
            } else {
                throw new RuntimeException("Expected parameter name in function declaration");
            }
        }
        if (currentToken != null && currentToken.type.equals("RPAREN")) {
            advance();
            if (currentToken != null && currentToken.type.equals("ARROW")) {
                advance();
                if (currentToken != null && currentToken.type.equals("LBRACE")) {
                    advance();
                    while (currentToken != null && !currentToken.type.equals("RBRACE")) {
                        if (currentToken.type.equals("RETURN")) {
                            handleReturnStatement();
                        } else {
                            advance();
                        }
                    }
                    if (currentToken != null && currentToken.type.equals("RBRACE")) {
                        advance();
                    }
                } else {
                    throw new RuntimeException("Expected '{' after '=>'");
                }
            } else {
                throw new RuntimeException("Expected '=>' after function parameters");
            }
        } else {
            throw new RuntimeException("Expected ')' after function parameters");
        }
    }

    private void handleReturnStatement() {
        advance();
        if (currentToken.type.equals("IDENTIFIER") || currentToken.type.equals("INTEGER")) {
            advance();
            if (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS") ||
                    currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE")) {
                advance();
                if (currentToken.type.equals("IDENTIFIER") || currentToken.type.equals("INTEGER")) {
                    advance();
                } else {
                    throw new RuntimeException("Expected second operand in return statement");
                }
            }
        } else {
            throw new RuntimeException("Expected value after 'return'");
        }
    }

    private void handleIdentifier() {
        String varName = currentToken.value;
        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("Variable " + varName + " not declared");
        }
        advance();
        if (currentToken != null && currentToken.type.equals("ASSIGN")) {
            advance();
            if (currentToken.type.equals("INTEGER") || (currentToken.type.equals("IDENTIFIER") && symbolTable.containsKey(currentToken.value))) {
                advance();
            } else {
                throw new RuntimeException("Invalid assignment value for variable: " + varName);
            }
        }
    }
}
