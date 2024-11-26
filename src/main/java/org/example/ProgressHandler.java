package org.example;

import javax.swing.*;
import java.io.*;

public class ProgressHandler {
    public static void parseProgress(InputStream inputStream, JTextArea logArea, JProgressBar progressBar) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8192)) {
            String line;
            while ((line = reader.readLine()) != null) {
                logArea.append(line + "\n");
                // Парсим прогресс из строки
                if (line.contains("[download]")) {
                    int progressIndex = line.indexOf("%");
                    if (progressIndex > 0) {
                        try {
                            String progress = line.substring(line.lastIndexOf("[download]") + 10, progressIndex).trim();
                            int progressValue = (int) Double.parseDouble(progress);
                            SwingUtilities.invokeLater(() -> progressBar.setValue(progressValue));
                        } catch (NumberFormatException ignored) {
                            // Пропускаем строки без числового значения прогресса
                        }
                    }
                }
            }
        } catch (IOException e) {
            logArea.append("Error reading progress: " + e.getMessage() + "\n");
        }
    }
}