import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class CoordinateConverterGUI extends ApplicationFrame {
    private JComboBox<String> dropdownMenu;
    private JButton selectFileButton;
    private JTextArea outputArea;
    private File selectedFile;
    private Timer timer;
    private BufferedReader bufferedReader;

    private XYSeries rangeSeries;
    private XYSeries thetaSeries;
    private XYSeries phiSeries;

    private XYSeries firstValueSeries;
    private XYSeries secondValueSeries;
    private XYSeries thirdValueSeries;

    private JFreeChart firstValueChart;
    private JFreeChart secondValueChart;
    private JFreeChart thirdValueChart;

    private int timeCounter = 0; 

    private DatagramSocket socket;
    private InetAddress ipAddress;
    private int port;

    public CoordinateConverterGUI() {
        super("Coordinate Converter");
        setSize(1600, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        
        ImageIcon leftIcon = new ImageIcon("C:/Users/KIIT/Downloads/drdo.png");
        Image leftImage = leftIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel leftImageLabel = new JLabel(new ImageIcon(leftImage));

        ImageIcon rightIcon = new ImageIcon("C:/Users/KIIT/Downloads/drdo.png");
        Image rightImage = rightIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel rightImageLabel = new JLabel(new ImageIcon(rightImage));

        
        JPanel imagePanel = new JPanel(new BorderLayout());

       
        JPanel leftPaddingPanel = new JPanel();
        JPanel rightPaddingPanel = new JPanel();

       
        leftPaddingPanel.setPreferredSize(new Dimension(35, 70)); 
        rightPaddingPanel.setPreferredSize(new Dimension(35, 70)); 

        
        imagePanel.add(leftPaddingPanel, BorderLayout.WEST);
        imagePanel.add(leftImageLabel, BorderLayout.CENTER);
        imagePanel.add(rightPaddingPanel, BorderLayout.EAST);
        imagePanel.add(rightImageLabel, BorderLayout.EAST);

       
        JTextField topTextBox = new JTextField("RADAR SIMULATOR");
        topTextBox.setHorizontalAlignment(JTextField.CENTER);
        topTextBox.setEditable(false);
        topTextBox.setFont(new Font("Book Antiqua", Font.BOLD, 40)); 

        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(leftImageLabel, BorderLayout.WEST);
        headerPanel.add(topTextBox, BorderLayout.CENTER);
        headerPanel.add(rightImageLabel, BorderLayout.EAST);

        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

       
        JPanel upperChartPanel = new JPanel(new GridLayout(1, 3)); 
        JPanel lowerChartPanel = new JPanel(new GridLayout(1, 3)); 

        rangeSeries = new XYSeries("Range");
        thetaSeries = new XYSeries("Theta");
        phiSeries = new XYSeries("Phi");

        firstValueSeries = new XYSeries("First Value");
        secondValueSeries = new XYSeries("Second Value");
        thirdValueSeries = new XYSeries("Third Value");

        JFreeChart rangeChart = createChart(rangeSeries, "Range", "Time (sec)", "R (m)");
        JFreeChart thetaChart = createChart(thetaSeries, "Azimuth Angle", "Time (sec)", "Theta (rad)");
        JFreeChart phiChart = createChart(phiSeries, "Elevation Angle", "Time", "Phi (rad)");

        firstValueChart = createChart(firstValueSeries, "First Value", "Time (sec)", "Value");
        secondValueChart = createChart(secondValueSeries, "Second Value", "Time (sec)", "Value");
        thirdValueChart = createChart(thirdValueSeries, "Third Value", "Time (sec)", "Value");

        upperChartPanel.add(new ChartPanel(rangeChart));
        upperChartPanel.add(new ChartPanel(thetaChart));
        upperChartPanel.add(new ChartPanel(phiChart));

        lowerChartPanel.add(new ChartPanel(firstValueChart));
        lowerChartPanel.add(new ChartPanel(secondValueChart));
        lowerChartPanel.add(new ChartPanel(thirdValueChart));

        mainPanel.add(upperChartPanel);

    
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        JTextField middleTextBox = new JTextField("Input Data Visualization");
        middleTextBox.setHorizontalAlignment(JTextField.CENTER);
        middleTextBox.setEditable(false);
        middleTextBox.setFont(new Font("Book Antiqua", Font.BOLD, 20)); 
        middlePanel.add(middleTextBox, BorderLayout.CENTER);
        mainPanel.add(middlePanel);

        mainPanel.add(lowerChartPanel);

        add(mainPanel, BorderLayout.CENTER);

       
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

        
        selectFileButton = new JButton("Send Data");
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        bottomPanel.add(selectFileButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.WEST);

        setVisible(true);

       
        try {
            socket = new DatagramSocket();
            ipAddress = InetAddress.getByName("192.168.1.10"); 
            port = 12345; 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            startReadingFile();
        }
    }

    private void startReadingFile() {
        if (selectedFile != null) {
            try {
                bufferedReader = new BufferedReader(new FileReader(selectedFile));
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timeCounter = 0; 
                timer.schedule(new ReadFileTask(), 0, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadFileTask extends TimerTask {
        @Override
        public void run() {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    String[] values = line.split(",");
                    double[] coords = new double[values.length];
                    for (int i = 0; i < values.length; i++) {
                        coords[i] = Double.parseDouble(values[i]);
                    }

                    // Send the line over UDP
                    byte[] sendData = line.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
                    socket.send(sendPacket);

                    String selectedOption = (String) dropdownMenu.getSelectedItem();
                    double[] convertedCoords = convertCoordinates(selectedOption, coords);
                    SwingUtilities.invokeLater(() -> {
                        outputArea.append("Converted Coordinates: " + convertedCoords[0] + ", " + convertedCoords[1] + ", " + convertedCoords[2] + "\n");
                       
                        rangeSeries.add(timeCounter, convertedCoords[0]);
                        thetaSeries.add(timeCounter, convertedCoords[1]);
                        phiSeries.add(timeCounter, convertedCoords[2]);

                        firstValueSeries.add(timeCounter, coords[0]);
                        secondValueSeries.add(timeCounter, coords[1]);
                        thirdValueSeries.add(timeCounter, coords[2]);
                        timeCounter++;
                    });
                } else {
                    timer.cancel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JFreeChart createChart(XYSeries series, String title, String xAxisLabel, String yAxisLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title, xAxisLabel, yAxisLabel, dataset);

        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(76, 95, 75)); 
        plot.setDomainGridlinePaint(new Color(255, 255, 255)); 
        plot.setRangeGridlinePaint(new Color(255, 255, 255)); 
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        
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
        double[] convertedCoords = new double[3];
        switch (selectedOption) {
            case "ECEF":
                double x = coords[0];
                double y = coords[1];
                double z = coords[2];
                convertedCoords[0] = Math.sqrt(x * x + y * y + z * z); 
                convertedCoords[1] = Math.atan2(y, x); 
                convertedCoords[2] = Math.acos(z / convertedCoords[0]); 
                break;
            case "ENV":
                double east = coords[0];
                double north = coords[1];
                double up = coords[2];
                convertedCoords[0] = Math.sqrt(east * east + north * north + up * up); // Range
                convertedCoords[1] = Math.atan2(north, east); // Azimuth angle
                convertedCoords[2] = Math.atan2(up, Math.sqrt(east * east + north * north)); // Elevation angle
                break;
            case "LLA":
                double lat = coords[0];
                double lon = coords[1];
                double alt = coords[2];
                convertedCoords[0] = lat;
                convertedCoords[1] = lon;
                convertedCoords[2] = alt;
                break;
        }
        return convertedCoords;
    }

    public static void main(String[] args) {
        new CoordinateConverterGUI();
    }
}

