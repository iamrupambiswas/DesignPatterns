public class Main {
    public static void main(String[] args) {
        TreeType Oak = TreeFactory.getTreeType("Oak", "Green");
        TreeType Pine = TreeFactory.getTreeType("Pine", "Dark Green");

        Tree tree1 = new Tree(10, 20, Oak);
        Tree tree2 = new Tree(30, 40, Pine);
        Tree tree3 = new Tree(50, 60, Oak); // Reusing the Oak TreeType
        Tree tree4 = new Tree(70, 80, Pine); // Reusing the Pine TreeType

        tree1.draw();
        tree2.draw();
        tree3.draw();
        tree4.draw();
    }
}
