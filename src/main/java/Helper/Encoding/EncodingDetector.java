package Helper.Encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * tries to identify the Encoding of a file
 * 
 * @author abischoff
 *
 */
public class EncodingDetector extends Reader {
	
	PushbackInputStream input;
	InputStreamReader input2 = null;

	private Encoding defaultEncoding = Encoding.UTF8;
	private static Encoding encoding;

	/**
	 * starts the detection
	 * @param in	InputStream of the file
	 * @param defaultEncoding	encoding that should be used if the detection fails
	 */
	public EncodingDetector(InputStream in, Encoding defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		this.input = new PushbackInputStream(in, 4);
		try {
			this.init();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * return the detected Encoding
	 * @return	the detected Encoding or UTF-8 if an Exception occurs
	 * @throws IOException
	 */
	public Encoding getEncoding() throws IOException {
		if (this.input2 == null)
			return this.defaultEncoding; //UTF-8

		this.input2.close();
		return encoding;
	}

	/**
	 * tries to detect the Encoding
	 * checks the first four bytes after BOM
	 */
	protected void init() throws IOException {
		if (this.input2 != null)
			return;

		int bomSize = 4;
		byte bom[] = new byte[bomSize];
		int num = this.input.read(bom, 0, bom.length);
		int unread = num;

		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			encoding = Encoding.UTF8BOM;
			unread = num - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = Encoding.UNICODEBE;
			unread = num - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = Encoding.UNICODELE;
			unread = num - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = Encoding.UTF32BE;
			unread = num - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = Encoding.UTF32LE;
			unread = num - 4;
		} else {
			if ((bom[0] == (byte) 0x00) ) {
				encoding = Encoding.UNICODEBE;
			}  else if (bom[1] == (byte) 0x00) {
				encoding = Encoding.UNICODELE;
			} else {
				encoding = defaultEncoding; 
			}
		}

		if (unread > 0)
			input.unread(bom, (num - unread), unread);

		if (encoding.is(Encoding.ASCII)) {
			input2 = new InputStreamReader(input);
		} else {
			input2 = new InputStreamReader(input, encoding.toString());
		}
	}
	
	/**
	 * closes the inputStreamReader
	 */
	public void close() throws IOException {
		this.input2.close();
	}

	/**
	 * reads a line
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		return this.input2.read(cbuf, off, len);
	}


}