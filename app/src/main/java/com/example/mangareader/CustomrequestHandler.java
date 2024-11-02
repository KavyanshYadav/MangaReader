package  com.example.mangareader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Request;
import com.squareup.picasso.Picasso.LoadedFrom;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.ResponseBody;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

public class CustomrequestHandler extends RequestHandler {

    private OkHttpClient client;

    public CustomrequestHandler(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public boolean canHandleRequest(Request request) {
        return request.uri.toString().startsWith("https://");
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        // Create a new request with custom headers
        Builder builder = new Builder().url(request.uri.toString())
                .addHeader("authority", "v12.com")
                .addHeader("method", "GET")
                .addHeader("path", "/img/tab_32/05/00/60/yi1001417/chapter_16/13-1722560353-o.jpg")
                .addHeader("scheme", "https")
                .addHeader("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
                .addHeader("Accept-Language", "en-US,en;q=0.9,hi;q=0.8")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Pragma", "no-cache")
                .addHeader("Priority", "i")
                .addHeader("Referer", "https://chapmanganato.to/")
                .addHeader("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                .addHeader("Sec-Ch-Ua-Mobile", "20")
                .addHeader("Sec-Ch-Ua-Platform", "Windows")
                .addHeader("Sec-Fetch-Dest", "image")
                .addHeader("Sec-Fetch-Mode", "no-cors")
                .addHeader("Sec-Fetch-Site", "cross-site")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6446.75 Safari/537.36");

        // Execute the request
        Call call = client.newCall(builder.build());
        Response response = call.execute(); // Use the Response class from OkHttp

        // Handle the response
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IOException("Empty response body");
        }

        // Convert the InputStream to Bitmap
        InputStream inputStream = responseBody.byteStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap == null) {
            throw new IOException("Failed to decode bitmap from stream");
        }

        // Return the result
        return new Result(bitmap, LoadedFrom.NETWORK);
    }
}
