import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Scanner {

    private boolean endToken;

    //counts to find location of current constants and identifiers
    private int count;
    private int idCount;
    private int constCount;

    //list to store all tokens
    private ArrayList<Core> rawTokens;

    //lists to store the values of identifiers and constants
    private ArrayList<String> ids;
    private ArrayList<String> consts;

    // Initialize the scanner
    //scans the entire file and stores tokens for extraction
    Scanner(String filename) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            System.out.println("IOException. Could not open file");
            e.printStackTrace();
        }
        //the token currently being built
        StringBuilder stringTokenInProgress = new StringBuilder();
        StringBuilder constantTokenInProgress = new StringBuilder();

        //initialize the list for tokens
        this.rawTokens = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.consts = new ArrayList<>();

        this.count = 0;
        this.idCount = 0;
        this.constCount = 0;
		this.endToken = false;

        char c;
        int value;
        String tokenStr;
        String constantStr;

        try {
            while ((value = reader.read()) != -1) {
                c = (char) value;

                //Builds a String of letter characters
                while (Character.isLetter(c)) {
                    stringTokenInProgress.append(c);
                    c = (char) reader.read();
                    if (this.endToken) {
                        System.out.println(
                                "Error: END token has been reached. No more input "
                                        + "is valid");
                        break;
                    }
                }

                tokenStr = stringTokenInProgress.toString();
                stringTokenInProgress.setLength(0);

                //Checks if the string of letters is a keyword.  If not, it is
                //considered an identifier
                if (tokenStr.length() > 0) {
                    if (tokenStr.equals("program")) {
                        this.rawTokens.add(Core.PROGRAM);
                    } else if (tokenStr.equals("begin")) {
                        this.rawTokens.add(Core.BEGIN);
                    } else if (tokenStr.equals("end")) {
                        this.rawTokens.add(Core.END);
                        this.endToken = true;
                    } else if (tokenStr.equals("int")) {
                        this.rawTokens.add(Core.INT);
                    } else if (tokenStr.equals("input")) {
                        this.rawTokens.add(Core.INPUT);
                    } else if (tokenStr.equals("output")) {
                        this.rawTokens.add(Core.OUTPUT);
                    } else if (tokenStr.equals("if")) {
                        this.rawTokens.add(Core.IF);
                    } else if (tokenStr.equals("then")) {
                        this.rawTokens.add(Core.THEN);
                    } else if (tokenStr.equals("else")) {
                        this.rawTokens.add(Core.ELSE);
                    } else if (tokenStr.equals("or")){
			this.rawTokens.add(Core.OR);
		    } else if (tokenStr.equals("endif")) {
                        this.rawTokens.add(Core.ENDIF);
                    } else if (tokenStr.equals("while")) {
                        this.rawTokens.add(Core.WHILE);
                    } else if (tokenStr.equals("endwhile")) {
                        this.rawTokens.add(Core.ENDWHILE);
                    } else if (tokenStr.equals("endfunc")) {
                        this.rawTokens.add(Core.ENDFUNC);
                    } else if (tokenStr.equals("EOS")) {
                        this.rawTokens.add(Core.EOS);
                    } else {
                        this.rawTokens.add(Core.ID);
                        this.ids.add(tokenStr);
                    }

                }

                //Builds a string of digits
                while (Character.isDigit(c)) {
                    constantTokenInProgress.append(c);
                    c = (char) reader.read();
                    if (this.endToken) {
                        System.out.println(
                                "Error: END token has been reached, no other input is valid");
                        break;
                    }
                }

                constantStr = constantTokenInProgress.toString();
                constantTokenInProgress.setLength(0);

                //If the constant String has and value, the add a constant token
                if (constantStr.length() > 0) {
                    this.rawTokens.add(Core.CONST);
                    this.consts.add(constantStr);
                }
                //This is error checking for the case that the end program token
                //has already been found
                if (this.endToken && !Character.isWhitespace(c)) {
                    System.out.println(
                            "Error: END token has been reached, no other input is valid");
                    break;
                }

                //Checks if the symbol character is a Token.  If not send error
                //message
                if (c == ',') {
                    this.rawTokens.add(Core.COMMA);
                } else if (c == ';') {
                    this.rawTokens.add(Core.SEMICOLON);
                } else if (c == '(') {
                    this.rawTokens.add(Core.LPAREN);
                } else if (c == ')') {
                    this.rawTokens.add(Core.RPAREN);
                } else if (c == ':') {
                    if (this.peek(reader) == '=') {
                        this.rawTokens.add(Core.ASSIGN);
                    } else {
                        this.rawTokens.add(Core.EOS);
                        System.out.println(
                                "Error: " + c + " is not a valid input");
                    }
                } else if (c == '!') {
                    this.rawTokens.add(Core.NEGATION);
                } else if (c == '=') {
                    this.rawTokens.add(Core.EQUAL);
                } else if (c == '<') {
                    if (this.peek(reader) == '=') {
                        this.rawTokens.add(Core.LESSEQUAL);
                    } else {
                        this.goBack(reader);
                        this.rawTokens.add(Core.LESS);
                    }
                } else if (c == '+') {
                    this.rawTokens.add(Core.ADD);
                } else if (c == '*') {
                    this.rawTokens.add(Core.MULT);
                } else if (c == '-') {
                    this.rawTokens.add(Core.SUB);
                } else if (!Character.isWhitespace(c)) {
                    System.out.println("Error: " + c + " not a valid input");
                    this.rawTokens.add(Core.EOS);
                }

                tokenStr = "";
            }
        } catch (IOException e) {
            System.out.println("IOException. Could not read from file");
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            System.out.print("IOException. Could not close file.");
            e.printStackTrace();
        }

        //the file has reached the end
        this.rawTokens.add(Core.EOS);
    }

    // Advance to the next token
    public Core nextToken() {
        if (this.currentToken() == Core.ID) {
            this.idCount++;
        }
        if (this.currentToken() == Core.CONST) {
            this.constCount++;
        }

        this.count++;

        return this.rawTokens.get(this.count);
    }

    // Return the current token
    public Core currentToken() {
        return this.rawTokens.get(this.count);
    }

    //gets the associated value of the identifier
    public String getID() {
        String result = this.ids.get(this.idCount);
        return result;
    }

    //gets the associated value of a constant
    public int getCONST() {
        int result = Integer.parseInt(this.consts.get(this.constCount));
        return result;
    }

    //sets a mark to check for next char
    private char peek(BufferedReader reader) throws IOException {
        reader.mark(12);
        return (char) reader.read();
    }

    //goes back to mark if it's not what you're looking for
    private void goBack(BufferedReader reader) throws IOException {
        reader.reset();
    }

}
