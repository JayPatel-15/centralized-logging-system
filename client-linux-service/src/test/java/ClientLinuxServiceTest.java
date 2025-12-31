import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientLinuxServiceTest {

    @Test
    public void testSendPostsBodyToLocalServer() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/log", exchange -> {
            try (InputStream is = exchange.getRequestBody()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }
                String body = baos.toString(StandardCharsets.UTF_8.name());
                received.set(body);
                exchange.sendResponseHeaders(200, 0);
            } finally {
                exchange.getResponseBody().close();
            }
        });
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        try {
            String body = "{\"message\":\"<86> aiops9242 sudo: pam_unix(sudo:session): session opened for user test(uid=123)\"}";
            ClientLinuxService.send(body);

            // small wait to ensure handler processed the request
            Thread.sleep(100);

            assertEquals(body, received.get());
        } finally {
            server.stop(0);
        }
    }
}