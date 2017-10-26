import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyMaze extends JPanel {
    private int dimensionX, dimensionY; // dimension of maze
    private int gridDimensionX, gridDimensionY; // dimension of output grid
    private char[][] grid; // output grid
    public Cell[][] cells; // 2d array of Cells
    private Random random = new Random(); // The random object

    // initialize with x and y the same 
    public MyMaze(int aDimension) {
        // Initialize
        this(aDimension, aDimension);
    }
    // constructor
    public MyMaze(int xDimension, int yDimension) {
        dimensionX = xDimension;
        dimensionY = yDimension;
        gridDimensionX = xDimension * 4 + 1;
        gridDimensionY = yDimension * 2 + 1;
        grid = new char[gridDimensionX][gridDimensionY];
        init();
        generateMaze();
    }

    private void init() {
        // create cells
        cells = new Cell[dimensionX][dimensionY];
        for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
                cells[x][y] = new Cell(x, y, false); // create cell (see Cell constructor)
            }
        }
    }

    // generate from upper left (In computing the y increases down often)
    private void generateMaze() {
        generateMaze(0, 0);
    }
    // generate the maze from coordinates x, y
    private void generateMaze(int x, int y) {
        generateMaze(getCell(x, y)); // generate from Cell
    }
    private void generateMaze(Cell startAt) {
        // don't generate from cell not there
        if (startAt == null) return;
        startAt.open = false; // indicate cell closed for generation
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(startAt);

        while (!cells.isEmpty()) {
            Cell cell;
            // this is to reduce but not completely eliminate the number
            //   of long twisting halls with short easy to detect branches
            //   which results in easy mazes
            if (random.nextInt(10)==0)
                cell = cells.remove(random.nextInt(cells.size()));
            else cell = cells.remove(cells.size() - 1);
            // for collection
            ArrayList<Cell> neighbors = new ArrayList<>();
            // cells that could potentially be neighbors
            Cell[] potentialNeighbors = new Cell[]{
                    getCell(cell.x + 1, cell.y),
                    getCell(cell.x, cell.y + 1),
                    getCell(cell.x - 1, cell.y),
                    getCell(cell.x, cell.y - 1)
            };
            for (Cell other : potentialNeighbors) {
                // skip if outside, is a wall or is not opened
                if (other==null || other.wall || !other.open) continue;
                neighbors.add(other);
            }
            if (neighbors.isEmpty()) continue;
            // get random cell
            Cell selected = neighbors.get(random.nextInt(neighbors.size()));
            // add as neighbor
            selected.open = false; // indicate cell closed for generation
            cell.addNeighbor(selected);
            cells.add(cell);
            cells.add(selected);
        }
    }
    // used to get a Cell at x, y; returns null out of bounds
    public Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException e) { // catch out of bounds
            return null;
        }
    }

    // get the projected distance
    // (A star algorithm consistent)
    public double getProjectedDistance(Cell current, double travelled, Cell end) {
        return travelled + Math.abs(current.x - end.x) +
                Math.abs(current.y - current.x);
    }

    // draw the maze
    public void updateGrid() {
        char backChar = ' ', wallChar = 'X', cellChar = ' ', pathChar = '*';
        // fill background
        for (int x = 0; x < gridDimensionX; x ++) {
            for (int y = 0; y < gridDimensionY; y ++) {
                grid[x][y] = backChar;
            }
        }
        // build walls
        for (int x = 0; x < gridDimensionX; x ++) {
            for (int y = 0; y < gridDimensionY; y ++) {
                if (x % 4 == 0 || y % 2 == 0)
                    grid[x][y] = wallChar;
            }
        }
        // make meaningful representation
        for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
                Cell current = getCell(x, y);
                int gridX = x * 4 + 2, gridY = y * 2 + 1;
                if (current.inPath) {
                    grid[gridX][gridY] = pathChar;
                    if (current.isCellBelowNeighbor())
                        if (getCell(x, y + 1).inPath) {
                            grid[gridX][gridY + 1] = pathChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        } else {
                            grid[gridX][gridY + 1] = cellChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        }
                    if (current.isCellRightNeighbor())
                        if (getCell(x + 1, y).inPath) {
                            grid[gridX + 2][gridY] = pathChar;
                            grid[gridX + 1][gridY] = pathChar;
                            grid[gridX + 3][gridY] = pathChar;
                        } else {
                            grid[gridX + 2][gridY] = cellChar;
                            grid[gridX + 1][gridY] = cellChar;
                            grid[gridX + 3][gridY] = cellChar;
                        }
                } else {
                    grid[gridX][gridY] = cellChar;
                    if (current.isCellBelowNeighbor()) {
                        grid[gridX][gridY + 1] = cellChar;
                        grid[gridX + 1][gridY + 1] = backChar;
                        grid[gridX - 1][gridY + 1] = backChar;
                    }
                    if (current.isCellRightNeighbor()) {
                        grid[gridX + 2][gridY] = cellChar;
                        grid[gridX + 1][gridY] = cellChar;
                        grid[gridX + 3][gridY] = cellChar;
                    }
                }
            }
        }
    }

    // simply prints the map
    public void paint(Graphics g) {
        updateGrid();
        BufferedImage wall=null, floor=null;
        try{
            wall = ImageIO.read(new File(getClass().getClassLoader().getResource("wall.png").getFile()));
            floor = ImageIO.read(new File(getClass().getClassLoader().getResource("floor.png").getFile()));
        }catch(IOException e){}
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                if(grid[x][y] == 'X'){
                    g.drawImage(wall,x*20,y*20,wall.getWidth()/10,wall.getHeight()/10,null);
                }else{
                    g.drawImage(floor,x*20,y*20,wall.getWidth()/10,wall.getHeight()/10,null);
                }
            }
        }
    }
    // forms a meaningful representation
    @Override
    public String toString() {
        updateGrid();
        String output = "";
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                output += grid[x][y];
            }
            output += "\n";
        }
        return output;
    }

    // run it
    public static void main(String[] args) {
        MyMaze maze = new MyMaze(5);
        JFrame frame = new JFrame();
        frame.setTitle("Maze");
        maze.setPreferredSize(new Dimension((5*4+1)*20,(5*2+1)*20));
        frame.add(maze);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        System.out.print(maze);
    }
}