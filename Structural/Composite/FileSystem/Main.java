public class Main {

    public static void main(String[] args) {

        File file1 = new File("Resume.pdf");
        File file2 = new File("Photo.jpg");
        File file3 = new File("Notes.txt");

        Folder documents = new Folder("Documents");
        documents.add(file1);
        documents.add(file3);

        Folder pictures = new Folder("Pictures");
        pictures.add(file2);

        Folder root = new Folder("Root");

        root.add(documents);
        root.add(pictures);

        root.showDetails();
    }
}
