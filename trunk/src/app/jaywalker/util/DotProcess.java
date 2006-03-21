package jaywalker.util;

import java.io.File;
import java.io.IOException;

public class DotProcess {

	public void executeDot(File file) throws IOException {
		Process p = null;
		Runtime r = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		File pngFile = DotProcess.toPngFilename(file);
		String command = "dot -Tpng -o" + pngFile.getAbsolutePath() + " "
				+ file.getAbsolutePath();
		if (OS.indexOf("windows 9") > -1) {
			p = r.exec("command.com /c " + command);
		} else if ((OS.indexOf("nt") > -1) || (OS.indexOf("windows 2000") > -1)
				|| (OS.indexOf("windows xp") > -1)) {
			p = r.exec("cmd.exe /c " + command);
		} else {
			p = r.exec(command);
		}
//		try {
//			p.waitFor();
//		} catch (InterruptedException e) {
//			throw new RuntimeException(FileSystem.readInputStreamIntoString(p
//					.getErrorStream()), e);
//		}
	}

	public static File toPngFilename(File file) {
		return new File(new StringHelper().substringBeforeLast(file
				.getAbsolutePath(), ".")
				+ ".png");
	}

}
