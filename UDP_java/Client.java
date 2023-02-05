import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        DatagramSocket clientSocket = null;
        try {
            // Crea il socket del client
            // Create the client socket
            clientSocket = new DatagramSocket();

            // Invia un pacchetto UDP vuoto al server
            // Send an empty UDP packet to the server
            byte[] sendBuffer = new byte[0];
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                    InetAddress.getLocalHost(), PORT);
            clientSocket.send(sendPacket);

            // Crea il buffer per ricevere il pacchetto UDP dal server
            // Create buffer to receive UDP packet from server
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            // Ricevi il pacchetto UDP dal server
            // Receive UDP packet from server
            clientSocket.receive(receivePacket);

            // Estrai il messaggio dal pacchetto UDP
            // Extract the message from the UDP packet
            String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

            // Stampa il messaggio ricevuto dal server
            // Print the message received from the server
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }
}