package org.odl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  public static void main(String[] args) {

    int portNumber = 8888;
    try {
      portNumber = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println(args[0] + "is not a valid port number");
    }

    try (ServerSocket serverSocket = new ServerSocket(portNumber);
         Socket clientSocket = serverSocket.accept();
         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    ) {
      out.println("Connection initialized. Send \"bye\" or \"BYE\" to exit.");
      String inputLine;
      String outputLine;
      while ((inputLine = in.readLine()) != null) {
        outputLine = in.readLine().toUpperCase();
        out.println(outputLine);
        if (outputLine.equals("BYE")) {
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("Failed to initialize server socket at port " + args[0]);
      throw new RuntimeException(e);
    }
  }
}