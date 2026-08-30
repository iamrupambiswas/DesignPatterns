import java.util.*;

public class Folder implements FileSystemComponent {

    private String name;
    private List<FileSystemComponent> children = new ArrayList<>();

    public Folder(String name) {
        this.name=name;
    }

    public void add(FileSystemComponent child) {
        children.add(child);
    }

    public void remove(FileSystemComponent child) {
        children.remove(child);
    }

    @Override
    public void showDetails() {
        System.out.println("Folder : " + name);

        for(FileSystemComponent child: children) {
            child.showDetails();
        }
    }
    
}
