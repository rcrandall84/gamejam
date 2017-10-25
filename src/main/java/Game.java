import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JPanel implements ActionListener, KeyListener {

    private boolean upPressed    = false;
    private boolean downPressed  = false;
    private boolean rightPressed = false;
    private boolean leftPressed  = false;

    private final int playerDiam = 100;
    private final int playerSpeed = 15;
    private final int tileSize = 400;

    private int[][] maze = {{1, 1, 1, 1, 1, 1},
            {1, 2, 1, 1, 3, 1},
            {1, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1},
    };
    private int[][] initX = new int[maze.length][maze.length];
    private int[][] initY = new int[maze.length][maze.length];

    private int deltaX = -210;
    private int deltaY = -210;

    private String screen = "menu";


    public Game() {
        setFocusable(true);
        addKeyListener(this);
        setUpInitialCoordinates();
        Timer timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tick();
    }

    private void setUpInitialCoordinates() {
        int x = 0;
        int y;
        for (int[] rowData : maze) {
            y = 0;
            for (int ignored : rowData) {
                initX[x][y] = x * tileSize;
                initY[x][y] = y * tileSize;
                y++;
            }
            x++;
        }
    }

    private void generateMaze() {
    }

    private void tick() {
        if (screen.equals("playing")) {
            if (upPressed) {
                deltaY += playerSpeed;
            } else if (downPressed) {
                deltaY -= playerSpeed;
            }
            if (rightPressed) {
                deltaX -= playerSpeed;
            } else if (leftPressed) {
                deltaX += playerSpeed;
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() ==   KeyEvent.VK_W) {
            upPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            rightPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            leftPressed = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (screen.equals("menu") && e.getKeyCode() == KeyEvent.VK_ENTER) {
            upPressed = false;
            downPressed = false;
            rightPressed = false;
            leftPressed = false;
            screen = "playing";
        } else if (screen.equals("playing")) {
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() ==    KeyEvent.VK_W) {
                upPressed = false;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode()   == KeyEvent.VK_S) {
                downPressed = false;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                rightPressed = false;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                leftPressed = false;
            } else if (e.getKeyCode() == KeyEvent.VK_P) {
                screen = "paused";
            }
        } else if (screen.equals("paused" ) && e.getKeyCode() ==     KeyEvent.VK_P) {
            upPressed = false;
            downPressed = false;
            rightPressed = false;
            leftPressed = false;
            screen = "playing";
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Aharoni", Font.PLAIN, 36));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        switch (screen) {
            case "menu":
                g.setColor(Color.BLACK);
                g.drawString("Labyrinth", 300, 200);
                g.drawString("Press Enter to Play!", getWidth() / 3, 500);
                break;
            case "playing":
                int x = 0;
                int y = 0;

                for (int[] rowData : maze) {
                    for (int cellData : rowData) {
                        if (cellData == 1) {
                            g.setColor(Color.DARK_GRAY);
                            g.fillRect(x + deltaX, y + deltaY, tileSize, tileSize);
                        } else if (cellData == 2) {
                            g.setColor(Color.GREEN);
                            g.fillRect(x + deltaX, y + deltaY, tileSize, tileSize);
                        } else if (cellData == 3) {
                            g.setColor(Color.YELLOW);
                            g.fillRect(x + deltaX, y + deltaY, tileSize,   tileSize);
                        }
                        x += tileSize;
                        if (x == maze.length * tileSize) {
                            x = 0;
                            y += tileSize;
                        }
                    }
                }   g.setColor(Color.RED);
                g.fillOval(getWidth() / 2, getHeight() / 2, playerDiam, playerDiam);
                break;
            case "gameOver":
                g.setColor(Color.BLACK);
                g.drawString("Game Over",getWidth() / 3 ,50 );
                break;
            case "paused":
                g.setColor(Color.BLACK);
                g.drawString("Paused", getWidth() / 3, 50);
                break;
        }
    }
}