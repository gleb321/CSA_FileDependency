import java.io.*;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DependencyGraphCreator {
    public AbstractMap<String, List<String>> create(File root) {
        this.root = root;
        dependencyGraph = new HashMap<>();
        fillDependencyGraph(root);
        return dependencyGraph;
    }

    private File root;
    private AbstractMap<String, List<String>> dependencyGraph;

    private void fillDependencyGraph(File directory) {
        var files = directory.listFiles();
        if (files == null) {
            return;
        }

        var subDirectories = new ArrayList<File>();
        for (var file : files) {
            if (file.isDirectory()) {
                subDirectories.add(file);
                continue;
            }

            if (!file.isFile() || file.isHidden()) {
                continue;
            }

            dependencyGraph.computeIfAbsent(file.getAbsolutePath(), key -> new ArrayList<>());
            for (var requirement : getAllFileRequirements(file)) {
                dependencyGraph.computeIfAbsent(requirement, key -> new ArrayList<>());
                dependencyGraph.get(file.getAbsolutePath()).add(requirement);
            }
        }

        for (var subDirectory : subDirectories) {
            fillDependencyGraph(subDirectory);
        }
    }

    private List<String> getAllFileRequirements(File file) {
        var files = new ArrayList<String>();
        try (var reader = new BufferedReader(new FileReader(file))) {
            var input = reader.readLine();
            while (input != null) {
                var words = input.split("[‘'’]");
                if (words.length < 2 || !words[0].startsWith("require")) {
                    input = reader.readLine();
                    continue;
                }

                var localPath = words[1];
                files.add(Paths.get(root.getAbsolutePath(), localPath).toString());
                input = reader.readLine();
            }
        } catch (FileNotFoundException exception) {
            System.out.printf("File %s was not found\n", file.getAbsolutePath());
        } catch (IOException exception) {
            System.out.printf("Impossible to read file %s\n", file.getAbsolutePath());
        }

        return files;
    }
}
