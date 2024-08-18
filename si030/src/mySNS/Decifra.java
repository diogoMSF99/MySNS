package mySNS;

import java.io.FileInputStream;  
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//Dicas para decifrar
//byte[] keyEncoded2 - lido do ficheiro
//SecretKeySpec keySpec2 = new SecretKeySpec(keyEncoded2, "AES");
//c.init(Cipher.DECRYPT_MODE, keySpec2);    //SecretKeySpec é subclasse de secretKey


public class Decifra {
	//TRATAR DAS EXCEÇÕES
	public void decifrar(String ficheiroCifrado, String ficheiroChave, String uName) throws Exception{
		
		FileInputStream kis = new FileInputStream(ficheiroChave);
		byte [] chaveAES = new byte[16];
		kis.read(chaveAES);
		SecretKeySpec keySpec = new SecretKeySpec(chaveAES, "AES");
		
	    FileInputStream kfile1 = new FileInputStream(uName + ".keystore");  //keystore
	    KeyStore kstore = KeyStore.getInstance("PKCS12");
	    kstore.load(kfile1, "123456".toCharArray());           //password
	    Key myPrivateKey = kstore.getKey(uName, "123456".toCharArray());
	    
	    Cipher c1 = Cipher.getInstance("RSA");
	    c1.init(Cipher.UNWRAP_MODE, myPrivateKey);
	    Key key = c1.unwrap(chaveAES,"AES",Cipher.SECRET_KEY);
	    
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, keySpec);
		c.doFinal();
		
		FileInputStream fis = new FileInputStream(ficheiroCifrado);
		FileOutputStream fos = new FileOutputStream(ficheiroCifrado.replace(".cifrado", "") + ".decifrado");
		CipherInputStream cis = new CipherInputStream(fis,c);
		
		byte [] b = new byte [16];
		
		int i = cis.read(b);
		
		while(i != -1) {
			fos.write(b,0,i);
			i = cis.read(b);
		}
		
		fos.close();
		fis.close();
		cis.close();
	}  
	 
	 
}