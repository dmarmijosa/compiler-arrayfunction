import java.util.List;

class Parser {
    private final List<Token> tokens;
    private int pos;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = null;
        }
    }

    public void parse() {
        while (currentToken != null) {
            switch (currentToken.type) {
                case "CONST":
                    parseConst();
                    break;
                default:
                    throw new RuntimeException("Unexpected token: " + currentToken);
            }
        }
    }

    private void parseConst() {
        advance();
        if (currentToken.type.equals("IDENTIFIER")) {
            advance();
            if (currentToken.type.equals("ASSIGN")) {
                advance();
                parseExpression();
            } else {
                throw new RuntimeException("Expected '=' after identifier in const declaration");
            }
        } else {
            throw new RuntimeException("Expected identifier after 'const'");
        }
    }

    private void parseExpression() {
        if (currentToken.type.equals("LPAREN")) {
            parseFunction();
        } else if (currentToken.type.equals("INTEGER") || currentToken.type.equals("IDENTIFIER")) {
            advance();
        } else {
            throw new RuntimeException("Unexpected token in expression: " + currentToken);
        }
    }

    private void parseFunction() {
        advance();
        while (currentToken != null && !currentToken.type.equals("RPAREN")) {
            if (currentToken.type.equals("IDENTIFIER")) {
                advance();
                if (currentToken.type.equals("COLON")) {
                    advance();
                    if (currentToken.type.equals("INTEGER")) {
                        advance();
                        if (currentToken.type.equals("COMMA")) {
                            advance();
                        }
                    } else {
                        throw new RuntimeException("Expected type after ':' in parameter declaration");
                    }
                } else {
                    throw new RuntimeException("Expected ':' after parameter name in function declaration");
                }
            } else {
                throw new RuntimeException("Expected parameter name in function declaration");
            }
        }
        if (currentToken != null && currentToken.type.equals("RPAREN")) {
            advance(); // Skip ')'
            if (currentToken != null && currentToken.type.equals("ARROW")) {
                advance(); // Skip '=>'
                if (currentToken != null && currentToken.type.equals("LBRACE")) {
                    advance(); // Skip '{'
                    while (currentToken != null && !currentToken.type.equals("RBRACE")) {
                        if (currentToken.type.equals("RETURN")) {
                            parseReturnStatement();
                        } else {
                            advance(); // Skip any other content for simplicity
                        }
                    }
                    if (currentToken != null && currentToken.type.equals("RBRACE")) {
                        advance(); // Skip '}'
                    } else {
                        throw new RuntimeException("Expected '}' at end of function");
                    }
                } else {
                    throw new RuntimeException("Expected '{' after '=>' in function declaration");
                }
            } else {
                throw new RuntimeException("Expected '=>' after parameters in function declaration");
            }
        } else {
            throw new RuntimeException("Expected ')' after parameters in function declaration");
        }
    }

    private void parseReturnStatement() {
        advance();
        parseExpression();
        if (currentToken != null && (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS") ||
                currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE"))) {
            advance();
            parseExpression();
        }
    }
}