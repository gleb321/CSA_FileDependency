import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Enter absolute path to root directory: ");
        var root = new File(new Scanner(System.in).nextLine());
        var fileDependencyManager = new FileDependencyManager();
        var dependencyGraphCreator = new DependencyGraphCreator();
        var graph = dependencyGraphCreator.create(root);
        try {
            concatenateFiles(root, fileDependencyManager.getOrderedFilesList(graph));
        } catch (IllegalArgumentException exception) {
            System.out.printf("%s:\n", exception.getMessage());
            for (var file : fileDependencyManager.getCycledFilesList(graph)) {
                System.out.printf("%s -> ", file.substring(root.getAbsolutePath().length() + 1));
            }

            System.out.print("...");
        }
    }

    private static void concatenateFiles(File root, List<String> files) {
        System.out.println("Ordered files list:");
        var resultFile = new File(root, "result.txt");
        try (var writer = new BufferedWriter(new FileWriter(resultFile))) {
            for (var file : files) {
                try (var reader = new BufferedReader(new FileReader(file))) {
                    var input = reader.readLine();
                    while (input != null) {
                        writer.write(input);
                        writer.newLine();
                        input = reader.readLine();
                    }
                    System.out.println(file.substring(root.getAbsolutePath().length() + 1));
                } catch (FileNotFoundException exception) {
                    System.out.printf("File %s was not found\n", file);
                } catch (IOException exception) {
                    System.out.printf("Impossible to read file %s\n", file);
                }
            }
        } catch (IOException exception) {
            System.out.printf("Impossible to create file %s\n", resultFile.getAbsolutePath());
        }

        System.out.printf("\nResult of file concatenation was written to file %s", resultFile);
    }
}