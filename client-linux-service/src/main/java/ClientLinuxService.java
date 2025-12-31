import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClientLinuxService {

    static Random r = new Random();

    static Map<String, Integer> userUidMap = new HashMap<>();
    static List<String> userList = new ArrayList<>();

    static {
        userUidMap.put("root", 1);
        userUidMap.put("admin", 99);
        userUidMap.put("jay", 45);

        userList.addAll(userUidMap.keySet());
    }

    public static void main(String[] args) throws Exception {
        while(true) {
            String u = userList.get(r.nextInt(userList.size()));
            int uid = userUidMap.get(u);

            String body = String.format(
                    "{\"message\":\"<86> aiops9242 sudo: pam_unix(sudo:session): session opened for user %s(uid=%d)\"}",
                    u, uid
            );
            send(body);
            Thread.sleep(1000);
        }
    }

    static void send(String body) throws Exception {
        URL url = new URL("http://localhost:8081/log");
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/json");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        c.getInputStream().close();
    }
}
