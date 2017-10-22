/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thu.thss.iise.util;

import java.io.File;

/**
 * @author JinTao
 * 
 */
public class FileUtil {

	/**
	 * @param name
	 *            the name of a file or a directory
	 */
	public static boolean deleteFile(String name) {
		File f = new File(name);
		return deleteFile(f);
	}

	public static boolean deleteFile(File f) {
		if (f.isDirectory()) {
			String[] children = f.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFile(new File(f, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return f.delete();

	}

	public static void createDirectory(String dirName) {
		File f = new File(dirName);
		if (!f.exists()) {
			f.mkdirs();
		} else if (f.isFile()) {
			f.delete();
			f.mkdirs();
		}
	}

	public static long getFileSizeInBytes(String fileName) {
		long ret = 0;
		File f = new File(fileName);
		if (f.isFile()) {
			return f.length();
		} else if (f.isDirectory()) {
			File[] contents = f.listFiles();
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile()) {
					ret += contents[i].length();
				} else if (contents[i].isDirectory())
					ret += getFileSizeInBytes(contents[i].getPath());
			}
		}
		return ret;
	}

	public static float getFileSizeInMB(String fileName) {
		float ret = getFileSizeInBytes(fileName);
		ret = ret / (float) (1024 * 1024);
		return ret;
	}

	// test
	public static void main(String[] args) {
		float size = getFileSizeInMB("processrepository/index/simplepathindex");
		System.out.println("size of index: " + size + " MB");
	}
}
