package org.example;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.File;
import java.io.IOException;

public class YouTubeDownloader {

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Usage: java -jar youtube-downloader.jar <video-url> <output-folder> [cookies-file]");
            System.exit(1);
        }

        String videoUrl = args[0];
        String outputFolder = args[1];
        String cookiesFile = args.length == 3 ? args[2] : null; // Опциональный путь к файлу cookies

        try {
            downloadVideo(videoUrl, outputFolder, cookiesFile);
        } catch (IOException e) {
            System.err.println("An error occurred while downloading the video: " + e.getMessage());
        }
    }

    private static void downloadVideo(String videoUrl, String outputFolder, String cookiesFile) throws IOException {
        String ytDlpPath = "yt-dlp"; // Убедитесь, что yt-dlp находится в PATH
        File outputDir = new File(outputFolder);

        if (!outputDir.exists() || !outputDir.isDirectory()) {
            throw new IllegalArgumentException("The output folder does not exist or is not a directory.");
        }

        // Создаём команду
        CommandLine cmd = new CommandLine(ytDlpPath);
        cmd.addArgument("-o"); // Указываем шаблон имени файла
        cmd.addArgument(outputFolder + "/%(title)s.%(ext)s");

        // Если указан файл cookies, добавляем его в команду
        if (cookiesFile != null) {
            File cookies = new File(cookiesFile);
            if (!cookies.exists() || !cookies.isFile()) {
                throw new IllegalArgumentException("The cookies file does not exist or is not a valid file.");
            }
            cmd.addArgument("--cookies");
            cmd.addArgument(cookiesFile);
        }

        cmd.addArgument(videoUrl);

        // Выполняем команду
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(cmd);

        System.out.println("Download completed!");
    }
}
