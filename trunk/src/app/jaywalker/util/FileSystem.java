/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileSystem {

	public static void validateDir(File dir) throws IOException {
		if (dir == null) {
			throw new IOException("Directory should not be null.");
		}
		if (!dir.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + dir);
		}
		if (!dir.isDirectory()) {
			throw new IOException("Is not a directory: " + dir);
		}
		if (!dir.canRead()) {
			throw new IOException("Directory cannot be read: " + dir);
		}
	}

	public static boolean delete(File path) {
		if (!path.exists()) {
			return false;
		}
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					delete(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static String readInputStreamIntoString(InputStream is)
			throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		byte[] bytes = new byte[1024];
		while (is.available() > 0) {
			int length = (is.available() < bytes.length) ? is.available()
					: bytes.length;
			is.read(bytes, 0, length);
			sb.append(new String(bytes, 0, length));
		}
		is.close();
		return sb.toString();
	}

	public static void readInputStreamIntoOutputStream(InputStream is,
			OutputStream os) throws IOException {
		byte[] bytes = new byte[1024];
		while (is.available() > 0) {
			int length = (is.available() < bytes.length) ? is.available()
					: bytes.length;
			is.read(bytes, 0, length);
			os.write(bytes, 0, length);
		}
		is.close();
		os.close();
	}

	public static String readFileIntoString(File file)
			throws FileNotFoundException, IOException {
		InputStream bis = new FileInputStream(file);
		return readInputStreamIntoString(bis);
	}

	public static void writeStringIntoFile(File file, String value)
			throws FileNotFoundException, IOException {
	    FileOutputStream fos = new FileOutputStream(file);
	    FileChannel channel = fos.getChannel();
	    ByteBuffer buf = ByteBuffer.allocate(value.length());
	    buf.put(value.getBytes());
	    channel.write(buf);
	    channel.close();
	}

	public static void writeInputStreamToFile(final InputStream inputStream,
			final long size, final File file) throws IOException {
		ReadableByteChannel input = Channels.newChannel(inputStream);
		FileChannel output = new FileOutputStream(file).getChannel();
		output.transferFrom(input, 0, size);
		input.close();
		output.close();
	}

}
