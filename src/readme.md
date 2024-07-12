El Lexer se conecta al SemanticAnalyzer en el flujo del programa mediante el análisis de la cadena de entrada y la generación de una lista de tokens, que luego se pasa al SemanticAnalyzer para la verificación semántica. Aquí está el paso a paso de cómo se hace esto:

Conexión del Lexer y el SemanticAnalyzer en el Main
Analiza la cadena de entrada: El Lexer toma una cadena de entrada que contiene el código fuente y la convierte en una lista de tokens.
Pasa los tokens al SemanticAnalyzer: La lista de tokens generada por el Lexer se pasa al SemanticAnalyzer.
Realiza el análisis semántico: El SemanticAnalyzer realiza comprobaciones en la lista de tokens para verificar que el código es válido según las reglas del lenguaje.
Ejemplo Completo
Lexer.java
Este archivo se encarga del análisis léxico, es decir, de convertir una cadena de entrada en una lista de tokens.

java
Copiar código
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
Token.java
Este archivo define la estructura de un token.

java
Copiar código
class Token {
public final String type;
public final String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s)", type, value);
    }
}
SemanticAnalyzer.java
Este archivo se encarga del análisis semántico, es decir, de verificar que los tokens tienen sentido dentro del contexto del lenguaje.

java
Copiar código
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
            currentToken = null; // End of token stream
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
        advance(); // Skip 'const'
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
        advance(); // Skip '('
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
                            advance(); // Skip ','
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
            advance(); // Skip ')'
            if (currentToken != null && currentToken.type.equals("ARROW")) {
                advance(); // Skip '=>'
                if (currentToken != null && currentToken.type.equals("LBRACE")) {
                    advance(); // Skip '{'
                    while (currentToken != null && !currentToken.type.equals("RBRACE")) {
                        if (currentToken.type.equals("RETURN")) {
                            handleReturnStatement();
                        } else {
                            advance(); // Skip any other content for simplicity
                        }
                    }
                    if (currentToken != null && currentToken.type.equals("RBRACE")) {
                        advance(); // Skip '}'
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
        advance(); // Skip 'return'
        if (currentToken.type.equals("IDENTIFIER") || currentToken.type.equals("INTEGER")) {
            advance(); // Skip return value
            if (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS") ||
                    currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE")) {
                advance(); // Skip operator
                if (currentToken.type.equals("IDENTIFIER") || currentToken.type.equals("INTEGER")) {
                    advance(); // Skip second operand
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
        if (currentToken !=




continia escribiendo
ChatGPT
Claro, continúo escribiendo el resto del método handleIdentifier y el código de Main.java para completar la explicación.

Método handleIdentifier()
java
Copiar código
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
Explicación: Este método maneja el uso de identificadores, verificando que las variables estén declaradas antes de su uso. Si una variable no está declarada, lanza una excepción. Si una variable está siendo asignada, verifica que el valor de asignación sea válido (un número entero o una variable previamente declarada).
Main.java
El archivo Main.java conecta el lexer con el analizador semántico. A continuación se muestra cómo se hace esto.

java
Copiar código
import java.util.List;

public class Main {
public static void main(String[] args) {
// Cadena de entrada que contiene el código fuente a analizar
String input = "const a = (un:integer, dos:integer) => { return un + dos; }";

        // Crear una instancia del lexer con la cadena de entrada
        Lexer lexer = new Lexer(input);
        
        // Obtener la lista de tokens generada por el lexer
        List<Token> tokens = lexer.tokenize();

        // Imprimir los tokens generados
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // Crear una instancia del analizador semántico con la lista de tokens
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
        
        // Realizar el análisis semántico
        try {
            semanticAnalyzer.analyze();
            System.out.println("Semantic analysis passed.");
        } catch (RuntimeException e) {
            System.err.println("Semantic analysis error: " + e.getMessage());
        }
    }
}
Explicación Completa del Flujo
Cadena de entrada: El Main.java define una cadena de entrada que contiene el código fuente a analizar.
Lexer: Se crea una instancia del Lexer con la cadena de entrada.
Tokenización: Se llama al método tokenize() del Lexer para obtener la lista de tokens.
Imprimir Tokens: Los tokens generados se imprimen en la consola para verificación.
SemanticAnalyzer: Se crea una instancia del SemanticAnalyzer con la lista de tokens.
Análisis Semántico: Se llama al método analyze() del SemanticAnalyzer para realizar el análisis semántico.
Manejo de const: Verifica que las declaraciones const sean válidas.
Declaración de Funciones: Maneja la declaración de funciones y la verificación de parámetros.
Declaraciones de Retorno: Verifica que las declaraciones de retorno dentro de las funciones sean válidas.
Identificadores: Verifica que las variables estén declaradas antes de su uso y que las asignaciones sean válidas.
Si se detecta un error durante el análisis semántico, se lanza una excepción y se imprime un mensaje de error.
