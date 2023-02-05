import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.StandardOpenOption;

public class Server {
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;
    private static final int FREE_SERVICE_LIMIT = 10;
    private static final String USAGE_COUNT_FILE = "usage_count.txt";

    public static void main(String[] args) {
        // Crea il socket del server
        // Create the server socket
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(PORT);

            // Carica i contatori di utilizzo dal file
            // Load usage counters from file
            Map<InetAddress, Integer> usageCount = loadUsageCount();

            while (true) {
                // Crea il buffer per ricevere il pacchetto UDP
                // Create buffer to receive UDP packet
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Ricevi il pacchetto UDP dal client
                // Receive UDP packet from client
                serverSocket.receive(receivePacket);

                // Estrai l'indirizzo IP e il numero di porta del client
                // Extract the IP address and port number of the client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Incrementa il contatore di utilizzo per il client
                // Increment the usage counter for the client
                int count = usageCount.getOrDefault(clientAddress, 0) + 1;
                usageCount.put(clientAddress, count);

                // Se il client ha superato il limite di utilizzo gratuito,
                // invia un messaggio per avvertire che il servizio è a pagamento

                // If the client has exceeded the free usage limit,
                 // send a message to warn that the service is paid
                String response;
                if (count > FREE_SERVICE_LIMIT) {
                    response = "Servizio a pagamento";
                } else {
                    // Invia la data e l'ora correnti al client
                    // Send the current date and time to the client
                    Date now = new Date();
                    response = now.toString();
                }

                // Crea il buffer per inviare il pacchetto UDP
                // Create buffer to send UDP packet
                byte[] sendBuffer = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);

                // Invia il pacchetto UDP al client
                // Send the UDP packet to the client
                serverSocket.send(sendPacket);

                // Salva i contatori di utilizzo nel file
                // Save usage counters to file
                saveUsageCount(usageCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Chiudi il socket del server se è stato aperto
            // Close the server socket if it is open
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
    private static Map<InetAddress, Integer> loadUsageCount() throws IOException {
        Map<InetAddress, Integer> usageCount = new HashMap<>();
        Path path = Paths.get(USAGE_COUNT_FILE);
        if (Files.exists(path)) {
            for (String line : Files.readAllLines(path)) {
                String[] parts = line.split(",");
                InetAddress address = InetAddress.getByName(parts[0]);
                int count = Integer.parseInt(parts[1]);
                usageCount.put(address, count);
            }
        } else {
            // Crea il file dei contatori di utilizzo se non esiste
            // Create the usage counter file if it doesn't exist
            Files.createFile(path);
        }
        return usageCount;
    }
    
    private static void saveUsageCount(Map<InetAddress, Integer> usageCount) throws IOException {
        Path path = Paths.get(USAGE_COUNT_FILE);
        List<String> lines = new ArrayList<>();
        for (Map.Entry<InetAddress, Integer> entry : usageCount.entrySet()) {
            InetAddress address = entry.getKey();
            int count = entry.getValue();
            lines.add(address.getHostAddress() + "," + count);
        }
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}