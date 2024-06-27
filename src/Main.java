import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input = "const a = (un:integer, dos:integer) => { return un + dos; }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}