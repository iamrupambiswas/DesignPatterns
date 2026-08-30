import java.util.*;

public class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color) {
        String key = name + "-" + color;

        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color));
            System.out.println("Creating new TreeType: " + name + " with color: " + color);
        }

        return treeTypes.get(key);
    }
}
