package httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpServer {
    public static boolean docRootCheck (List<String> docRoot) {
        for (String path: docRoot) {
            Path filePath = Paths.get(path);
            // checks if file exists
            if (!Files.exists(filePath)){
                System.out.println("Path does not exist: " + path);
                System.exit(1);
                return false;
            // checks if file is directory
            } else if (!Files.isDirectory(filePath)) {
                System.out.println("Path is not a directory: " + path);
                System.exit(1);
                return false;
            // checks if server can read the path by creating then deleting a file
            } else { 
                File testFile = new File(path + "\\" + "test_12342234.txt");
                try {
                    testFile.createNewFile();
                    if (!testFile.delete()) {
                        System.out.println("Path is not readable: " + path);
                        System.exit(1);
                        return false;
                    }
                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
                
            }
        }
    System.out.println("Server startup test passed!");
    return true;
    }    
    
}
