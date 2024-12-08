package pewpew.smash.game.network.server;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;

public class ServerPacketRegistry {
    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors = new HashMap<>();

    public ServerPacketRegistry(EntityManager entityManager, ServerWrapper server,
            ServerItemUpdater serverItemUpdater, ServerLobbyManager lobbyManager) {
        Reflections reflections = new Reflections("pewpew.smash.game.network.processor.serverProcessor");
        Set<Class<? extends ServerProcessor>> processorClasses = reflections.getSubTypesOf(ServerProcessor.class);

        for (Class<? extends ServerProcessor> processorClass : processorClasses) {
            registerProcessor(processorClass, entityManager, server, serverItemUpdater, lobbyManager);
        }
    }

    private void registerProcessor(Class<? extends ServerProcessor> processorClass, EntityManager entityManager,
            ServerWrapper server, ServerItemUpdater serverItemUpdater, ServerLobbyManager lobbyManager) {
        try {
            Constructor<?>[] constructors = processorClass.getConstructors();

            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                PacketProcessor<?> processor = null;

                if (parameterTypes.length == 2 &&
                        parameterTypes[0] == EntityManager.class &&
                        parameterTypes[1] == ServerWrapper.class) {
                    processor = (PacketProcessor<?>) constructor.newInstance(entityManager, server);
                } else if (parameterTypes.length == 3 &&
                        parameterTypes[0] == EntityManager.class &&
                        parameterTypes[1] == ServerWrapper.class &&
                        parameterTypes[2] == ServerItemUpdater.class) {
                    processor = (PacketProcessor<?>) constructor.newInstance(entityManager, server, serverItemUpdater);
                } else if (parameterTypes.length == 3 &&
                        parameterTypes[0] == EntityManager.class &&
                        parameterTypes[1] == ServerWrapper.class &&
                        parameterTypes[2] == ServerLobbyManager.class) {
                    processor = (PacketProcessor<?>) constructor.newInstance(entityManager, server, lobbyManager);
                }

                if (processor != null) {
                    Class<? extends BasePacket> packetType = getPacketTypeFromProcessor(processorClass);
                    if (packetType != null) {
                        packetProcessors.put(packetType, processor);
                        System.out.println("Registered processor: " + processorClass.getSimpleName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to register processor: " + processorClass.getSimpleName());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the packet type associated with a given server processor class.
     *
     * This annotation is used to suppress a compiler warning about an unchecked
     * cast. This is safe in this specific case because we control the naming
     * convention for processor classes and expect packet classes to be created
     * correctly. If an invalid processor class is encountered, a
     * `ClassNotFoundException` will be thrown.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends BasePacket> getPacketTypeFromProcessor(Class<? extends ServerProcessor> processorClass) {
        String packetName = processorClass.getSimpleName()
                .replace("Processor", "")
                .replace("Server", "");
        try {
            return (Class<? extends BasePacket>) Class.forName("pewpew.smash.game.network.packets." + packetName);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to find packet type for processor: " + processorClass.getSimpleName());
            return null;
        }
    }

    public Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> getPacketProcessors() {
        return packetProcessors;
    }
}
