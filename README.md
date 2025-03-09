# 🌐 Web-Based Tracking RADAR Trajectory Simulator

![Project Banner](https://via.placeholder.com/800x200.png?text=Web-Based+Tracking+RADAR+Trajectory+Simulator)

## 📝 Overview

This project processes data from **ECEF**, **ENV**, or **LLA** coordinate datasets, converting them into **Range**, **Theta**, and **Phi** parameters. It offers real-time simulation of essential RADAR metrics, displaying **Azimuth Angle**, **Elevation Angle**, and **Range** across three distinct graphs. Additionally, it facilitates data transmission to a receiver PC for further analysis.

---

## 📚 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Usage](#-usage)
  - [Sender Module](#-launch-the-sender-module)
  - [Receiver Module](#-start-the-receiver-module)
  - [Data Visualization](#-visualize-the-data)
- [Contributing](#-contributing)
- [License](#-license)
- [Acknowledgements](#-acknowledgements)

---

## 🚀 Features

✅ **Real-Time Simulation**: Immediate visualization of RADAR parameters.  
✅ **Data Conversion**: Seamlessly transforms ECEF, ENV, or LLA coordinates to Range, Theta, and Phi.  
✅ **Graphical Display**: Simultaneous plotting of Azimuth Angle, Elevation Angle, and Range.  
✅ **Data Transmission**: Efficiently sends processed data to a receiver PC for extended processing.  

---

## 🏗️ Architecture

The simulator is structured into two primary modules:

1. **Sender Module**:
   - **Function**: Reads coordinate data, converts it to RADAR parameters, and transmits the processed data.
   - **Implementation**: Developed in **Java**, leveraging **multi-threading** for optimal performance.

2. **Receiver Module**:
   - **Function**: Receives processed data and visualizes RADAR parameters in real-time.
   - **Implementation**: Built using **Java**, integrating with **HTML** and **JavaScript** for dynamic data representation.

---

## 🛠️ Installation

1. **Clone the Repository:**

```bash
git clone https://github.com/mitul-goswami/Web---Based-Tracking-RADAR-Trajectory-Simulator.git
cd Web---Based-Tracking-RADAR-Trajectory-Simulator
```

2. **Set Up the Development Environment:**

- Ensure [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) is installed.
- Use an IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Eclipse](https://www.eclipse.org/).

3. **Compile the Java Files:**

```bash
javac SenderModule.java ReceiverModule.java
```

4. **Prepare the Web Interface:**

Open `RadarSimulator.html` in a web browser to visualize the RADAR parameters.

---

## 📊 Usage

### 📡 Launch the Sender Module

1. Execute `SenderModule.java`.

```bash
java SenderModule
```

2. Input the path to your coordinate dataset (ECEF, ENV, or LLA).

The module will process and transmit the data.

### 📶 Start the Receiver Module

1. Run `ReceiverModule.java`.

```bash
java ReceiverModule
```

The receiver will collect incoming RADAR data and pass it to the visualization module.

### 📊 Visualize the Data

1. Open the `RadarSimulator.html` file in a compatible web browser.

2. Observe the dynamic graphs displaying:

- **Azimuth Angle**
- **Elevation Angle**
- **Range**

---

## 🤝 Contributing

Contributions are welcome! Here’s how you can get involved:

1. **Fork the Repository:**

2. **Create a New Branch:**

```bash
git checkout -b feature/your-feature
```

3. **Make Your Changes and Commit:**

```bash
git commit -m "Add your feature"
```

4. **Push to Your Branch:**

```bash
git push origin feature/your-feature
```

5. **Submit a Pull Request.**

---

## 📜 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 🙌 Acknowledgements

- **ECEF Coordinate System**: [Earth-Centered, Earth-Fixed Reference](https://en.wikipedia.org/wiki/ECEF)
- **Java Networking**: [Java Networking Tutorial](https://docs.oracle.com/javase/tutorial/networking/)

For issues or questions, please open an [issue](https://github.com/mitul-goswami/Web---Based-Tracking-RADAR-Trajectory-Simulator/issues).

---

⭐ **If you find this project helpful, please consider giving it a star!**
