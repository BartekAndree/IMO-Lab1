import javax.swing.*;
import java.awt.*;
import java.util.List;

class CyclesVisualizer extends JPanel {
    private final List<int[]> coords;
    private final List<Integer> cycle1;
    private final List<Integer> cycle2;

    public CyclesVisualizer(List<int[]> coords, List<Integer> cycle1, List<Integer> cycle2) {
        this.coords = coords;
        this.cycle1 = cycle1;
        this.cycle2 = cycle2;
        setPreferredSize(new Dimension(400, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCycle(g, cycle1, Color.RED);
        drawCycle(g, cycle2, Color.BLUE);
    }

    private void drawCycle(Graphics g, List<Integer> cycle, Color color) {
        g.setColor(color);
        for (int i = 0; i < cycle.size(); i++) {
            int[] point1 = coords.get(cycle.get(i));
            int[] point2 = coords.get(cycle.get((i + 1) % cycle.size()));
            g.drawLine(point1[0] / 10, point1[1] / 10, point2[0] / 10, point2[1] / 10);
        }
    }

    public static void display(List<int[]> coords, List<Integer> cycle1, List<Integer> cycle2) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dual Cycle TSP Visualization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CyclesVisualizer(coords, cycle1, cycle2));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}