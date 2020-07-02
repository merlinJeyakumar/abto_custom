package com.support.utills;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipManager {

	private final int BUFFER = 80000;

	public void zip(List<File> _files, File zipFile) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			byte data[] = new byte[BUFFER];

			for (int i = 0; i < _files.size(); i++) {
				Log.v("Compress", "Adding: " + _files.get(i));
				FileInputStream fi = new FileInputStream(_files.get(i));
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(_files.get(i).getAbsolutePath().substring(_files.get(i).getAbsolutePath().lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unzip(String _zipFile, String _targetLocation) {
		
		//create target location folder if not exist
		dirChecker(_targetLocation);
		
		try {
			FileInputStream fin = new FileInputStream(_zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {

				//create dir if required while unzipping
				if (ze.isDirectory()) {
					dirChecker(ze.getName());
				} else {
					FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
					for (int c = zin.read(); c != -1; c = zin.read()) {
						fout.write(c);
					}

					zin.closeEntry();
					fout.close();
				}

			}
			zin.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void dirChecker(String dir) {
		File f = new File(dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

}