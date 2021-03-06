package a1si12cs111.shashank.gmail.com.fourlettergame;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Created by I332376 on 8/22/2017.
 */

public class DataUtil {

    public String getJsonFromFile(Context context) throws IOException {
        String jsonString = "";

        InputStream is = context.getResources().openRawResource(R.raw.words);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }

        jsonString = writer.toString();
        return jsonString;
    }
}
