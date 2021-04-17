package ru.example.example.task1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NameGA {

    private static final String characters = "abcdefghijklmnopqrstuvwxyz ";
    private static final double mutationIndex = 0.2;
    private static final int initNumberOfIndividuals = 100;
    private static boolean isFound = false;
    private static int indexOfFound = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your name:");
        boolean isEmptyName = true;
        String name = "";
        while (isEmptyName) {
            name = in.nextLine().toLowerCase();
            if (name.isEmpty()) {
                System.out.println("Input is empty. Please, try again.");
            } else {
                isEmptyName = false;
            }
        }
        int genotypeLength = name.length();

        List<String> allPopulation = new ArrayList<>();
        // Генерация начального потомства
        for (int i = 0; i < initNumberOfIndividuals; i++) {
            String newString = generateRandomString(genotypeLength);
            allPopulation.add(i, newString);
        }

        // Начало цикла
        for (int i = 0; i < 10000; i++) {
            Random random = new Random();
            List<String> children = new ArrayList<>();

            int border = allPopulation.size() / (genotypeLength - 1);
            int letterBorder = 1;

            // формируем пары и их потомков
            for (int j = 0; j < allPopulation.size(); j++) {
                int firstParent = random.nextInt(allPopulation.size());
                int secondParent = -1;
                while (secondParent != firstParent) {
                    secondParent = random.nextInt(allPopulation.size());
                }

                if (j == border) {
                    border += border;
                    letterBorder += 1;
                }

                // "рождение" потомка
                String newChild = allPopulation.get(firstParent).substring(0, letterBorder) +
                        allPopulation.get(secondParent).substring(letterBorder, genotypeLength);
                children.add(newChild);
            }

            // мутация
            int numberOfMutants = (int) (children.size() * mutationIndex);
            for (int j = 0; j < numberOfMutants; j++) {
                int mutantIndex = random.nextInt(children.size());
                int numberOfLetterToReplace = random.nextInt(genotypeLength / 2) + 1;
                String mutant = children.get(mutantIndex);
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
                children.remove(mutantIndex);
                children.add(mutantIndex, sb.toString());
            }
            allPopulation.addAll(children);

            // Вычисление fitness-функции
            List<Double> fitnessValues = new ArrayList<>();
            double sumOfSurvivalChances = 0d;
            for (int j = 0; j < allPopulation.size(); j++) {
                double fitnessValue = 0d;
                String anotherOne = allPopulation.get(j);
                for (int k = 0; k < genotypeLength; k++) {
                    if (anotherOne.charAt(k) != name.charAt(k)) {
                        fitnessValue += 1d;
                    }
                }
                fitnessValues.add(j, fitnessValue);
                if (fitnessValue != 0d)
                    sumOfSurvivalChances += 1d / fitnessValue;
                else {
                    // найден подходящий
                    isFound = true;
                    indexOfFound = j;
                    break;
                }
            }

            // Если еще не найдена подходящая строка
            if (!isFound) {
                // Вычисление шансов на выживание для каждой отдельной особи
                List<Double> survivalChances = new ArrayList<>();
                for (int j = 0; j < fitnessValues.size(); j++) {
                    double survivalChance = 1d / fitnessValues.get(j) / sumOfSurvivalChances;
                    survivalChances.add(j, survivalChance);
                }

                List<Integer> indexesToDelete = new ArrayList<>();
                // Селекция (по парам)
                for (int j = 0; j < survivalChances.size(); j += 2) {
                    if (j == survivalChances.size() - 1)
                        break;
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
                        survivalChances.remove(j);
                    }
                }

                double min = 100000;
                int indexOfMinValue = -1;
                for (int j = 0; j < fitnessValues.size(); j++) {
                    if (fitnessValues.get(j) < min) {
                        min = fitnessValues.get(j);
                        indexOfMinValue = j;
                    }
                }
                String fittestOne = allPopulation.get(indexOfMinValue);
                System.out.println("At " + i + "'th" + " iteration the fittest one is - \"" + fittestOne +"\"");
            } else {
                String generatedName = allPopulation.get(indexOfFound);
                System.out.println("The name \"" + generatedName + "\" was generated at the " + i + "'th iteration!");
                break;
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
