package mySNS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;

public class WriteSignedFile {
	
	//TRATAR DAS EXCEÇÕES
	public void assinar(String filename, String username, String doctor) throws NoSuchAlgorithmException, CertificateException, IOException, InvalidKeyException, UnrecoverableKeyException, KeyStoreException, SignatureException {
		
			FileInputStream fis = new FileInputStream(filename);
			FileOutputStream fos = new FileOutputStream(filename + ".assinado");
			
			FileInputStream kis = new FileInputStream(doctor + ".keystore");
			FileOutputStream kos = new FileOutputStream(filename + ".assinatura." + doctor);
			KeyStore kstore = KeyStore.getInstance("PKCS12");
			
			//123456 é a password, pode ter que ser mudada.
			kstore.load(kis, "123456".toCharArray());
			
			//123456 é a password, pode ter que ser mudada.
			Key myPrivateKey = kstore.getKey(username, "123456".toCharArray());
			
			PrivateKey pk = (PrivateKey) myPrivateKey;
			
			Signature s = Signature.getInstance("MD5withRSA");
			s.initSign(pk);
			
			byte [] b = new byte[16];
			
			int i = fis.read(b);
			
			while (i != -1) {
				s.update(b, 0, i);
				fos.write(b, 0, i);
				i = fis.read(b);
			}
			fos.write(s.sign());
			kos.write(s.sign());
			fos.close();
			fis.close();
			kos.close();
	}
}
