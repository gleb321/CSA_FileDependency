import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var path = new Scanner(System.in).nextLine();
        var root = new File(path);
        var dependencyGraphCreator = new DependencyGraphCreator();
        var graph = dependencyGraphCreator.create(root);
    }
}