package web;

import php.PHPParser;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    public String path,
                    filename,
                    extension,
                    dirname;
    public Object content = "";

    private File file;
    private boolean text;
    private int parsingLine;

    public Document(String path) throws FileNotFoundException {
        this.path = path;

        String[] pathParts = this.path.split("/");
        this.dirname = "";
        for (int i = 0; i < pathParts.length-1; i++) {
            this.dirname += pathParts[i] + "/";
        }

        this.filename = pathParts[pathParts.length - 1];
        this.extension = this.filename.contains(".") ? this.filename.substring(this.filename.lastIndexOf(".")+1) : "";
        this.text = Documents.isTextFile(this.extension);
        this.file = new File(this.path);

        if (!this.file.exists() || !this.file.isFile())
            throw new FileNotFoundException("File " + this.path + " either doesn't exist, or is directory ... ");
    }



    public boolean isText() {
        return text;
    }
    public Object read() {
        Object ret = null;

        if (this.isText()) {
            ret = "";

            try {
                FileReader fInput = new FileReader(this.path);
                try {
                    int c;
                    while ((c = fInput.read()) != -1)
                        ret += "" + (char) c;
                } catch (IOException e) {}
            } catch (FileNotFoundException e) {}
        } else {
            try {
                FileInputStream fInput = new FileInputStream(this.path);
                try {
                    ret = new byte[fInput.available()];
                    fInput.read((byte[]) ret);
                } catch (IOException e) {}
            } catch (FileNotFoundException e) {}
        }

        this.content = ret;
        if (this.extension.equalsIgnoreCase("php")) {
            this.parsePHP();
        }

        return this.content;
    }

    public void parsePHP() {
        Pattern phpBlocksPattern = Pattern.compile("<[?]php[\\s\\S]*?[?]>");
        Matcher phpBlocksMatcher = phpBlocksPattern.matcher((String) this.content);

        PHPParser parser = new PHPParser();

        while (phpBlocksMatcher.find()) {
            String tag = phpBlocksMatcher.group(),
                    code = tag.substring(5, tag.length()-2).trim();

            Pattern phpStatementsPattern = Pattern.compile("[\\s\\S]*?(?:;|\\{|\\})(?=(?:[^\"']*(?:\"|')[^\"']*(?:\"|'))*[^\"|']*$)");
            Matcher phpStatementsMatcher = phpStatementsPattern.matcher(code);

            String blockOutput = parser.parseCode(phpStatementsMatcher);
            this.content = ((String)this.content).replace(tag, blockOutput);
        }
    }


    public static Object readDocument (String path) throws FileNotFoundException {
        Document doc = new Document(path);
        return doc.read();
    }
}
