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
    // add a neighbor to this cell, and this cell as a neighbor to the other
    void addNeighbor(Cell other) {
        if (!this.neighbors.contains(other)) { // avoid duplicates
            this.neighbors.add(other);
        }
        if (!other.neighbors.contains(this)) { // avoid duplicates
            other.neighbors.add(this);
        }
    }
    // used in updateGrid()
    boolean isCellBelowNeighbor() {
        return this.neighbors.contains(new Cell(this.x, this.y + 1));
    }
    // used in updateGrid()
    boolean isCellRightNeighbor() {
        return this.neighbors.contains(new Cell(this.x + 1, this.y));
    }
    // useful Cell representation
    @Override
    public String toString() {
        return String.format("Cell(%s, %s)", x, y);
    }
    // useful Cell equivalence
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cell)) return false;
        Cell otherCell = (Cell) other;
        return (this.x == otherCell.x && this.y == otherCell.y);
    }
    // should be overridden with equals
    @Override
    public int hashCode() {
        // random hash code method designed to be usually unique
        return this.x + this.y * 256;
    }
}