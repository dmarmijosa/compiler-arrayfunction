# Simple Compiler

Este es un compilador simple implementado en Java. El compilador consta de varias etapas: lexing, parsing y análisis semántico.

## Clases y Métodos

### Lexer

`Lexer` es responsable de convertir la entrada de texto en una secuencia de tokens.

- **Constructor**
    - `Lexer(Reader reader) throws IOException`: Inicializa el lexer con un objeto `Reader`.

- **Métodos**
    - `private void advance() throws IOException`: Avanza al siguiente carácter en la entrada.
    - `private void skipWhitespace() throws IOException`: Omite los caracteres de espacio en blanco.
    - `private Token number() throws IOException`: Procesa y retorna un token de tipo número.
    - `private Token identifier() throws IOException`: Procesa y retorna un token de tipo identificador.
    - `public Token nextToken() throws IOException`: Retorna el siguiente token de la entrada.

### Main

`Main` es la clase de entrada del programa que contiene el método `main`.

- **Métodos**
    - `public static void main(String[] args)`: Método principal que inicia el lexer y procesa una cadena de entrada.

### Parser

`Parser` es responsable de analizar la secuencia de tokens y construir una estructura sintáctica.

- **Constructor**
    - `Parser(Lexer lexer) throws IOException`: Inicializa el parser con un objeto `Lexer`.

- **Métodos**
    - `private void eat(TokenType type) throws IOException`: Consume el token actual si coincide con el tipo esperado.
    - `private int factor() throws IOException`: Procesa y retorna el valor de un factor.
    - `private int term() throws IOException`: Procesa y retorna el valor de un término.
    - `public int expression() throws IOException`: Procesa y retorna el valor de una expresión.

### SemanticAnalyzer

`SemanticAnalyzer` es responsable de realizar el análisis semántico de la estructura sintáctica.

- **Métodos**
    - `public void analyze(int value)`: Analiza el valor resultante del análisis sintáctico.

### Token

`Token` representa un token en la entrada.

- **Constructor**
    - `Token(TokenType type, String value)`: Inicializa un token con un tipo y un valor.

- **Métodos**
    - `public TokenType getType()`: Retorna el tipo del token.
    - `public String getValue()`: Retorna el valor del token.
    - `public String toString()`: Retorna una representación en cadena del token.

### TokenType

`TokenType` es una enumeración que define los diferentes tipos de tokens.

- **Valores**
    - `NUMBER`
    - `IDENTIFIER`
    - `PLUS`
    - `MINUS`
    - `STAR`
    - `SLASH`
    - `LPAREN`
    - `RPAREN`
    - `EOF`

## Ejecución

Para ejecutar el compilador, compile todas las clases y ejecute el método `main` en la clase `Main`.

```sh
javac *.java
java Main