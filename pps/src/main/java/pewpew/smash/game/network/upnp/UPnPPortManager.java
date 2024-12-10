package pewpew.smash.game.network.upnp;

import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class UPnPPortManager implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(UPnPPortManager.class.getName());
    @Getter
    private final GatewayDevice gateway;
    private final ConcurrentHashMap<Integer, List<String>> managedPorts = new ConcurrentHashMap<>();
    private static UPnPPortManager instance;
    private final boolean isAvailable;

    public static synchronized UPnPPortManager getInstance() {
        if (instance == null) {
            instance = new UPnPPortManager();
        }
        return instance;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean openPort(int externalPort, int internalPort) {
        if (!isAvailable) {
            LOGGER.warning("Cannot open port: UPnP is not available");
            return false;
        }
        boolean tcpSuccess = false;
        boolean udpSuccess = false;

        try {
            PortMappingEntry portMapping = new PortMappingEntry();
            if (gateway.getSpecificPortMappingEntry(externalPort, "TCP", portMapping)) {
                LOGGER.warning("TCP Port " + externalPort + " is already mapped!");
                return false;
            }

            tcpSuccess = gateway.addPortMapping(
                    externalPort,
                    internalPort,
                    gateway.getLocalAddress().getHostAddress(),
                    "TCP",
                    "PewPewSmash:Battle Royale");

            udpSuccess = gateway.addPortMapping(
                    externalPort,
                    internalPort,
                    gateway.getLocalAddress().getHostAddress(),
                    "UDP",
                    "PewPewSmash:Battle Royale");

            if (tcpSuccess && udpSuccess) {
                List<String> protocols = new ArrayList<>();
                protocols.add("TCP");
                protocols.add("UDP");
                managedPorts.put(externalPort, protocols);
                LOGGER.info("Successfully mapped port " + externalPort + " (TCP/UDP)");
                return true;
            } else {
                if (tcpSuccess) {
                    gateway.deletePortMapping(externalPort, "TCP");
                }
                if (udpSuccess) {
                    gateway.deletePortMapping(externalPort, "UDP");
                }
                return false;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error mapping port " + externalPort, e);
            return false;
        }
    }

    public boolean closePort(int externalPort) {
        if (!isAvailable) {
            LOGGER.warning("Cannot close port: UPnP is not available");
            return false;
        }
        List<String> protocols = managedPorts.get(externalPort);
        if (protocols == null) {
            LOGGER.warning("Port " + externalPort + " was not managed by this instance");
            return false;
        }

        boolean success = true;
        for (String protocol : protocols) {
            try {
                success &= gateway.deletePortMapping(externalPort, protocol);
                LOGGER.info("Closed port " + externalPort + " for " + protocol);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error closing port " + externalPort + " for " + protocol, e);
                success = false;
            }
        }

        if (success) {
            managedPorts.remove(externalPort);
        }
        return success;
    }

    public void closeAllPorts() {
        if (!isAvailable) {
            LOGGER.warning("Cannot close port: UPnP is not available");
            return;
        }
        for (Integer port : managedPorts.keySet()) {
            closePort(port);
        }
    }

    @Override
    public void close() throws Exception {
        closeAllPorts();
    }

    public String getGatewayInfo() {
        return String.format("Gateway: %s (%s)",
                gateway.getFriendlyName(),
                gateway.getLocalAddress().getHostAddress());
    }

    private UPnPPortManager() {
        GatewayDevice discoveredGateway = null;
        boolean gatewayAvailable = false;

        try {
            GatewayDiscover discover = new GatewayDiscover();
            try {
                discover.discover();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                LOGGER.log(Level.WARNING, "Failed to discover UPnP gateway", e);
            }

            discoveredGateway = discover.getValidGateway();
            if (discoveredGateway != null) {
                gatewayAvailable = true;
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    LOGGER.info("Shutdown hook triggered - cleaning up port mappings");
                    closeAllPorts();
                }));
            } else {
                LOGGER.warning("No valid UPnP gateway device found!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during UPnP initialization", e);
        }

        this.gateway = discoveredGateway;
        this.isAvailable = gatewayAvailable;
    }
}
