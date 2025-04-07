import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DataStreamApp extends JFrame {

    private JTextArea originalFileTextArea;
    private JTextArea filteredFileTextArea;
    private JTextField searchTextField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path currentFilePath;

    public DataStreamApp() {
        setTitle("Data Stream Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        originalFileTextArea = new JTextArea();
        JScrollPane originalScrollPane = new JScrollPane(originalFileTextArea);
        originalScrollPane.setPreferredSize(new Dimension(400, 300));

        filteredFileTextArea = new JTextArea();
        JScrollPane filteredScrollPane = new JScrollPane(filteredFileTextArea);
        filteredScrollPane.setPreferredSize(new Dimension(400, 300));
        filteredFileTextArea.setEditable(false);

        searchTextField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");
        searchButton.setEnabled(false); // Initially disabled

        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(new JLabel("Original File:"));
        textPanel.add(new JLabel("Filtered Lines:"));
        textPanel.add(originalScrollPane);
        textPanel.add(filteredScrollPane);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Search String:"));
        controlPanel.add(searchTextField);
        controlPanel.add(loadButton);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        add(textPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentFilePath = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
            originalFileTextArea.setText("");
            filteredFileTextArea.setText("");
            try (Stream<String> lines = Files.lines(currentFilePath)) {
                StringBuilder sb = new StringBuilder();
                lines.forEach(line -> sb.append(line).append("\n"));
                originalFileTextArea.setText(sb.toString());
                searchButton.setEnabled(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                currentFilePath = null;
                searchButton.setEnabled(false);
            }
        }
    }

    private void searchFile(ActionEvent event) {
        if (currentFilePath == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String searchText = searchTextField.getText();
        filteredFileTextArea.setText("");
        try (Stream<String> lines = Files.lines(currentFilePath)) {
            StringBuilder sb = new StringBuilder();
            lines.filter(line -> line.contains(searchText))
                    .forEach(line -> sb.append(line).append("\n"));
            filteredFileTextArea.setText(sb.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamApp::new);
    }
}