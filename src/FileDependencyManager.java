import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileDependencyManager {
    public List<String> getOrderedFilesList(AbstractMap<String, List<String>> dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        if (dependencyIsCyclic()) {
            throw new IllegalArgumentException("Dependency graph contains cycle");
        }

        orderedFiles = new ArrayList<>();
        for (var key : dependencyGraph.keySet()) {
            used.put(key, 0);
        }

        for (var key : dependencyGraph.keySet()) {
            if (used.get(key) != 0) {
                continue;
            }

            dfs(key);
        }

        return orderedFiles;
    }

    public List<String> getCycledFilesList(AbstractMap<String, List<String>> dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        if (!dependencyIsCyclic()) {
            throw new IllegalArgumentException("Dependency graph does not contain cycle");
        }

        var current = cycleEnd;
        var cycledFiles = new ArrayList<String>();
        while (!current.equals(cycleBegin)) {
            cycledFiles.add(current);
            current = path.get(current);
        }

        cycledFiles.add(cycleBegin);
        Collections.reverse(cycledFiles);
        return cycledFiles;
    }

    private String cycleBegin, cycleEnd;
    private List<String> orderedFiles;
    private AbstractMap<String, Integer> used;
    private AbstractMap<String, String> path;
    private AbstractMap<String, List<String>> dependencyGraph;

    private void dfs(String file) {
        used.put(file, 1);
        for (var dependency : dependencyGraph.get(file)) {
            if (used.get(dependency) == 1) {
                continue;
            }

            dfs(dependency);
        }

        orderedFiles.add(file);
    }

    private boolean dependencyIsCyclic() {
        used = new HashMap<>();
        for (var key : dependencyGraph.keySet()) {
            used.put(key, 0);
        }

        for (var key : dependencyGraph.keySet()) {
            if (used.get(key) != 0) {
                continue;
            }

            path = new HashMap<>();
            if (dependencyIsCyclic(key)) {
                return true;
            }
        }

        return false;
    }

    private boolean dependencyIsCyclic(String file) {
        used.put(file, 1);
        for (var dependency : dependencyGraph.get(file)) {
            if (used.get(dependency) == 1) {
                cycleBegin = dependency;
                cycleEnd = file;
                return true;
            }

            if (used.get(dependency) == 2) {
                continue;
            }

            path.put(dependency, file);
            if (dependencyIsCyclic(dependency)) {
                return true;
            }
        }

        used.put(file, 2);
        return false;
    }
}
