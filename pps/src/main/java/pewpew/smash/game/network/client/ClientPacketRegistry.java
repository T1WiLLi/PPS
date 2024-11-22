package pewpew.smash.game.network.client;

import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientPacketRegistry {
    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors = new HashMap<>();

    public ClientPacketRegistry(EntityManager entityManager, ClientWrapper client, ClientHandler clientHandler) {
        Reflections reflections = new Reflections("pewpew.smash.game.network.processor.clientProcessor");
        Set<Class<? extends ClientProcessor>> processorClasses = reflections.getSubTypesOf(ClientProcessor.class);

        for (Class<? extends ClientProcessor> processorClass : processorClasses) {
            registerProcessor(processorClass, entityManager, client, clientHandler);
        }
    }

    private void registerProcessor(Class<? extends ClientProcessor> processorClass, EntityManager entityManager,
            ClientWrapper client, ClientHandler clientHandler) {
        try {
            // Match constructor by parameter count
            Constructor<?>[] constructors = processorClass.getConstructors();

            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                PacketProcessor<?> processor = null;

                if (parameterTypes.length == 2 &&
                        parameterTypes[0] == EntityManager.class &&
                        parameterTypes[1] == ClientWrapper.class) {
                    processor = (PacketProcessor<?>) constructor.newInstance(entityManager, client);
                } else if (parameterTypes.length == 3 &&
                        parameterTypes[0] == EntityManager.class &&
                        parameterTypes[1] == ClientWrapper.class &&
                        parameterTypes[2] == ClientHandler.class) {
                    processor = (PacketProcessor<?>) constructor.newInstance(entityManager, client, clientHandler);
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
    private Class<? extends BasePacket> getPacketTypeFromProcessor(Class<? extends ClientProcessor> processorClass) {
        String packetName = processorClass.getSimpleName()
                .replace("Processor", "")
                .replace("Client", "");
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
