package mySNS;

import java.io.FileInputStream;  
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cifra {
	
	//TRATAR DAS EXCEÇÕES
    public void cifrar(String filename, String username, String doctor) throws Exception {

	    //gerar uma chave aleatoria para utilizar com o AES
	    KeyGenerator kg = KeyGenerator.getInstance("AES");
	    kg.init(128);
	    SecretKey key = kg.generateKey();

	    Cipher c = Cipher.getInstance("AES");
	    c.init(Cipher.ENCRYPT_MODE, key);
	
	    FileInputStream fis;
	    FileOutputStream fos;
	    CipherOutputStream cos;
	    
	    fis = new FileInputStream(filename);
	    fos = new FileOutputStream(filename + ".cifrado");
	
	    cos = new CipherOutputStream(fos, c);
	    byte[] b = new byte[16];  
	    int i = fis.read(b);
	    while (i != -1) {
	        cos.write(b, 0, i);
	        i = fis.read(b);
	    }
	    cos.close();
	    fis.close();
	    
	    FileInputStream kfile = new FileInputStream(doctor + ".keystore");  //keystore
	    KeyStore kstore = KeyStore.getInstance("PKCS12");
	        
        //123456 é a password, pode ter que ser mudada.
        kstore.load(kfile, "123456".toCharArray());
        Certificate cert = kstore.getCertificate(username);  //alias do utilizador
        Cipher c1 = Cipher.getInstance("RSA");
        c1.init(Cipher.WRAP_MODE, cert);

        byte[] keyEncoded = c1.wrap(key);
        FileOutputStream kos = new FileOutputStream(filename + ".chave_secreta." + username);
        kos.write(keyEncoded);
        kos.close();
	        
	    
	    
	//-export -keystore maria.keystore -storetype PKCS12 -alias maria -file certificado.maria
	//-import -keystore silva.keystore -storetype PKCS12 -alias silva -file certificado.silva
	    
	    //Dicas para decifrar
	    //byte[] keyEncoded2 - lido do ficheiro
	    //SecretKeySpec keySpec2 = new SecretKeySpec(keyEncoded2, "AES");
	    //c.init(Cipher.DECRYPT_MODE, keySpec2);    //SecretKeySpec é subclasse de secretKey
    }
}