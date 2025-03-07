import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// FileNode Class - Represents files and folders
class FileNode {
    String name;
    boolean isDirectory;
    List<FileNode> children;

    // Constructor
    FileNode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

    // Method to add children (files or directories)
    void addChild(FileNode child) {
        if (this.isDirectory) {
            this.children.add(child);
        }
    }
}

// EventListener Interface
interface FileSearchListener {
    void onFileFound(String filePath);
}

// FileSearcher Class - Handles file searching with recursion and event triggering
class FileSearcher {
    private FileSearchListener listener;  // Event listener
    private String extension;  // File extension to search for
    private FileWriter writer; // File writer to write the results

    // Constructor
    FileSearcher(FileSearchListener listener, String extension) throws IOException {
        this.listener = listener;
        this.extension = extension;
        this.writer = new FileWriter("search_results.txt", true);  // Appends to the result file
    }

    // Recursive method to search through the directory
    public void search(FileNode node, String path) throws IOException {
        // If it's a directory, we recurse through its children
        if (node.isDirectory) {
            for (FileNode child : node.children) {
                search(child, path + "\\" + node.name);
            }
        } else {
            // If it's a file, check if it matches the given extension
            if (node.name.endsWith(extension)) {
                String filePath = path + "\\" + node.name;
                // Trigger the event
                listener.onFileFound(filePath);
                // Save the result to the output file
                writer.write(filePath + "\n");
            }
        }
    }

    // Close the writer after search
    public void closeWriter() throws IOException {
        writer.close();
    }
}

// Main FileSearch Class - Handles user input and invokes the search
public class FileSearch implements FileSearchListener {

    public static void main(String[] args) throws IOException {
        // Create the mock folder structure
        FileNode rootFolder = new FileNode("Documents", true);
        FileNode file1 = new FileNode("notes.txt", false);
        FileNode file2 = new FileNode("todo.txt", false);
        FileNode subfolder = new FileNode("subfolder", true);
        FileNode file3 = new FileNode("shopping_list.txt", false);
        FileNode file4 = new FileNode("image.png", false);

        rootFolder.addChild(file1);
        rootFolder.addChild(file2);
        rootFolder.addChild(subfolder);
        subfolder.addChild(file3);
        subfolder.addChild(file4);

        // Prompt user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter directory path: ");
        String path = scanner.nextLine();
        System.out.print("Enter file extension to search for: ");
        String extension = scanner.nextLine();

        // Create FileSearcher with event listener
        FileSearcher fileSearcher = new FileSearcher(new FileSearch(), extension);

        // Start searching
        System.out.println("Searching...");
        fileSearcher.search(rootFolder, path);

        // Close the file writer
        fileSearcher.closeWriter();

        System.out.println("Search completed. Results saved to search_results.txt.");
    }

    // This method is called when a file is found
    @Override
    public void onFileFound(String filePath) {
        System.out.println("File found: " + filePath);
    }
}
