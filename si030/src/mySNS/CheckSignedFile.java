package mySNS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;

public class CheckSignedFile {
	
	public void verificar(String filename, String userUsername) throws NoSuchAlgorithmException, CertificateException, IOException, InvalidKeyException, UnrecoverableKeyException, KeyStoreException, SignatureException {
		
			FileInputStream sig = new FileInputStream(filename + "assinado");
			FileInputStream kis = new FileInputStream(userUsername + ".keystore");
			//String filename = "a.sign";
			long datalen = new File(filename + ".assinado").length() - 256;
			KeyStore kstore = KeyStore.getInstance("PKCS12");
			kstore.load(kis, "123456".toCharArray());
			
			Certificate cert = kstore.getCertificate(userUsername);
			PublicKey pk = cert.getPublicKey();
			Signature s = Signature.getInstance("MD5withRSA");
			s.initVerify(pk);
				
			byte [] b = new byte[16];
			
			int i;
			
			while (datalen > 0) {
				i = sig.read(b,0, (int)datalen > 16 ? 16 : (int) datalen);
				s.update(b,0,i);
				datalen -= i;
			}
			
			byte [] signInit = new byte[256];
			sig.read(signInit);
			
			if(s.verify(signInit)) {
				System.out.println("ok");
			} else {
				System.out.println("NOK");
			};
			
			sig.close();
	}
}