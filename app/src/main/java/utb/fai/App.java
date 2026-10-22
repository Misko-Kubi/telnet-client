package utb.fai;

public class App {

    public static void main(String[] args) {
        // Výchozí hodnoty pro případ, že parametry nejsou předány
        String serverIp = "127.0.0.1";
        int port = 23;

        // Zpracování vstupních parametrů
        if (args.length > 0) {
            serverIp = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Neplatné číslo portu, používá se výchozí port: " + port);
            }
        }

        TelnetClient telnetClient = new TelnetClient(serverIp, port);
        telnetClient.run(); // Spuštění telnet klienta
    }
}