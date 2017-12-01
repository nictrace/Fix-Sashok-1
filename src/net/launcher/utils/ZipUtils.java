package net.launcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

	public static void unzip(String destinationFolder, String zipFile) {
		File directory = new File(destinationFolder);
        
		// if the output directory doesn't exist, create it
		if(!directory.exists()) 
			directory.mkdirs();

		// buffer for read and write data to file
		byte[] buffer = new byte[2048];
        
		try {
			FileInputStream fInput = new FileInputStream(zipFile);
			ZipInputStream zipInput = new ZipInputStream(fInput, Charset.forName("Cp866") );	// UTF-8 for linux
            
			ZipEntry entry = zipInput.getNextEntry();
            
			while(entry != null){
				String entryName = entry.getName();
				File file = new File(destinationFolder + File.separator + entryName);
                
				System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());
                
				// create the directories of the zip directory
				if(entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if(!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if(success == false) {
							System.out.println("Problem creating Folder");
						}
					}
                }
				else {
					if(Files.notExists(file.toPath().getParent())){		// if target directory's not created
						Files.createDirectory(file.toPath().getParent());
					}
					
					FileOutputStream fOutput = new FileOutputStream(file);					
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						// write 'count' bytes to the file output stream
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				// close ZipEntry and take the next one
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
            
			// close the last ZipEntry
			zipInput.closeEntry();
            
			zipInput.close();
			fInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
