package pewpew.smash.game.network.upnp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkUtils {
    private static final Logger LOGGER = Logger.getLogger(NetworkUtils.class.getName());

    private static final String IP_SERVICE = "http://checkip.amazonaws.com";

    public static String getExternalIP() {
        try {
            URL url = new URI(IP_SERVICE).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String ip = reader.readLine().trim();
                if (isValidIP(ip)) {
                    return ip;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get external IP from Amazon AWS", e);
        }
        return null;
    }

    private static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr instanceof Inet4Address && !addr.isLoopbackAddress();
        } catch (Exception e) {
            return false;
        }
    }
}
