package pewpew.smash.game.network.upnp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class NetworkUtilsTest {
    @Test
    void testGetExternalIP() {
        String externalIP = NetworkUtils.getExternalIP();
        assertNotNull(externalIP, "External IP should not be null");
        assertEquals("76.70.85.172", externalIP, "The external IP should match the expected IP address");
    }
}
