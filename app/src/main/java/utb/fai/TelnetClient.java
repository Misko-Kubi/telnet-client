package utb.fai;

import java.io.*;
import java.net.*;

public class TelnetClient {

    private String serverIp;
    private int port;
    private volatile boolean running = true;

    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try {
            Socket socket = new Socket(serverIp, port);
            System.out.println("Připojeno k serveru " + serverIp + ":" + port);

            // Vytvoření vlákna pro příjem dat ze serveru
            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream in = socket.getInputStream();
                    while (running && !socket.isClosed()) {
                        if (in.available() > 0) {
                            int data = in.read();
                            if (data == -1) {
                                break;
                            }
                            System.out.print((char) data);
                            System.out.flush();
                        } else {
                            Thread.sleep(20); // Krátká pauza, aby se snížilo zatížení CPU
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    if (running) {
                        // Pokud dojde k chybě při čtení, ukončíme vlákno
                        e.printStackTrace();
                    }
                }
            });

            // Odesílací vlákno pro čtení vstupu od uživatele a odesílání na server
            Thread sendThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    OutputStream out = socket.getOutputStream();
                    String line;
                    
                    while (running && (line = reader.readLine()) != null) {
                        if ("/QUIT".equalsIgnoreCase(line)) {
                            running = false;
                            break;
                        }
                        // Odeslání řádku na server 
                        out.write((line + "\r\n").getBytes());
                        out.flush();
                    }
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                } finally {
                    running = false;
                }
            });

            receiveThread.start();
            sendThread.start();

            // Čekání na dokončení odesílacího vlákna
            sendThread.join();
            running = false;
            socket.close();
            receiveThread.join();

            System.out.println("Spojení bylo ukončeno.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
