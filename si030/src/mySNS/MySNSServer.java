package mySNS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MySNSServer {
		
	    private boolean isRunning = true;

	    public static void main(String[] args) {
	        System.out.println("servidor: main");
	        MySNSServer server = new MySNSServer();
	        server.startServer(args);
	    }

	    public void startServer (String[] args){
	    	
	    	String serverSocket = args[0];
	        ServerSocket sSoc = null;
	        try {
	            sSoc = new ServerSocket(Integer.parseInt(serverSocket));
	        } catch (IOException e) {
	            System.err.println(e.getMessage());
	            System.exit(-1);
	        }

	        while(isRunning) {
	            try {
	                Socket inSoc = sSoc.accept();
	                ServerThread newServerThread = new ServerThread(inSoc);
	                newServerThread.start();
	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }

	        }
	        try {
	            sSoc.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    //Threads utilizadas para comunicacao com os clientes
	    class ServerThread extends Thread {

	        private Socket socket = null;

	        ServerThread(Socket inSoc) {
	            socket = inSoc;
	            System.out.println("thread do server para cada cliente");
	        }

	        public void run(){
	            try {
	            	String option;
	            	String userUsername = null;
	            	List<String> filenames = new ArrayList<>();
	            	File file;
	                int filenamesSize;
	                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
	                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
	                
	                option = (String) inStream.readObject();
	                userUsername = (String) inStream.readObject();
	                filenamesSize = (int) inStream.readObject();
	                System.out.println("option --> " + option);
	                System.out.println(filenamesSize);
	                while (true) {
	                	System.out.println("volta");
	                	System.out.println(filenames.size() == filenamesSize);
	                    if (filenames.size() == filenamesSize) {
	                        break; // Exit loop when client finishes sending filenames
	                    }

	                    String filename = (String) inStream.readObject();
	                    System.out.println(filename);
	                    if (!(filename == "####")) {
	                    	filenames.add(filename);
	                    	System.out.println(option.equals("-g"));
	                    	if (option.equals("-g")) {
		                    	file = new File(filename + ".cifrado");
		                    	System.out.println("aa");
		                    	if (file.exists()) {
		                    		System.out.println("a");
			                        sendFile(outStream, filename + ".cifrado");
			                        System.out.println("b");
			                        sendFile(outStream, filename + ".assinado");
			                        System.out.println("c");
			                    }else {
			                    	System.out.println("File does not exist in the server");
			                    }
		                    }else {
		                    	//VERIFCAR SE JÁ EXISTE NO SERVER
			                    file = new File(userUsername + "/" + filename);
			                    // Se o diretório não existir, criar
			                    if (!file.getParentFile().exists()) {
			                        file.getParentFile().mkdirs();
			                    } 
			                    
			                    if (!file.exists() && !file.isFile()) {
			                    	receiveFile(inStream, filename, userUsername);
			                    	
			                    } else {
			                    	inStream.skip(Long.MAX_VALUE);
			                    }
		                    }
		                }
	                }

	                outStream.close();
	                inStream.close();
	               
	                socket.close();
	            

	            } catch (Exception e) {
	                e.printStackTrace();
	                return;
	            }
	        }
	        
	        public void receiveFile(ObjectInputStream inStream, String filename, String userUsername) {
	            try {
	                long fileSize = inStream.readLong();
	                byte[] fileData = new byte[(int) fileSize];
	                inStream.readFully(fileData);
	                try (FileOutputStream fileOutputStream = new FileOutputStream(userUsername + "/" + filename)) {
	                    fileOutputStream.write(fileData);
	                    System.out.println("File received successfully.");
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        public void sendFile(ObjectOutputStream outStream, String filename) {
	            File file = new File(filename);
	            System.out.println(filename);
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
	    }
}
