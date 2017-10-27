import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyMaze extends JPanel implements KeyListener{
    private int cellSize = 50;
    private int dimensionX, dimensionY; // dimension of maze
    private int gridDimensionX, gridDimensionY; // dimension of output grid
    private char[][] grid; // output grid
    private Cell[][] cells; // 2d array of Cells
    private Random random = new Random(); // The random object
    private int playerPosX = cellSize+1;
    private int playerPosY = cellSize+1;
    private BufferedImage wall=null, floor=null, knight = null;
    private JFrame frame;


    private MyMaze(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int mazeWidth = (int) screenSize.getWidth()/cellSize - 1;
        int mazeHeight = (int) screenSize.getHeight()/cellSize - 1;
        new MyMaze(mazeWidth/2, mazeHeight/2);
    }
    private MyMaze(int xDimension, int yDimension) {
        dimensionX = xDimension;
        dimensionY = yDimension;
        gridDimensionX = xDimension * 2 + 1;
        gridDimensionY = yDimension * 2 + 1;
        grid = new char[gridDimensionX][gridDimensionY];
        init();
        generateMaze();
        loadImages();
        buildFrame();
    }

    private void loadImages() {
        try{
        assert false;
        wall = ImageIO.read(new File(getClass().getClassLoader().getResource("wall.png").getFile()));
        floor = ImageIO.read(new File(getClass().getClassLoader().getResource("floor.png").getFile()));
        knight = ImageIO.read(new File(getClass().getClassLoader().getResource("knight2.png").getFile()));
    }catch(IOException e){e.printStackTrace();}
    }

    private void buildFrame() {
        frame = new JFrame();
        frame.setTitle("Maze");
        this.setPreferredSize(new Dimension(gridDimensionX*cellSize,gridDimensionY*cellSize));
        frame.addKeyListener(this);
        frame.add(this);
        frame.setResizable(false);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.toFront();
        frame.setVisible(true);
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
        grid[gridDimensionX-1][gridDimensionY-2] = floorChar;
    }

    // simply prints the map
    public void paint(Graphics g) {
        updateGrid();
        drawMaze(g);
        drawObject(g);
    }

    private void drawObject(Graphics g) {
            g.drawImage(knight, playerPosX,playerPosY,cellSize-2,cellSize-2,null);
    }

    private void drawMaze(Graphics g) {
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                if(grid[x][y] == 'X'){
                    assert wall != null;
                    g.drawImage(wall,x*cellSize,y*cellSize,cellSize,cellSize,null);
                }else{
                    assert floor != null;
                    g.drawImage(floor,x*cellSize,y*cellSize,cellSize,cellSize,null);
                }
            }
        }
        g.drawImage(floor,0,cellSize,cellSize,cellSize,null);
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
        new MyMaze();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent){
        try{
        switch(keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:
                if(grid[playerPosX/cellSize][(playerPosY-cellSize)/cellSize] == 'X'){
                    break;
                }
                playerPosY -= cellSize;
                break;
            case KeyEvent.VK_DOWN:
                if(grid[playerPosX/cellSize][(playerPosY+cellSize)/cellSize] == 'X'){
                    break;
                }
                playerPosY += cellSize;
                break;
            case KeyEvent.VK_LEFT:
                if(grid[(playerPosX-cellSize)/cellSize][playerPosY/cellSize] == 'X'){
                    break;
                }
                playerPosX -= cellSize;
                break;
                case KeyEvent.VK_RIGHT :
                    if(grid[(playerPosX+cellSize)/cellSize][playerPosY/cellSize] == 'X'){
                    break;
                    }
                    playerPosX += cellSize;
                    break;
            case KeyEvent.VK_ESCAPE:
                System.exit(1);
            }
        }catch(ArrayIndexOutOfBoundsException ex){
            frame.dispose();
            new MyMaze();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {}
}