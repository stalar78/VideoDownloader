package org.example;

import javax.swing.*;
import java.awt.*;

public class YouTubeDownloaderGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new YouTubeDownloaderGUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        // Создаём основное окно (фрейм)
        JFrame frame = new JFrame("YouTube Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new GridBagLayout());

        // Добавляем компоненты GUI
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

        // Действия кнопок
        folderButton.addActionListener(e -> FileChooserHelper.chooseFolder(folderField));
        cookiesButton.addActionListener(e -> FileChooserHelper.chooseFile(cookiesField, "txt"));
        downloadButton.addActionListener(e -> {
            // Проверка полей ввода
            if (!ValidationUtils.areFieldsFilled(urlField.getText(), folderField.getText())) {
                logArea.append("Error: Please provide both video URL and output folder.\n");
                return;
            }
            new YouTubeDownloaderService().startDownload(
                    urlField.getText().trim(),
                    folderField.getText().trim(),
                    cookiesField.getText().trim(),
                    (String) formatComboBox.getSelectedItem(),
                    (String) qualityComboBox.getSelectedItem(),
                    logArea,
                    progressBar
            );
        });

        // Отображение окна
        frame.setVisible(true);
    }
}
