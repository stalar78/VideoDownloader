package org.example;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import javax.swing.*;
import java.io.*;

public class YouTubeDownloaderService {
    public void startDownload(String videoUrl, String outputFolder, String cookiesFile,
                              String format, String quality, JTextArea logArea, JProgressBar progressBar) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    logArea.append("Starting download...\n");
                    executeDownload(videoUrl, outputFolder, cookiesFile, format, quality, logArea, progressBar);
                } catch (IOException ex) {
                    logArea.append("Error: " + ex.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setValue(0);
                logArea.append("Download finished.\n");
            }
        };
        worker.execute();
    }

    private void executeDownload(String videoUrl, String outputFolder, String cookiesFile,
                                 String format, String quality, JTextArea logArea, JProgressBar progressBar) throws IOException {
        String ytDlpPath = "yt-dlp";
        CommandLine cmd = new CommandLine(ytDlpPath);

        cmd.addArgument("-o");
        cmd.addArgument(outputFolder + "/%(title)s.%(ext)s");

        // Формат и качество
        if (quality.equals("best")) {
            cmd.addArgument("-f");
            cmd.addArgument("bestvideo[ext=mp4]+bestaudio[ext=m4a]/best");
        } else {
            cmd.addArgument("-f");
            cmd.addArgument("bestvideo[ext=mp4][height<=" + quality + "]+bestaudio[ext=m4a]");
        }

        // Формат файла
        cmd.addArgument("--merge-output-format");
        cmd.addArgument(format);

        // Ограничение скорости
        cmd.addArgument("--limit-rate");
        cmd.addArgument("10M");

        // Параллельное скачивание
        cmd.addArgument("--concurrent-fragments");
        cmd.addArgument("4");

        // Файл cookies
        if (cookiesFile != null && !cookiesFile.isEmpty()) {
            cmd.addArgument("--cookies");
            cmd.addArgument(cookiesFile);
        }

        cmd.addArgument(videoUrl);

        // Выводим команду в лог
        logArea.append("Executing command: " + cmd.toString() + "\n");

        // Настраиваем потоки вывода команды
        PipedOutputStream pipedOut = new PipedOutputStream();
        PipedInputStream pipedIn = new PipedInputStream(pipedOut);

        PumpStreamHandler streamHandler = new PumpStreamHandler(pipedOut, pipedOut);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);

        Thread progressThread = new Thread(() -> ProgressHandler.parseProgress(pipedIn, logArea, progressBar));
        progressThread.start();

        try {
            executor.execute(cmd);
        } catch (IOException e) {
            logArea.append("Error: Command execution failed. " + e.getMessage() + "\n");
            throw e;
        }
    }
}