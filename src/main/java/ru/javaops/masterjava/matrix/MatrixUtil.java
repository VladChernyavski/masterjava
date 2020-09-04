package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

//        class ColumnMultipleResult {
//            private final int col;
//            private final int[] columnC;
//
//            public ColumnMultipleResult(int col, int[] columnC) {
//                this.col = col;
//                this.columnC = columnC;
//            }
//        }
//
//        final CompletionService<ColumnMultipleResult> completionService = new ExecutorCompletionService<>(executor);
//
//        for (int j = 0; j < matrixSize; j++) {
//            final int col = j;
//            final int[] columnB = new int[matrixSize];
//            for (int k = 0; k < matrixSize; k++) {
//                columnB[k] = matrixB[k][col];
//            }
//            completionService.submit(() -> {
//                final int[] columnC = new int[matrixSize];
//
//                for (int row = 0; row < matrixSize; row++) {
//                    final int[] rowA = matrixA[row];
//                    int sum = 0;
//                    for (int k = 0; k < matrixSize; k++) {
//                        sum += rowA[k] * columnB[k];
//                    }
//                    columnC[row] = sum;
//                }
//                return new ColumnMultipleResult(col, columnC);
//            });
//        }
//
//        for (int i = 0; i < matrixSize; i++){
//            ColumnMultipleResult res = completionService.take().get();
//            for (int k = 0; k < matrixSize; k++){
//                matrixC[k][res.col] = res.columnC[k];
//            }
//        }

//        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
//        for (int j = 0; j < matrixSize; j++){
//            final int row = j;
//            final int[] rowA = matrixA[row];
//            tasks.add(() -> {
//                final int[] rowC = new int[matrixSize];
//                for (int idx = 0; idx < matrixSize; idx++){
//                    final int elA = rowA[idx];
//                    final int[] rowB = matrixB[idx];
//                    for (int col = 0; col < matrixSize; col++){
//                        rowC[col] += elA * rowB[col];
//                    }
//                }
//                matrixC[row] = rowC;
//                return null;
//            });
//        }
//        executor.invokeAll(tasks);

        new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1).submit(
                () -> IntStream.range(0, matrixSize)
                        .parallel()
                        .forEach(row -> {
                            final int[] rowA = matrixA[row];
                            final int[] rowC = matrixC[row];

                            for (int idx = 0; idx < matrixSize; idx++) {
                                final int elA = rowA[idx];
                                final int[] rowB = matrixB[idx];
                                for (int col = 0; col < matrixSize; col++){
                                    rowC[col] += elA * rowB[col];
                                }
                            }
                        })
        ).get();

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int thatColumn[] = new int[matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][i];
            }

            for (int j = 0; j < matrixSize; j++) {
                int thisRow[] = matrixA[j];
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
