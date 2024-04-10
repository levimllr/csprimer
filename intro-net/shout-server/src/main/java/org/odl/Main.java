package org.odl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args) {

    int portNumber = 8888;
    try {
      portNumber = Integer.parseInt(args[0]);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Port number not provided. Using 8888.");
    } catch (NumberFormatException e) {
      System.out.println(args[0] + "is not a valid port number");
    }

    System.out.printf("Waiting for datagrams on port %s.%n", portNumber);

    try (DatagramSocket serverSocket = new DatagramSocket(portNumber)) {

      DatagramPacket inputPacket;
      String output;
      DatagramPacket outputPacket;

      while (true) {

        byte[] rawInput = new byte[1024];
        inputPacket = new DatagramPacket(rawInput, rawInput.length);
        serverSocket.receive(inputPacket);

        String decodedInput = new String(inputPacket.getData(), StandardCharsets.UTF_8);
        InetAddress ipAddress = inputPacket.getAddress();
        int port = inputPacket.getPort();

        System.out.printf("Received %s from %s:%s%n", decodedInput, ipAddress.getHostAddress(),
            port);

        output = decodedInput.toUpperCase();

        if (output.contains("EXIT")) {
          break;
        }

        outputPacket =
            new DatagramPacket(output.getBytes(), output.getBytes().length, ipAddress, port);

        serverSocket.send(outputPacket);
      }
    } catch (IOException e) {
      System.out.println("Failed to initialize server socket at port " + args[0]);
      System.out.println(e.getMessage());
    }
  }
}
