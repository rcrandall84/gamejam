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
    private Cell[][] cells; // 2d array of Cells
    private Random random = new Random(); // The random object

    // initialize with x and y the same 
    private MyMaze(int aDimension) {
        // Initialize
        this(aDimension, aDimension);
    }
    // constructor
    private MyMaze(int xDimension, int yDimension) {
        dimensionX = xDimension;
        dimensionY = yDimension;
        gridDimensionX = xDimension * 2 + 1;
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

    // generate the maze from coordinates x, y
    private void generateMaze() {
        generateMaze(getCell(0, 0)); // generate from Cell
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
    private Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException e) { // catch out of bounds
            return null;
        }
    }

    // draw the maze
    private void updateGrid() {
        char wallChar = 'X', floorChar = ' ';
        // fill background
        for (int x = 0; x < gridDimensionX; x ++) {
            for (int y = 0; y < gridDimensionY; y ++) {
                grid[x][y] = floorChar;
            }
        }
        // build walls
        for (int x = 0; x < gridDimensionX; x ++) {
            for (int y = 0; y < gridDimensionY; y ++) {
                if (x % 2 == 0 || y % 2 == 0) {
                    grid[x][y] = wallChar;
                }
            }
        }
        // make meaningful representation
        for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
                Cell current = getCell(x, y);
                int gridX = x * 2 + 1, gridY = y * 2 + 1;
                grid[gridX][gridY] = floorChar;
                assert current != null;
                if (current.isCellBelowNeighbor()) {
                    grid[gridX][gridY + 1] = floorChar;
                }
                if (current.isCellRightNeighbor()) {
                    grid[gridX + 2][gridY] = floorChar;
                    grid[gridX + 1][gridY] = floorChar;
                }
            }
        }
    }

    // simply prints the map
    public void paint(Graphics g) {
        updateGrid();
        BufferedImage wall=null, floor=null;
        try{
            assert false;
            wall = ImageIO.read(new File(getClass().getClassLoader().getResource("wall.png").getFile()));
            assert floor !=null;
            floor = ImageIO.read(new File(getClass().getClassLoader().getResource("floor.png").getFile()));
        }catch(IOException e){e.printStackTrace();}
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                if(grid[x][y] == 'X'){
                    assert wall != null;
                    g.drawImage(wall,x*20,y*20,wall.getWidth()/10,wall.getHeight()/10,null);
                }else{
                    assert floor != null;
                    g.drawImage(floor,x*20,y*20,floor.getWidth()/10,floor.getHeight()/10,null);
                }
            }
        }
    }
    // forms a meaningful representation
    @Override
    public String toString() {
        updateGrid();
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                output.append(grid[x][y]);
            }
            output.append("\n");
        }
        return output.toString();
    }

    // run it
    public static void main(String[] args) {
        MyMaze maze = new MyMaze(25);
        JFrame frame = new JFrame();
        frame.setTitle("Maze");
        maze.setPreferredSize(new Dimension((25*2+1)*20,(25*2+1)*20));
        frame.add(maze);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        System.out.print(maze);
    }
}