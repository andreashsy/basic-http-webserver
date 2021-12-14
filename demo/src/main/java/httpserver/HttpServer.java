package httpserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpServer {
    public boolean docRootCheck (List<String> docRoot) {
        for (String path: docRoot) {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)){
                System.out.println("Path does not exist: " + path);
                System.exit(1);
                return false;
            } else if (!Files.isDirectory(filePath)) {
                System.out.println("Path is not a directory: " + path);
                System.exit(1);
                return false;
            } else if (false) { // TODO: path readable check
                System.out.println("Path is not readable: " + path);
                System.exit(1);
                return false;
            }
        }
    return true;
    }    
    
}
