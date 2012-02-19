package mn.aug.restfulandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import android.content.Context;

public class TestUtil {

	public static String getRawResourceAsString(Context context, int resourceId) throws Exception {

		InputStream is = context.getResources().openRawResource(resourceId);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}

		return writer.toString();
	}

}
