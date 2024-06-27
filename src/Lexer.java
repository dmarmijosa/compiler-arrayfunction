import java.util.ArrayList;
import java.util.List;

class Lexer {
    private final String input;
    private int pos;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.currentChar = input.charAt(pos);
    }

    private void advance() {
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0'; // Indicates end of input
        } else {
            currentChar = input.charAt(pos);
        }
    }

    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
            advance();
        }
    }

    private Token number() {
        StringBuilder result = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return new Token("INTEGER", result.toString());
    }

    private Token identifier() {
        StringBuilder result = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        String value = result.toString();
        switch (value) {
            case "const":
            case "integer":
            case "return":
                return new Token(value.toUpperCase(), value);
            default:
                return new Token("IDENTIFIER", value);
        }
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (Character.isDigit(currentChar)) {
                tokens.add(number());
                continue;
            }

            if (Character.isLetter(currentChar)) {
                tokens.add(identifier());
                continue;
            }

            switch (currentChar) {
                case '+':
                    tokens.add(new Token("PLUS", "+"));
                    advance();
                    break;
                case '-':
                    tokens.add(new Token("MINUS", "-"));
                    advance();
                    break;
                case '*':
                    tokens.add(new Token("MULTIPLY", "*"));
                    advance();
                    break;
                case '/':
                    tokens.add(new Token("DIVIDE", "/"));
                    advance();
                    break;
                case '(':
                    tokens.add(new Token("LPAREN", "("));
                    advance();
                    break;
                case ')':
                    tokens.add(new Token("RPAREN", ")"));
                    advance();
                    break;
                case '{':
                    tokens.add(new Token("LBRACE", "{"));
                    advance();
                    break;
                case '}':
                    tokens.add(new Token("RBRACE", "}"));
                    advance();
                    break;
                case ':':
                    tokens.add(new Token("COLON", ":"));
                    advance();
                    break;
                case ';':
                    tokens.add(new Token("SEMICOLON", ";"));
                    advance();
                    break;
                case ',':
                    tokens.add(new Token("COMMA", ","));
                    advance();
                    break;
                case '=':
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '>') {
                        tokens.add(new Token("ARROW", "=>"));
                        advance();
                        advance();
                    } else {
                        tokens.add(new Token("ASSIGN", "="));
                        advance();
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown character: " + currentChar);
            }
        }

        return tokens;
    }
}