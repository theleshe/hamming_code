package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner1 = new Scanner(System.in);

        boolean flag;
        String strA;
        do {
            flag = true;
            System.out.println("Введите сообщение, которое необходимо закодировать кодом Хэмминга: ");
             strA = scanner1.nextLine();

            for (char ch : strA.toCharArray())
            {
                if (ch != '1' && ch != '0')         //проверяем на корректность ввода
                    flag = false;
            }
        } while (!flag);

        int a = Integer.parseInt(strA);
        System.out.println("a = " + a + "\n");

        int k = strA.length();
        System.out.println("1) k = " + k);

        double x = log2((k + 1 + Math.ceil(log2(k + 1))));
        int r = (int) Math.ceil(x);
        System.out.println("2) r = " + r);

        int n = k + r;
        System.out.println("3) n = " + n);

        int [][] matrixH = buildH(r,n);
        System.out.println("4) H = ");
        printMatrix(matrixH, r, n);

        int[][] matrixG = buildG(matrixH, k, n);
        System.out.println("5) G = ");
        printMatrix(matrixG, k, n);

        int [] arrayA = convertToIntArray(a, k);
        int [] b = mulMatrix(arrayA, matrixG);
        System.out.print("6) b = ");
        for (int i = 0; i < b.length; i++)
        {
            System.out.print(b[i]);
        }
        System.out.println();

        int numOfMistake;
        do {
            System.out.println("Введите разряд, в котором хотите допустить ошибку.");
            numOfMistake = scanner1.nextInt() - 1;
        } while (numOfMistake < 0 || numOfMistake > n);

        int [] nonB = b.clone();
        if (nonB[numOfMistake] == 0)
            nonB[numOfMistake] = 1;            //делаем ошибку
        else
            nonB[numOfMistake] = 0;

        System.out.print("7) !b = ");
        for (int i = 0; i < b.length; i++)
        {
            System.out.print(nonB[i]);
        }
        System.out.println();

        System.out.print("8) S = !b * H^T = ");
        int [][] transH = transposeMatrix(matrixH);
        int [] S = mulMatrix(nonB, transH);
        for (int i = 0; i < S.length; i++)
        {
            System.out.print(S[i]);
        }
        System.out.println();

        System.out.println("Сравниваю синдром со столбцами проверочной матрицы H.");
        System.out.println("Ошибка в " + (findMistake(S, matrixH)+1) + " разряде.");
    }

    public static double log2(double x) {      //log2
        return Math.log(x) / Math.log(2);
    }

    public static int[][] buildH(int r, int n)      //построение проверочной матрицы
    {
        Random random = new Random();
        int[][] matrixH = new int[r][n];
        for (int j = 0; j < n - r; j++)     //проходимся по строкам добавочной матрицы
        {
            boolean isAnother = false;
            while (!isAnother) {
                for (int i = 0; i < r; i++)
                    matrixH[i][j] = random.nextInt((1) + 1);        //проставили 0 и 1 в столбце случайным способом

                isAnother = true;
                if (j > 0)                 //проверяем с предыдущими столбцами
                {
                    for (int col = 0; col < j; col++) {
                        if (!compareColums(matrixH, j, col)) {
                            isAnother = false;
                            break;
                        }
                    }
                }

                if (isAnother) {        //если прошли предыдущую проверку
                    int countOfOne = 0;
                    for (int i = 0; i < r; i++) {
                        if (matrixH[i][j] == 1) countOfOne++;       //проверяем на то, что единиц больше 1
                    }
                    isAnother = countOfOne > 1;
                }
            }
        }

        for (int i = 0; i < r; i++)
        {
            matrixH[i][i+(n-r)] = 1;                //заполняем единичную матрицу
        }

        return matrixH;
    }

    public static boolean compareColums(int[][] matrix, int colum1, int colum2)
    {
        for(int i = 0; i < matrix.length; i++)      //если попадается хоть один несовпадающий элемент, возвращаем true
        {
            if (matrix[i][colum1] != matrix[i][colum2]) return true;
        }
        return false;
    }

    public static int [][] buildG(int[][] matrixH, int k, int n)        //построить порождающую матрицу
    {
        int[][] matrixG = new int[k][n];

        for (int i = 0; i < k; i++)
        {
            matrixG[i][i] = 1;          //заполняем единичную матрицу
        }

        for (int i = 0; i < n -k; i++)
        {
            for (int j = 0; j < k; j++)
            {
                matrixG[j][i + k] = matrixH[i][j];
            }
        }

        return matrixG;
    }

    public static void printMatrix(int[][] matrix, int countOfRows, int countOfColums)      //вывести матрицу
    {
        for (int i = 0; i < countOfRows; i++)
        {
            for (int j = 0; j < countOfColums; j++)
            {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n");
        }
    }

    public static int[] convertToIntArray(int num, int size)
    {
        int [] numArray = new int[size];
        while (size > 0)
        {
            numArray[size - 1] = num % 10;
            num /= 10;
            size--;
        }
        return numArray;
    }

    public static int[] mulMatrix(int[] matrix1, int[][]matrix2)
    {
        int [] result = new int[matrix2[0].length];

        for (int num = 0; num < matrix2[0].length; num++)
        {
            int sum = 0;
            for (int j = 0; j < matrix1.length; j++)
            {
                sum += matrix1[j] * matrix2[j][num];
            }
            result[num] = sum % 2;
        }

        return result;
    }

    public static int[][] transposeMatrix(int[][] matrix)
    {
        int [][] transMatrix = new int [matrix[0].length][matrix.length];

        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length;j++)
            {
                transMatrix[j][i] = matrix[i][j];
            }
        }

        return transMatrix;
    }

    public static int findMistake(int[] S, int[][] matrixH)
    {
        for (int count = 0; count < matrixH[0].length; count++)
        {
            boolean isFound = true;
            for (int j = 0; j < S.length; j++)
            {
                if (S[j] != matrixH[j][count]) {
                    isFound = false;
                    break;
                }
            }
            if (isFound)
                return count;
        }
        return -1;
    }
}
