package httpserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main( String[] args ) {

        System.out.println( "Hello World!" );
        List<String> docRoot = new ArrayList<>();
        int port = 3000;
        List<String> argList = new ArrayList<>(Arrays.asList(args));

        //parse command line args
        if (argList.contains("--docRoot")) {

        } else {
            docRoot.add("static");
        }
        if (argList.contains("--port")) {

        } 
        System.out.println("Doc root is: " + docRoot);
        System.out.println("Port is " + port);


        //docRoot check
        for (String path: docRoot) {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)){
                System.out.println("Path does not exist: " + path);
                System.exit(1);
            } else if (!Files.isDirectory(filePath)) {
                System.out.println("Path is not a directory: " + path);
                System.exit(1);
            } else if (true) {
                System.out.println("Path is not readable: " + path);
                System.exit(1);
            }
        }

        //


    }
}
