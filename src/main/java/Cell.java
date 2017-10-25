import java.util.ArrayList;

public class Cell {
    int x, y; // coordinates
    // cells this cell is connected to
    ArrayList<Cell> neighbors = new ArrayList<>();
    // solver: if already used
    boolean visited = false;
    // solver: the Cell before this one in the path
    Cell parent = null;
    // solver: if used in last attempt to solve path
    boolean inPath = false;
    // solver: distance travelled this far
    double travelled;
    // solver: projected distance to end
    double projectedDist;
    // impassable cell
    boolean wall = true;
    // if true, has yet to be used in generation
    boolean open = true;

    // construct Cell at x, y
    Cell(int x, int y) {
        this(x, y, true);
    }

    // construct Cell at x, y and with whether it isWall
    Cell(int x, int y, boolean isWall) {
        this.x = x;
        this.y = y;
        this.wall = isWall;
    }
}
// add a neighbor to this cell, and this cell as a neighbor to the other