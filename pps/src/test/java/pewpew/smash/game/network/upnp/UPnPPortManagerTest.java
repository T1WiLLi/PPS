package pewpew.smash.game.network.upnp;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.PortMappingEntry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;

import java.net.ServerSocket;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UPnPPortManagerTest {

    private static UPnPPortManager portManager;
    private static boolean isUPnPAvailable = false;

    @BeforeAll
    static void setUp() {
        try {
            portManager = UPnPPortManager.getInstance();
            isUPnPAvailable = true;
        } catch (Exception e) {
            System.out.println("UPnP not available in this environment: " + e.getMessage());
            isUPnPAvailable = false;
        }
    }

    @AfterAll
    static void tearDown() {
        if (portManager != null) {
            portManager.closeAllPorts();
        }
    }

    static boolean isUPnPAvailable() {
        return isUPnPAvailable;
    }

    private int findFreePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    @Test
    @Order(1)
    @EnabledIf("isUPnPAvailable")
    void testGetGatewayInfo() {
        String gatewayInfo = portManager.getGatewayInfo();
        assertNotNull(gatewayInfo, "Gateway info should not be null");
        assertTrue(gatewayInfo.startsWith("Gateway:"), "Gateway info should start with 'Gateway:'");
        assertTrue(gatewayInfo.contains("("), "Gateway info should contain IP address in parentheses");
    }

    @Test
    @Order(2)
    @EnabledIf("isUPnPAvailable")
    void testOpenPort() throws Exception {
        int port = findFreePort();
        boolean result = portManager.openPort(port, port);
        assertTrue(result, "Port mapping should succeed");

        GatewayDevice gateway = portManager.getGateway();
        PortMappingEntry portMapping = new PortMappingEntry();
        assertTrue(gateway.getSpecificPortMappingEntry(port, "TCP", portMapping),
                "TCP Port mapping should exist");

        assertTrue(gateway.getSpecificPortMappingEntry(port, "UDP", portMapping),
                "UDP Port mapping should exist");

        portManager.closePort(port);
    }

    @Test
    @Order(3)
    @EnabledIf("isUPnPAvailable")
    void testOpenPortWithDifferentInternalPort() throws Exception {
        int externalPort = findFreePort();
        int internalPort = findFreePort();
        boolean result = portManager.openPort(externalPort, internalPort);
        assertTrue(result, "Port mapping with different internal port should succeed");

        GatewayDevice gateway = portManager.getGateway();
        PortMappingEntry portMapping = new PortMappingEntry();
        assertTrue(gateway.getSpecificPortMappingEntry(externalPort, "TCP", portMapping),
                "TCP Port mapping should exist");
        assertEquals(internalPort, portMapping.getInternalPort(),
                "Internal port should match specified port");

        portManager.closePort(externalPort);
    }

    @Test
    @Order(4)
    @EnabledIf("isUPnPAvailable")
    void testOpenPortTwiceFails() throws Exception {
        int port = findFreePort();
        boolean firstResult = portManager.openPort(port, port);
        assertTrue(firstResult, "First port mapping should succeed");

        boolean secondResult = portManager.openPort(port, port);
        assertFalse(secondResult, "Second port mapping should fail");

        portManager.closePort(port);
    }

    @Test
    @Order(5)
    @EnabledIf("isUPnPAvailable")
    void testClosePort() throws Exception {
        int port = findFreePort();
        portManager.openPort(port, port);

        boolean result = portManager.closePort(port);
        assertTrue(result, "Port closing should succeed");

        GatewayDevice gateway = portManager.getGateway();
        PortMappingEntry portMapping = new PortMappingEntry();
        assertFalse(gateway.getSpecificPortMappingEntry(port, "TCP", portMapping),
                "TCP Port mapping should not exist after closing");

        assertFalse(gateway.getSpecificPortMappingEntry(port, "UDP", portMapping),
                "UDP Port mapping should not exist after closing");
    }

    @Test
    @Order(6)
    @EnabledIf("isUPnPAvailable")
    void testAutoCloseable() throws Exception {
        int port = findFreePort();
        try (UPnPPortManager localManager = UPnPPortManager.getInstance()) {
            localManager.openPort(port, port);
        }

        GatewayDevice gateway = portManager.getGateway();
        PortMappingEntry portMapping = new PortMappingEntry();
        assertFalse(gateway.getSpecificPortMappingEntry(port, "TCP", portMapping),
                "TCP Port mapping should not exist after auto-closing");
        assertFalse(gateway.getSpecificPortMappingEntry(port, "UDP", portMapping),
                "UDP Port mapping should not exist after auto-closing");
    }

    @Test
    @Order(7)
    @EnabledIf("isUPnPAvailable")
    void testCloseNonExistentPort() {
        boolean result = portManager.closePort(65535);
        assertFalse(result, "Closing non-existent port should return false");
    }
}