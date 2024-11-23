package org.example;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

public class YouTubeDownloaderGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new YouTubeDownloaderGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Основное окно
        JFrame frame = new JFrame("YouTube Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Поле для ввода URL
        JLabel urlLabel = new JLabel("Video URL:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(urlLabel, gbc);
        JTextField urlField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(urlField, gbc);

        // Поле для выбора папки
        JLabel folderLabel = new JLabel("Output Folder:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(folderLabel, gbc);
        JTextField folderField = new JTextField();
        JButton folderButton = new JButton("Choose...");
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(folderField, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        frame.add(folderButton, gbc);

        // Поле для выбора cookies
        JLabel cookiesLabel = new JLabel("Cookies File (Optional):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(cookiesLabel, gbc);
        JTextField cookiesField = new JTextField();
        JButton cookiesButton = new JButton("Choose...");
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(cookiesField, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        frame.add(cookiesButton, gbc);

        // Выбор формата
        JLabel formatLabel = new JLabel("Format:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(formatLabel, gbc);
        JComboBox<String> formatComboBox = new JComboBox<>(new String[]{"mp4", "webm", "mkv"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(formatComboBox, gbc);

        // Выбор качества
        JLabel qualityLabel = new JLabel("Quality:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(qualityLabel, gbc);
        JComboBox<String> qualityComboBox = new JComboBox<>(new String[]{"best", "1080p", "720p", "480p", "360p"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(qualityComboBox, gbc);

        // Кнопка для начала загрузки
        JButton downloadButton = new JButton("Download");
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(downloadButton, gbc);

        // Лог текстового процесса
        JTextArea logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        frame.add(scrollPane, gbc);

        // Индикатор загрузки
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        frame.add(progressBar, gbc);

        // Логика кнопки "Download"
        folderButton.addActionListener(e -> chooseFolder(folderField));
        cookiesButton.addActionListener(e -> chooseFile(cookiesField, "txt"));
        downloadButton.addActionListener(e -> startDownload(urlField, folderField, cookiesField, formatComboBox, qualityComboBox, logArea, progressBar));

        // Отображение окна
        frame.setVisible(true);
    }

    private void chooseFolder(JTextField field) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseFile(JTextField field, String extension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Files", extension));
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startDownload(JTextField urlField, JTextField folderField, JTextField cookiesField,
                               JComboBox<String> formatComboBox, JComboBox<String> qualityComboBox,
                               JTextArea logArea, JProgressBar progressBar) {
        String videoUrl = urlField.getText().trim();
        String outputFolder = folderField.getText().trim();
        String cookiesFile = cookiesField.getText().trim();
        String format = (String) formatComboBox.getSelectedItem();
        String quality = (String) qualityComboBox.getSelectedItem();

        if (videoUrl.isEmpty() || outputFolder.isEmpty()) {
            logArea.append("Error: Please provide both video URL and output folder.\n");
            return;
        }

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
        cmd.addArgument("10M"); // Ограничение до 10 МБ/с

        // Параллельное скачивание
        cmd.addArgument("--concurrent-fragments");
        cmd.addArgument("4"); // 4 потока для загрузки

        // Многопоточность ffmpeg
        cmd.addArgument("--postprocessor-args");
        cmd.addArgument("ffmpeg:-threads 4");

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

        Thread progressThread = new Thread(() -> parseProgress(pipedIn, logArea, progressBar));
        progressThread.start();

        try {
            executor.execute(cmd);
        } catch (IOException e) {
            logArea.append("Error: Command execution failed. " + e.getMessage() + "\n");
            throw e;
        }
    }


    private void parseProgress(InputStream inputStream, JTextArea logArea, JProgressBar progressBar) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8192)) { // Увеличиваем буфер до 8 KB
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


    private void parseProgress(ByteArrayOutputStream outputStream, JTextArea logArea, JProgressBar progressBar) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logArea.append(line + "\n");
                if (line.contains("%")) {
                    String progress = line.split("%")[0].trim();
                    try {
                        progressBar.setValue(Integer.parseInt(progress));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }


}
