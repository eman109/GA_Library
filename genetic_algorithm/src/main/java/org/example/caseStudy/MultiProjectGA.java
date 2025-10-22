package org.example.caseStudy;

import org.example.core.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MultiProjectGA extends JFrame {

    // === GUI Components ===
    private JTextField populationField, generationsField, crossoverRateField, mutationRateField;
    private JComboBox<String> chromTypeBox, mutBox, crossBox, selectBox, replaceBox;
    private JTextArea outputArea;
    private JButton runButton;

    public MultiProjectGA() {
        setTitle("Genetic Algorithm - Project Selector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // === Input Panel ===
        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));

        panel.add(new JLabel("Population Size:"));
        populationField = new JTextField("10");
        panel.add(populationField);

        panel.add(new JLabel("Number of Generations:"));
        generationsField = new JTextField("20");
        panel.add(generationsField);

        panel.add(new JLabel("Crossover Rate (0-1):"));
        crossoverRateField = new JTextField("0.8");
        panel.add(crossoverRateField);

        panel.add(new JLabel("Mutation Rate (0-1):"));
        mutationRateField = new JTextField("0.1");
        panel.add(mutationRateField);

        panel.add(new JLabel("Chromosome Type:"));
        chromTypeBox = new JComboBox<>(new String[]{"Binary", "Integer", "Floating Point"});
        panel.add(chromTypeBox);

        panel.add(new JLabel("Mutation Type:"));
        mutBox = new JComboBox<>(new String[]{"BitFlip", "Swap", "Uniform"});
        panel.add(mutBox);

        panel.add(new JLabel("Crossover Type:"));
        crossBox = new JComboBox<>(new String[]{"OnePoint", "TwoPoint", "Uniform"});
        panel.add(crossBox);

        panel.add(new JLabel("Selection Type:"));
        selectBox = new JComboBox<>(new String[]{"Roulette", "Tournament"});
        panel.add(selectBox);

        panel.add(new JLabel("Replacement Strategy:"));
        replaceBox = new JComboBox<>(new String[]{"Generational", "Steady-State", "Elitism"});
        panel.add(replaceBox);

        runButton = new JButton("Run GA");
        panel.add(new JLabel());
        panel.add(runButton);

        add(panel, BorderLayout.NORTH);

        // === Output Area ===
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Auto-scroll as new lines are added
        DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // === Button Action ===
        runButton.addActionListener(e -> runGA());
    }

    private void runGA() {
        try {
            // === Define project data ===
            double[] costs = {4000, 6000, 3500, 7000, 5000, 8000};
            double[] profits = {5000, 7500, 4000, 10000, 6500, 12000};
            double budget = 18000;

            // === User Inputs ===
            int populationSize = Integer.parseInt(populationField.getText());
            int generations = Integer.parseInt(generationsField.getText());
            double crossoverRate = Double.parseDouble(crossoverRateField.getText());
            double mutationRate = Double.parseDouble(mutationRateField.getText());
            int geneLength = costs.length;

            int chromType = chromTypeBox.getSelectedIndex() + 1;
            int mutChoice = mutBox.getSelectedIndex() + 1;
            int crossChoice = crossBox.getSelectedIndex() + 1;
            int selectChoice = selectBox.getSelectedIndex() + 1;
            int replaceChoice = replaceBox.getSelectedIndex() + 1;
            int eliteSize = 2;

            // === GA Components ===
            FitnessFunction fitness = new MultiProjectFitness(costs, profits, budget);
            InfeasibleHandler infeasibleHandler = new BudgetInfeasibleHandler(costs, profits, budget);

            Mutation mutation = switch (mutChoice) {
                case 1 -> new BitFlip();
                case 2 -> new Swap();
                case 3 -> new Uniform();
                default -> new BitFlip();
            };

            Crossover crossover = switch (crossChoice) {
                case 1 -> new OnePointCrossover();
                case 2 -> new TwoPointCrossover();
                case 3 -> new UniformCrossover();
                default -> new OnePointCrossover();
            };

            Selection selection = switch (selectChoice) {
                case 1 -> new RouletteSelection();
                case 2 -> new TournamentSelection(3);
                default -> new RouletteSelection();
            };

            ReplacementStrategy<?> replacement = switch (replaceChoice) {
                case 1 -> new GenerationalReplacement<>();
                case 2 -> new SteadyStateReplacement<>();
                case 3 -> new ElitismReplacement<>(eliteSize);
                default -> new GenerationalReplacement<>();
            };

            // === Initialize Population ===
            List<Chromosome<?>> population = new ArrayList<>();

            for (int i = 0; i < populationSize; i++) {
                Chromosome<?> c;
                switch (chromType) {
                    case 1 -> c = new BinaryChromosome(geneLength);
                    case 2 -> c = new IntegerChromosome(geneLength, 0, 9);
                    case 3 -> c = new FloatingPointChromosome(geneLength, 0.0, 1.0);
                    default -> c = new BinaryChromosome(geneLength);
                }

                if (!infeasibleHandler.isFeasible(c))
                    c = infeasibleHandler.repair(c);
                c.setFitness(fitness.evaluate(c));
                population.add(c);
            }

            outputArea.append("\n=== Running Genetic Algorithm ===\n");

            // === Run GA Based on Chromosome Type ===
            switch (chromType) {
                case 1, 2 -> {
                    GeneticAlgorithm<Integer> ga = new GeneticAlgorithm<>(
                            populationSize, generations, crossoverRate, mutationRate,
                            geneLength, chromType, // ✅ FIX: Added chromType argument
                            fitness, infeasibleHandler, crossover, mutation, selection,
                            (ReplacementStrategy<Integer>) replacement
                    );
                    Chromosome<Integer> best = ga.run();
                    outputArea.append("\n===== FINAL RESULT =====\n");
                    outputArea.append("Best Chromosome: " + best + "\n");
                    outputArea.append("Fitness: " + best.getFitness() + "\n");
                }
                case 3 -> {
                    GeneticAlgorithm<Double> ga = new GeneticAlgorithm<>(
                            populationSize, generations, crossoverRate, mutationRate,
                            geneLength, chromType, // ✅ FIX: Added chromType argument
                            fitness, infeasibleHandler, crossover, mutation, selection,
                            (ReplacementStrategy<Double>) replacement
                    );
                    Chromosome<Double> best = ga.run();
                    outputArea.append("\n===== FINAL RESULT =====\n");
                    outputArea.append("Best Chromosome: " + best + "\n");
                    outputArea.append("Fitness: " + best.getFitness() + "\n");
                }
                default -> outputArea.append("Invalid Chromosome Type!\n");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Execution Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultiProjectGA().setVisible(true));
    }
}
