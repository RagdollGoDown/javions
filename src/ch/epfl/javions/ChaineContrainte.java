package ch.epfl.javions;import java.util.regex.Pattern;

public class ChaineContrainte {
    private String string;

    public ChaineContrainte(String string, String regex, boolean forceAcceptEmpty){
        if (forceAcceptEmpty && string.equals("")){
            this.string = "";
        }else{
            if (!testRegex(string, regex)) throw new IllegalArgumentException();
            this.string = string;
        }

    }
    public ChaineContrainte(String string, String regex){
        if (!testRegex(string, regex)) throw new IllegalArgumentException();
        this.string = string;
    }
    private static boolean testRegex(String string, String regex){
        return Pattern.matches(regex, string);
    }
    public String toString() {
        return string;
    }
}
