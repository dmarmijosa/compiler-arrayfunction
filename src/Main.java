import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input = "const a = (1uno:integer, dos:integer) => { return un + dos; }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        try {
            parser.parse();
            System.out.println("Syntax analysis passed.");

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
            try {
                semanticAnalyzer.analyze();
                System.out.println("Semantic analysis passed.");
            } catch (RuntimeException e) {
                System.err.println("Semantic analysis error: " + e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println("Syntax analysis error: " + e.getMessage());
        }
    }
}