package files.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation> simulations;
    private List<Thread> threads = new ArrayList<>();
    private ExecutorService threadPool;

    public SimulationEngine(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public void runSync() {
        for (Simulation simulation : simulations) {
            simulation.run();
        }
    }

    public void runAsync() {
        for (Simulation simulation : simulations) {
            Thread thread = new Thread(simulation);
            threads.add(thread);
            thread.start();
        }
        awaitSimulationEnd();
    }

    public void runAsyncInThreadPool() {
        threadPool = Executors.newFixedThreadPool(3);
        for (Simulation simulation : simulations) {
            threadPool.submit(simulation);
        }
        awaitSimulationEnd();
    }

    public void awaitSimulationEnd() {
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Nie wszystkie wątki zakończyły działanie w czasie!");
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("Oczekiwanie na zakończenie wątków zostało przerwane.");
                threadPool.shutdownNow();
            }
        } else {
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Thread join interrupted: " + e.getMessage());
            }
        }
    }

}