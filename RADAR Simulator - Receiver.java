import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class CoordinateReceiverGUI extends ApplicationFrame {
    private JComboBox<String> dropdownMenu;
    private JTextArea outputArea;
    private DatagramSocket socket;

    private XYSeries firstValueSeries;
    private XYSeries secondValueSeries;
    private XYSeries thirdValueSeries;

    private JFreeChart firstValueChart;
    private JFreeChart secondValueChart;
    private JFreeChart thirdValueChart;

    private int timeCounter = 0; 

    public CoordinateReceiverGUI() {
        super("Coordinate Receiver");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        
        JTextField topTextBox = new JTextField("RADAR DISPLAY SCREEN");
        topTextBox.setHorizontalAlignment(JTextField.CENTER);
        topTextBox.setEditable(false);
        topTextBox.setFont(new Font("Book Antiqua", Font.BOLD, 50)); 

        
        JLabel leftImageLabel = new JLabel();
        JLabel rightImageLabel = new JLabel();
        try {
            BufferedImage leftImage = ImageIO.read(new File("C:/Users/KIIT/Downloads/drdo.png"));
            BufferedImage rightImage = ImageIO.read(new File("C:/Users/KIIT/Downloads/drdo.png"));
            leftImage = resizeImage(leftImage, 220, 220);
            rightImage = resizeImage(rightImage, 220, 220);
            leftImageLabel.setIcon(new ImageIcon(leftImage));
            rightImageLabel.setIcon(new ImageIcon(rightImage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        JPanel textBoxPanel = new JPanel(new BorderLayout());
        textBoxPanel.add(leftImageLabel, BorderLayout.WEST);
        textBoxPanel.add(topTextBox, BorderLayout.CENTER);
        textBoxPanel.add(rightImageLabel, BorderLayout.EAST);

        
        topPanel.add(textBoxPanel, BorderLayout.NORTH);

        
        JTextField positionTextBox = new JTextField("Position Of Object");
        positionTextBox.setHorizontalAlignment(JTextField.CENTER);
        positionTextBox.setEditable(false);
        positionTextBox.setFont(new Font("Book Antiqua", Font.ITALIC, 30)); 

        
        topPanel.add(positionTextBox, BorderLayout.SOUTH);

        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        topPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

       
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.01; 
        add(topPanel, gbc);

        
        JPanel chartPanel = new JPanel(new GridLayout(1, 3));

        firstValueSeries = new XYSeries("First Value");
        secondValueSeries = new XYSeries("Second Value");
        thirdValueSeries = new XYSeries("Third Value");

        firstValueChart = createChart(firstValueSeries, "First Value", "Time (sec)", "Value");
        secondValueChart = createChart(secondValueSeries, "Second Value", "Time (sec)", "Value");
        thirdValueChart = createChart(thirdValueSeries, "Third Value", "Time (sec)", "Value");

        chartPanel.add(new ChartPanel(firstValueChart));
        chartPanel.add(new ChartPanel(secondValueChart));
        chartPanel.add(new ChartPanel(thirdValueChart));

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0.6; 
        add(chartPanel, gbc);

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        
        dropdownMenu = new JComboBox<>(new String[]{"ECEF", "ENV", "LLA"});
        dropdownMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateChartLabels();
            }
        });
        bottomPanel.add(dropdownMenu, BorderLayout.CENTER);

       
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.001; 
        add(bottomPanel, gbc);

        setVisible(true);

        
        try {
            socket = new DatagramSocket(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        receiveData();
    }

    private void receiveData() {
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String selectedOption = (String) dropdownMenu.getSelectedItem();
                    double[] convertedData = convertCoordinates(selectedOption, parseData(receivedData));
                    updateCharts(convertedData);
                    updateChartLabels(); 
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private double[] parseData(String data) {
        String[] values = data.split(",");
        double[] coords = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            coords[i] = Double.parseDouble(values[i]);
        }
        return coords;
    }

    private void updateCharts(double[] data) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append("Received Data: " + data[0] + ", " + data[1] + ", " + data[2] + "\n");
            firstValueSeries.add(timeCounter, data[0]);
            secondValueSeries.add(timeCounter, data[1]);
            thirdValueSeries.add(timeCounter, data[2]);
            timeCounter++;
        });
    }

    private JFreeChart createChart(XYSeries series, String title, String xAxisLabel, String yAxisLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title, xAxisLabel, yAxisLabel, dataset);

        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(76, 95, 75));

        
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.WHITE);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f)); 
        return chart;
    }

    private void updateChartLabels() {
        String selectedOption = (String) dropdownMenu.getSelectedItem();
        if (selectedOption != null) {
            switch (selectedOption) {
                case "ECEF":
                    firstValueSeries.setKey("X - Coordinate");
                    secondValueSeries.setKey("Y - Coordinate");
                    thirdValueSeries.setKey("Z - Coordinate");
                    break;
                case "ENV":
                    firstValueSeries.setKey("East (m)");
                    secondValueSeries.setKey("North (m)");
                    thirdValueSeries.setKey("Vertical (m)");
                    break;
                case "LLA":
                    firstValueSeries.setKey("Latitude (deg)");
                    secondValueSeries.setKey("Longitude (deg)");
                    thirdValueSeries.setKey("Altitude (m)");
                    break;
            }

            updateChartTitle(firstValueChart, firstValueSeries.getKey().toString(), "Time (sec)", firstValueSeries.getKey().toString());
            updateChartTitle(secondValueChart, secondValueSeries.getKey().toString(), "Time (sec)", secondValueSeries.getKey().toString());
            updateChartTitle(thirdValueChart, thirdValueSeries.getKey().toString(), "Time (sec)", thirdValueSeries.getKey().toString());
        }
    }

    private void updateChartTitle(JFreeChart chart, String title, String xAxisLabel, String yAxisLabel) {
        chart.setTitle(title);
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLabel(xAxisLabel);
        plot.getRangeAxis().setLabel(yAxisLabel);
    }

    private double[] convertCoordinates(String selectedOption, double[] coords) {
        
        double[] converted = new double[3];

        switch (selectedOption) {
            case "ECEF":
                
                converted = coords;
                break;
            case "ENV":
                
                converted[0] = coords[0] * 0.5; // East
                converted[1] = coords[1] * 0.5; // North
                converted[2] = coords[2] * 0.5; // Vertical
                break;
            case "LLA":
                
                converted[0] = coords[0] * 0.0001; // Latitude
                converted[1] = coords[1] * 0.0001; // Longitude
                converted[2] = coords[2] * 0.0001; // Altitude
                break;
        }

        return converted;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    public static void main(String[] args) {
        new CoordinateReceiverGUI();
    }
}
