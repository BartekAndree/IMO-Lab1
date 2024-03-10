import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreedyCycleWithRegretDualCycleTSP {
    private static final int EXPERIMENTS = 1_000_000;
    private static final String FILENAME = "src/kroB100.tsp";
    private static List<int[]> coords = new ArrayList<>();
    private static List<Integer> cycle1 = new ArrayList<>();
    private static List<Integer> cycle2 = new ArrayList<>();

    public static void main(String[] args) {
        List<Long> executionTimes = new ArrayList<>();
        try {
            loadFromFile(FILENAME);
            for (int i = 0; i < EXPERIMENTS; i++){
                long startTime = System.currentTimeMillis();
                solveDualCycleGreedyCycleWithRegret();
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

    private static void solveDualCycleGreedyCycleWithRegret() {
        int n = coords.size();
        boolean[] visited = new boolean[n];
        Random rand = new Random();

        initializeCycle(cycle1, visited, rand.nextInt(n));
        initializeCycle(cycle2, visited, rand.nextInt(n));

        while (cycle1.size() < n / 2 || cycle2.size() < n / 2) {
            if (cycle1.size() < n / 2) {
                insertByRegret(cycle1, visited);
            }
            if (cycle2.size() < n / 2) {
                insertByRegret(cycle2, visited);
            }
        }
    }

    private static void initializeCycle(List<Integer> cycle, boolean[] visited, int startIndex) {
        cycle.add(startIndex);
        visited[startIndex] = true;
        int nearestNeighborIndex = findNearestNeighbor(startIndex, visited);
        if (nearestNeighborIndex != -1) {
            cycle.add(nearestNeighborIndex);
            visited[nearestNeighborIndex] = true;
        }
    }

    private static void insertByRegret(List<Integer> cycle, boolean[] visited) {
        int n = coords.size();
        double maxRegret = -1;
        int bestIndex = -1;
        int bestInsertPosition = -1;

        for (int newIndex = 0; newIndex < n; newIndex++) {
            if (!visited[newIndex]) {
                double bestIncrease = Double.MAX_VALUE;
                double secondBestIncrease = Double.MAX_VALUE;
                int bestPositionForNewIndex = -1;

                for (int insertPosition = 0; insertPosition < cycle.size(); insertPosition++) {
                    double increase = calculateInsertionCost(cycle, newIndex, insertPosition);
                    if (increase < bestIncrease) {
                        secondBestIncrease = bestIncrease;
                        bestIncrease = increase;
                        bestPositionForNewIndex = insertPosition;
                    } else if (increase < secondBestIncrease) {
                        secondBestIncrease = increase;
                    }
                }

                double regret = secondBestIncrease - bestIncrease;
                if (regret > maxRegret) {
                    maxRegret = regret;
                    bestIndex = newIndex;
                    bestInsertPosition = bestPositionForNewIndex;
                }
            }
        }

        if (bestIndex != -1) {
            cycle.add(bestInsertPosition, bestIndex);
            visited[bestIndex] = true;
        }
    }

    private static double calculateInsertionCost(List<Integer> cycle, int newIndex, int position) {
        int prevIndex = cycle.get(position);
        int nextIndex = cycle.get((position + 1) % cycle.size());
        double costBefore = calculateDistance(coords.get(prevIndex), coords.get(nextIndex));
        double costAfter = calculateDistance(coords.get(prevIndex), coords.get(newIndex))
                + calculateDistance(coords.get(newIndex), coords.get(nextIndex));
        return costAfter - costBefore;
    }

    private static int findNearestNeighbor(int index, boolean[] visited) {
        int nearestIndex = -1;
        double nearestDistance = Double.MAX_VALUE;
        for (int i = 0; i < coords.size(); i++) {
            if (!visited[i]) {
                double distance = calculateDistance(coords.get(index), coords.get(i));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestIndex = i;
                }
            }
        }
        return nearestIndex;
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
