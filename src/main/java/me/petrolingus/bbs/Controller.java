package me.petrolingus.bbs;

import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class Controller {

    public TextField randomNumbersCountField;
    public StackedBarChart<String, Number> chart;

    public TextField pField;
    public TextField qField;
    public TextField moduleField;
    public TextField seedField;
    public TextField intervalsField;
    public TextField minField;
    public TextField maxField;
    public TextField pearsonTestField;
    public TextField pearsonCriticalField;

    public void initialize() {
        onGenerateSequence();
    }

    public void onGenerateSequence() {

        int randomsCount = Integer.parseInt(randomNumbersCountField.getText());

        long[] randomNumbers;
        int[] histogramData;

        long[] randomNumbersCopy;

        do {
            randomNumbers = generateRandomSequence(randomsCount);
            randomNumbersCopy = Arrays.copyOf(randomNumbers, randomsCount);
            histogramData = createHistogramData(randomNumbersCopy);
        } while (!calculationPearsonCriterion(randomNumbersCopy, histogramData));

        writeToFile(randomNumbers);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < histogramData.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), histogramData[i]));
        }

        chart.getData().clear();
        chart.getData().add(series);
    }

    private boolean calculationPearsonCriterion(long[] randomNumbers, int[] histogramData) {

        Arrays.sort(randomNumbers);
        long min = randomNumbers[0];
        long max = randomNumbers[randomNumbers.length - 1];

        int n = histogramData.length;
        double h = (double) (max - min) / n;

        double chi = 0;
        for (int i = 0; i < n; i++) {
            double rightBound = min + (i + 1) * h;
            double leftBound = min + i * h;
            double pi = distributionFunction(rightBound, min, max) - distributionFunction(leftBound, min, max);
            double np = randomNumbers.length * pi;
            chi += Math.pow(histogramData[i] - np, 2) / np;
        }

        ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(n - 1);
        double cumulativeProbability = chiSquaredDistribution.inverseCumulativeProbability(0.05);
        pearsonCriticalField.setText(String.valueOf(cumulativeProbability));
        pearsonTestField.setText(String.valueOf(chi));
        return chi < cumulativeProbability;
    }

    private int[] createHistogramData(long[] randomNumbers) {

        int n = 1 + (int) Math.round(Math.log(randomNumbers.length) / Math.log(2));
        System.out.println("n: " + n);

        Arrays.sort(randomNumbers);
        System.out.println("randomNumbers length: " + randomNumbers.length);
        long min = randomNumbers[0];
        minField.setText(String.valueOf(min));
        long max = randomNumbers[randomNumbers.length - 1];
        maxField.setText(String.valueOf(max));

        double h = (double) (max - min) / n;
        intervalsField.setText(String.valueOf(h));

        int[] histogramData = new int[n];
        for (int i = 0; i < n; i++) {
            double prevBound = min + i * h;
            double nextBound = min + (i + 1) * h;
            int count = 0;
            for (long randomNumber : randomNumbers) {
                boolean b1 = randomNumber >= prevBound;
                boolean b2 = randomNumber <= nextBound;
                if (b1 && b2) {
                    count++;
                }
            }
            histogramData[i] = count;
        }
        System.out.println("histogram data: " + Arrays.toString(histogramData));
        System.out.println("histogram sum: " + Arrays.stream(histogramData).sum());
        return histogramData;
    }

    private long[] generateRandomSequence(int n) {

        System.out.println("randomsCount: " + n);

        BigInteger p = generatePrime();
        pField.setText(p.toString());

        BigInteger q = generatePrime();
        qField.setText(q.toString());

        BigInteger module = p.multiply(q);
        moduleField.setText(module.toString());

        // TODO: 29.04.2021 Выяснить как вычисляется сид с помощью времени
        BigInteger seed = module.divide(BigInteger.TWO);
        seedField.setText(seed.toString());

        long[] randomNumbers = new long[n];
        for (int i = 0; i < randomNumbers.length; i++) {
            seed = seed.modPow(BigInteger.valueOf(2), module);
            randomNumbers[i] = seed.longValue();
        }

        return randomNumbers;
    }

    private void writeToFile(long[] randomNumbers) {
        File file = new File("C:\\Users\\Petrolingus\\Desktop\\randomSequence.txt");
        try (FileWriter writer = new FileWriter(file)) {
            for (long l : randomNumbers) {
                writer.write(l + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BigInteger generatePrime() {
        Random random = new Random();
        BigInteger prime;
        do {
            prime = new BigInteger(16, random);
        } while (!prime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)));
        assert prime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3));
        return prime;
    }

    private double distributionFunction(double x, double min, double max) {
        return (x - min + 1) / (max - min);
    }
}
