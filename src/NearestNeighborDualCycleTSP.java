import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NearestNeighborDualCycleTSP {
    private static final int EXPERIMENTS = 1_000_000;
    private static final String FILENAME = "src/kroB100.tsp";
    private static List<int[]> coords = new ArrayList<>();
    private static List<Integer> cycle1 = new ArrayList<>();
    private static List<Integer> cycle2 = new ArrayList<>();

    public static void main(String[] args) {
        List<Long> executionTimes = new ArrayList<>();
        try {
            loadFromFile(FILENAME);
            for (int i = 0; i < EXPERIMENTS; i++) {
                long startTime = System.currentTimeMillis();
                solveDualCycleTSP();
                long endTime = System.currentTimeMillis();
                executionTimes.add(endTime - startTime);
            }
            //CyclesVisualizer.display(coords, cycle1, cycle2);
            displayExecutionTimes(executionTimes);
        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas wczytywania pliku: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void solveDualCycleTSP() {
        int n = coords.size();
        boolean[] visited = new boolean[n];
        Random rand = new Random();

        int startNode1 = rand.nextInt(n);
        int startNode2;
        do {
            startNode2 = rand.nextInt(n);
        } while (startNode1 == startNode2);

        cycle1.add(startNode1);
        cycle2.add(startNode2);
        visited[startNode1] = true;
        visited[startNode2] = true;

        while (cycle1.size() < n / 2 || cycle2.size() < n / 2) {
            if (cycle1.size() < n / 2) {
                addNearestNeighborToCycle(cycle1, visited);
            }
            if (cycle2.size() < n / 2) {
                addNearestNeighborToCycle(cycle2, visited);
            }
        }
    }

    private static void addNearestNeighborToCycle(List<Integer> cycle, boolean[] visited) {
        int nearestNode = -1;
        int lastNode = cycle.get(cycle.size() - 1);
        double nearestDistance = Double.MAX_VALUE;
        for (int i = 0; i < coords.size(); i++) {
            if (!visited[i]) {
                double distance = calculateDistance(coords.get(lastNode), coords.get(i));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestNode = i;
                }
            }
        }
        if (nearestNode != -1) {
            cycle.add(nearestNode);
            visited[nearestNode] = true;
        }
    }

    private static double calculateDistance(int[] point1, int[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }

    public static void loadFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches("\\d+\\s+\\d+\\s+\\d+")) {
                    String[] parts = line.split("\\s+");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    coords.add(new int[]{x, y});
                }
            }
        }
    }
    private static void displayExecutionTimes(List<Long> executionTimes) {
        long sum = 0, min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (long time : executionTimes) {
            sum += time;
            if (time < min) {
                min = time;
            }
            if (time > max) {
                max = time;
            }
        }
        double average = sum / (double) executionTimes.size();
        System.out.println("Średni czas: " + average + " ms");
        System.out.println("Minimalny czas: " + min + " ms");
        System.out.println("Maksymalny czas: " + max + " ms");
    }
}