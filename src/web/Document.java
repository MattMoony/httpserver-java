package web;

import java.io.*;

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
        this.parsingLine = 0;
        String[] parts = this.content.toString().split("<\\?php");

        for (int i = 1; i < parts.length; i++) {
            String code = parts[i].substring(0, parts[i].contains("?>") ? parts[i].indexOf("?>") : parts[i].length());
            parts[i] = parts[i].substring(parts[i].contains("?>") ? parts[i].indexOf("?>")+2 : parts[i].length() - 1);

            // evaluate "code" ...
            code = code.trim();
        }
        this.content = String.join("", parts);
    }


    public static Object readDocument (String path) throws FileNotFoundException {
        Document doc = new Document(path);
        return doc.read();
    }
}
