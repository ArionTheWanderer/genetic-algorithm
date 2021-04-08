package ru.example.example.task1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NameGA {

    private static final String characters = "abcdefghijklmnopqrstuvwxyz";
    private static final double mutationIndex = 0.2;
    // private static final double survivorsIndex = 0.5;
    private static final int initNumberOfIndividuals = 5;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your name:");
        boolean isEmptyName = false;
        String name = "";
        while (!isEmptyName) {
            name = in.next().toLowerCase();
            if (!name.isEmpty()) {
                isEmptyName = true;
            }
        }
        int genotypeLength = name.length();

        List<String> allPopulation = new ArrayList<>();
        // Генерация начального потомства
        for (int i = 0; i < initNumberOfIndividuals; i++) {
            String newString = generateRandomString(name.length());
            allPopulation.add(i, newString);
        }

        // Начало цикла
        for (int i = 0; i < 1000000; i++) {
            Random random = new Random();
            // формируем пары и их потомков
            for (int j = 0; j < initNumberOfIndividuals; j++) {
                int firstParent = random.nextInt(allPopulation.size());
                int secondParent = -1;
                while (secondParent != firstParent) {
                    secondParent = random.nextInt(allPopulation.size());
                }

                // "рождение" потомка
                String newChild = allPopulation.get(firstParent).substring(0, j + 1) +
                        allPopulation.get(secondParent).substring(j + 1, genotypeLength);
                allPopulation.add(newChild);
            }

            // мутация
            int numberOfMutants = (int) (allPopulation.size() * mutationIndex);
            for (int j = 0; j < numberOfMutants; j++) {
                int mutantIndex = random.nextInt(allPopulation.size());
                int numberOfLetterToReplace = random.nextInt(genotypeLength / 2) + 1;
                String mutant = allPopulation.get(mutantIndex);
                List<Integer> indexesToReplace = new ArrayList<>();
                int k = 0;
                while(k != numberOfLetterToReplace) {
                    int newIndex = random.nextInt(genotypeLength);
                    if (!indexesToReplace.contains(newIndex)) {
                        indexesToReplace.add(newIndex);
                        k += 1;
                    }
                }
                StringBuilder sb = new StringBuilder(genotypeLength);
                for (int l = 0; l < genotypeLength; l++) {
                    if (indexesToReplace.contains(l)) {
                        sb.append(characters.charAt(random.nextInt(characters.length())));
                    } else {
                        sb.append(mutant.charAt(l));
                    }
                }
                allPopulation.remove(mutantIndex);
                allPopulation.add(mutantIndex, sb.toString());
            }

            // Вычисление fitness-функции
            List<Double> fitnessValues = new ArrayList<>();
            double sumOfSurvivalChances = 0d;
            for (int j = 0; j < allPopulation.size(); j++) {
                double fitnessValue = 0;
                String anotherOne = allPopulation.get(j);
                for (int k = 0; k < genotypeLength; k++) {
                    if (anotherOne.charAt(k) != name.charAt(k)) {
                        fitnessValue += 1;
                    }
                }
                fitnessValues.add(j, fitnessValue);
                sumOfSurvivalChances += 1d / fitnessValue;
            }

            // Вычисление шансов на выживание для каждой отдельной особи
            List<Double> survivalChances = new ArrayList<>();
            for (int j = 0; j < fitnessValues.size(); j++) {
                double survivalChance = fitnessValues.get(j) / sumOfSurvivalChances;
                survivalChances.add(j, survivalChance);
            }

            List<Integer> indexesToDelete = new ArrayList<>();
            // Селекция (по парам)
            for (int j = 0; j < survivalChances.size(); j += 2) {
                if (survivalChances.get(j) < survivalChances.get(j + 1)) {
                    indexesToDelete.add(j);
                } else {
                    indexesToDelete.add(j + 1);
                }
            }
            for (int j = allPopulation.size() - 1; j >= 0; j--) {
                if (indexesToDelete.contains(j)) {
                    allPopulation.remove(j);
                    fitnessValues.remove(j);
                }
            }

            if (fitnessValues.contains(0d)) {
                int index = fitnessValues.indexOf(0d);
                String generatedName = allPopulation.get(index);
                System.out.println("The name " + generatedName + " was generated!");
                break;
            } else {
                double max = -1d;
                int indexOfMaxValue = -1;
                for (int j = 0; j < fitnessValues.size(); j++) {
                    if (fitnessValues.get(j) > max) {
                        max = fitnessValues.get(j);
                        indexOfMaxValue = j;
                    }
                }
                String fittestOne = allPopulation.get(indexOfMaxValue);
                System.out.println("At this iteration the fittest one is - " + fittestOne);
            }

        }
    }

    public static String generateRandomString(int length) {
        Random random = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

}
