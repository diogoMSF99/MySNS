package mySNS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.crypto.CipherOutputStream;


public class MySNS {
	public static void main(String[] args) {
		System.out.println("cliente: main");
		MySNS client = new MySNS();
		client.startClient(args);
	}
	
	public void startClient(String[] args) {
        try {
            String serverAddress = null;
            String doctorUsername = null;
            String userUsername = null;
            String[] filenames = null;
            String option = null;
            int port = 23456;

            // Parsing command-line arguments
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-a")) {
                    serverAddress = args[i + 1];
                } else if (args[i].equals("-m")) {
                    doctorUsername = args[i + 1];
                } else if (args[i].equals("-u")) {
                    userUsername = args[i + 1];
                } else if (args[i].equals("-sc") || args[i].equals("-sa") || args[i].equals("-se")) {
                    int filenamesStartIndex = i + 1;
                    option = args[i];
                    filenames = new String[args.length - filenamesStartIndex];
                    System.arraycopy(args, filenamesStartIndex, filenames, 0, args.length - filenamesStartIndex);
                    break; // Assuming filenames are at the end of arguments
                } else if(args[i].equals("-g")) {
                	doctorUsername = "#####";
                	int filenamesStartIndex = i + 1;
                	option = args[i];
                    filenames = new String[args.length - filenamesStartIndex];
                    System.arraycopy(args, filenamesStartIndex, filenames, 0, args.length - filenamesStartIndex);
                    break; // Assuming filenames are at the end of arguments
                }
            }
            // Validation
            if (serverAddress == null || doctorUsername == null || userUsername == null || filenames == null) {
                System.err.println("Invalid arguments.");
                return;
            }
            // ServerAddress can or cannot have port
            if (serverAddress.contains(":")) {
                String[] parts = serverAddress.split(":");
                serverAddress = parts[0];
                port = Integer.parseInt(parts[1]);
            }
            
            // Establishing connection
            Socket socket = new Socket(serverAddress, port);
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

    
            // Sending data
            outStream.writeObject(option);
            outStream.writeObject(userUsername);
            Cifra cifra = new Cifra();
            WriteSignedFile assina = new WriteSignedFile();
            ///////////
            Decifra decifra = new Decifra();
            CheckSignedFile verificaAssinatura = new CheckSignedFile();
            ///////////
            
            if(option.equals("-sc")) {
            	
            	outStream.writeObject(filenames.length*2); 
            	
	            for (String filename : filenames) {
	            	// Check if the file exists locally
	                File file = new File(filename);
	                if (!file.exists()) {
	                    System.err.println("File " + filename + " does not exist locally. Skipping...");
	                    continue; // Move to the next filename
	                }
	                // Check if the file exists on the server
	                // Assuming you have a method to check this, let's call it fileExistsOnServer
	                if (fileExistsOnServer(filename)) {
	                    System.err.println("File " + filename + " already exists on the server. Skipping...");
	                    continue; // Move to the next filename
	                }	    
	                
	            	cifra.cifrar(filename, userUsername, doctorUsername);
	            	
	            	
	            	outStream.writeObject(filename + ".cifrado");
	            	sendFile(outStream, filename + ".cifrado");
	            	outStream.writeObject(filename + ".chave_secreta." + userUsername);
	            	sendFile(outStream, filename + ".chave_secreta." + userUsername);
	            	
	            	}
	            
           
            } else if(option.equals("-sa")) {
            	
            	outStream.writeObject(filenames.length*2); 
            	
            	for (String filename : filenames) {
	            	// Check if the file exists locally
	                File file = new File(filename);
	                if (!file.exists()) {
	                    System.err.println("File " + filename + " does not exist locally. Skipping...");
	                    continue; // Move to the next filename
	                }
	                // Check if the file exists on the server
	                // Assuming you have a method to check this, let's call it fileExistsOnServer
	                if (fileExistsOnServer(filename)) {
	                    System.err.println("File " + filename + " already exists on the server. Skipping...");
	                    continue; // Move to the next filename
	                }
	                
		            assina.assinar(filename, userUsername, doctorUsername);
		            
		            outStream.writeObject(filename + ".assinado");
		            sendFile(outStream, filename + ".assinado");
		            outStream.writeObject(filename + ".assinatura." + doctorUsername);
	            	sendFile(outStream, filename + ".assinatura." + doctorUsername);
	            	
	            	}
	                
            	
            } else if(option.equals("-se")) {
            	
            	outStream.writeObject(filenames.length*4);
            	
            	for (String filename : filenames) {
	            	// Check if the file exists locally
	                File file = new File(filename);
	                if (!file.exists()) {
	                    System.err.println("File " + filename + " does not exist locally. Skipping...");
	                    continue; // Move to the next filename
	                }
	                // Check if the file exists on the server
	                // Assuming you have a method to check this, let's call it fileExistsOnServer
	                if (fileExistsOnServer(filename)) {
	                    System.err.println("File " + filename + " already exists on the server. Skipping...");
	                    continue; // Move to the next filename
	                }
	                
	                cifra.cifrar(filename, userUsername, doctorUsername);
		            assina.assinar(filename, userUsername, doctorUsername);
		            
		            outStream.writeObject(filename + ".cifrado");
		            sendFile(outStream, filename + ".cifrado");
		            outStream.writeObject(filename + ".chave_secreta." + userUsername);
	            	sendFile(outStream, filename + ".chave_secreta." + userUsername);
	            	outStream.writeObject(filename + ".assinado");
		            sendFile(outStream, filename + ".assinado");
		            outStream.writeObject(filename + ".assinatura." + doctorUsername);
	            	sendFile(outStream, filename + ".assinatura." + doctorUsername);
	            	}
            	
            } else if(option.equals("-g")) {
            	outStream.writeObject(filenames.length*4);
            	for (String filename : filenames) {
            		System.out.println(filename);
            		File file = new File(filename);
	                if (file.exists()) {
	                	outStream.writeObject(filename);
	                	System.out.println("a");
	                	receiveFile(inStream, filename + ".cifrado");
	                	System.out.println("b");
	                	receiveFile(inStream, filename + ".assinado");
	                	System.out.println("c");
	                    decifra.decifrar(filename + ".cifrado", filename + ".chave_secreta." + userUsername, userUsername);
	                    System.out.println("d");
	                    verificaAssinatura.verificar(filename, userUsername);
	                    
	                } else {
	                	outStream.writeObject("####");
	                	System.err.println("File " + filename + " already exists locally. Skipping...");
	                    //continue; // Move to the next filename
	                }
            	}
            }
            
            //VERIFICAR SE OS FICHEIROS CRIADOS JA EXISTEM NA DIRETORIA DA PESSOA

            // Closing resources
            outStream.close();
            inStream.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void sendFile(ObjectOutputStream outStream, String filename) {
        File file = new File(filename);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            outStream.writeLong(file.length());

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	public void receiveFile(ObjectInputStream inStream, String filename) {
        try {
        	System.out.println(filename);
            long fileSize = inStream.readLong();
            byte[] fileData = new byte[(int) fileSize];
            inStream.readFully(fileData);
            try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                fileOutputStream.write(fileData);
                System.out.println("File received successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	private boolean fileExistsOnServer(String filename) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
