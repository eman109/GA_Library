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
            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(220, 235, 255)); // soft baby blue
            setLocationRelativeTo(null);

// === Input Panel ===
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(250, 252, 255)); // light pastel white-blue
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            Font labelFont = new Font("Times New Roman", Font.PLAIN, 16);
            Font fieldFont = new Font("Times New Roman", Font.PLAIN, 15);

// Helper method to add label + component with spacing
            java.util.function.BiConsumer<String, JComponent> addRow = (labelText, component) -> {
                JLabel label = new JLabel(labelText);
                label.setFont(labelFont);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                component.setAlignmentX(Component.LEFT_ALIGNMENT);
                component.setFont(fieldFont);
                component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                if (component instanceof JComboBox)
                    component.setBackground(new Color(245, 247, 255));
                panel.add(label);
                panel.add(Box.createVerticalStrut(5));
                panel.add(component);
                panel.add(Box.createVerticalStrut(15));
            };

// === Inputs ===
            populationField = new JTextField("10");
            generationsField = new JTextField("20");
            crossoverRateField = new JTextField("0.8");
            mutationRateField = new JTextField("0.1");

            chromTypeBox = new JComboBox<>(new String[]{"Binary", "Integer", "Floating Point"});
            mutBox = new JComboBox<>(new String[]{"BitFlip", "Swap", "Uniform"});
            crossBox = new JComboBox<>(new String[]{"OnePoint", "TwoPoint", "Uniform"});
            selectBox = new JComboBox<>(new String[]{"Roulette", "Tournament"});
            replaceBox = new JComboBox<>(new String[]{"Generational", "Steady-State", "Elitism"});

// Style all combo boxes
            for (JComboBox<?> box : new JComboBox[]{chromTypeBox, mutBox, crossBox, selectBox, replaceBox}) {
                box.setBackground(new Color(245, 247, 255));
                box.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1, true));
                box.setFont(fieldFont);
            }

// Add rows neatly spaced
            addRow.accept("Population Size:", populationField);
            addRow.accept("Number of Generations:", generationsField);
            addRow.accept("Crossover Rate (0-1):", crossoverRateField);
            addRow.accept("Mutation Rate (0-1):", mutationRateField);
            addRow.accept("Chromosome Type:", chromTypeBox);
            addRow.accept("Mutation Type:", mutBox);
            addRow.accept("Crossover Type:", crossBox);
            addRow.accept("Selection Type:", selectBox);
            addRow.accept("Replacement Strategy:", replaceBox);

// === Run Button ===
            runButton = new JButton("Run GA");
            runButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
            runButton.setBackground(new Color(100, 150, 255));
            runButton.setForeground(Color.WHITE);
            runButton.setFocusPainted(false);
            runButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
            runButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            runButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            runButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 130, 255), 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            panel.add(Box.createVerticalStrut(10));
            panel.add(runButton);

// === Scrollable Panel ===
            JScrollPane inputScroll = new JScrollPane(panel);
            inputScroll.setBorder(BorderFactory.createEmptyBorder());
            inputScroll.getVerticalScrollBar().setUnitIncrement(16);
            inputScroll.setPreferredSize(new Dimension(700, 0));
            add(inputScroll, BorderLayout.WEST);

// === Output Area ===
            outputArea = new JTextArea();
            outputArea.setEditable(false);
            outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
            outputArea.setMargin(new Insets(10, 10, 10, 10));
            outputArea.setBackground(new Color(250, 252, 255));
            JScrollPane scrollPane = new JScrollPane(outputArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(180, 200, 255), 1),
                    "Genetic Algorithm Output",
                    0, 0, new Font("Times New Roman", Font.BOLD, 14)
            ));
            add(scrollPane, BorderLayout.CENTER);


// Auto-scroll for output
            DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

// === Button Action ===
            runButton.addActionListener(e -> runGA());}
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
