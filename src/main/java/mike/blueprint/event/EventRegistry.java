package mike.blueprint.event;

import java.lang.invoke.MethodHandle;
import java.util.*;
import java.util.concurrent.*;

public class EventRegistry {

    public static EventRegistry REGISTRY = new EventRegistry();

    private final Queue<BlueprintEvent> executingEvents = new ConcurrentLinkedQueue<>();

    private final Map<Class<? extends BlueprintEvent>, List<BlueprintConsumer<? extends BlueprintEvent>>> registeredEvents = new ConcurrentHashMap<>();

    private final Map<Class<? extends BlueprintEvent>, List<MethodHandle>> loadedMethods = new ConcurrentHashMap<>();

    public <T extends BlueprintEvent> void registerEvent(Class<T> clazz, BlueprintConsumer<T> consumer) {
        registeredEvents.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>()).add(consumer);
        registeredEvents.get(clazz).sort(Comparator.comparingInt(BlueprintConsumer::priority));
    }

    public <T extends BlueprintEvent> void executeEvent(T event) {
        final List<BlueprintConsumer<? extends BlueprintEvent>> events = registeredEvents.get(event.getClass());
        if(events == null || events.isEmpty()) return;
        events.stream().map(e -> (BlueprintConsumer<T>) e).forEach(
                consumer -> {
                    consumer.consumer().accept(event);
                    executingEvents.add(event);
                }
        );
    }

    /*

    public List<Class<?>> listenerClasses(Plugin plugin) throws IOException {
        final ClassLoader classLoader = plugin.getClass().getClassLoader();
        final ClassPath classPath = ClassPath.from(classLoader);
        final List<Class<?>> classes = classPath.getTopLevelClassesRecursive(plugin.getClass().getPackageName()).stream()
                .map(ClassPath.ClassInfo::load)
                .filter(load -> load.isAnnotationPresent(BlueprintListener.class)).collect(Collectors.toUnmodifiableList());
        return classes;
    }

    public <T extends BlueprintEvent> Map<Class<T>, List<MethodHandle>> getAnnotated(Plugin plugin) throws IOException, IllegalAccessException {
        final List<Class<?>> classes = listenerClasses(plugin);
        final Map<Class<T>, List<MethodHandle>> annotatedMethods = new HashMap<>();
        for(Class<?> clazz : classes) {
            for(Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                final Class<?>[] parameters = method.getParameterTypes();
                if(parameters.length != 1) continue;
                if(!method.isAnnotationPresent(BlueprintHandler.class)) continue;
                if(!BlueprintEvent.class.isAssignableFrom(parameters[0])) continue;
                final Class<T> eventClass = (Class<T>) parameters[0];
                annotatedMethods.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(MethodHandles.lookup().unreflect(method));
            }
        }
        loadedMethods.putAll(annotatedMethods);
        return annotatedMethods;
    }

    public void loadMethods(Plugin plugin) {
        try {
            getAnnotated(plugin);
        }catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4);

    public void handleCalls() {
        EXECUTOR.scheduleAtFixedRate(() -> {
            if(executingEvents.isEmpty()) return;
            final BlueprintEvent event = executingEvents.poll();
            final Class<? extends BlueprintEvent> eventClass = event.getClass();
            loadedMethods.get(eventClass).forEach(methodHandle -> {
                try {
                    methodHandle.invoke(Loader.get(eventClass), event);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }, 0L, 40L, TimeUnit.MILLISECONDS);
    }

     */

}
