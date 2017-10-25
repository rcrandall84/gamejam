import javax.swing.*;

public class Screen extends JFrame{

    public Screen(int dimension) {
        JFrame frame = new JFrame();
        frame.setSize(dimension,dimension);
        frame.setVisible(true);
    }
}
