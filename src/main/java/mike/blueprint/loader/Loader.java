package mike.blueprint.loader;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.common.reflect.ClassPath;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import mike.blueprint.config.Config;
import mike.blueprint.config.SQLiteStorage;
import mike.blueprint.gui.BaseGUI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class Loader {

    @Getter
    public static final Object2ObjectMap<Class<?>, Object> loaded = new Object2ObjectOpenHashMap<>();
    private static final Set<Class<?>> resolving = new HashSet<>();

    public static ObjectList<Class<?>> getComponentClasses(Plugin plugin) throws IOException {
        if(plugin == null) return new ObjectArrayList<>();
        final ObjectList<Class<?>> componentClasses = new ObjectArrayList<>();
        final ClassLoader classLoader = plugin.getClass().getClassLoader();
        final ClassPath classPath = ClassPath.from(classLoader);
        final List<ClassPath.ClassInfo> classes = new ArrayList<>(classPath.getTopLevelClassesRecursive(plugin.getClass().getPackageName()));
        classes.forEach(classInfo -> {
            final Class<?> clazz = classInfo.load();
            if(clazz.isAnnotationPresent(Component.class)) {
                componentClasses.add(clazz);
            }
        });
        componentClasses.sort(Comparator.comparing(c -> c.getAnnotation(Component.class).priority()));
        return componentClasses;
    }

    public static Object resolve(Plugin plugin, Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if(plugin == null) return null;
        if(get(clazz) != null) return get(clazz);
        if(clazz == PaperCommandManager.class) {
            final PaperCommandManager paperCommandManager = get(PaperCommandManager.class) != null ? get(PaperCommandManager.class) : new PaperCommandManager(plugin);
            loaded.putIfAbsent(paperCommandManager.getClass(), paperCommandManager);
            return paperCommandManager;
        }
        if(!clazz.isAnnotationPresent(Component.class)) return null;
        if(resolving.contains(clazz)) {
            throw new RuntimeException("Class already resolved: " + clazz.getName());
        }
        resolving.add(clazz);
        int constructors = clazz.getDeclaredConstructors().length;
        if (constructors == 0) return null;
        final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        final Parameter[] parameters = constructor.getParameters();
        if(parameters.length == 0) {
            final Object instance = constructor.newInstance();
            register(plugin, instance);
            resolving.remove(clazz);
            return instance;
        }else{
            final Object[] params = new Object[parameters.length];
            for(int i = 0; i < params.length; i++) {
                final Parameter parameter = parameters[i];
                final Class<?> paramClass = parameter.getType();
                params[i] = resolve(plugin, paramClass);
            }
            final Object inst = constructor.newInstance(params);
            register(plugin, inst);
            resolving.remove(clazz);
            return inst;
        }
    }

    public static void load(Plugin plugin) {
        try {
            final ObjectList<Class<?>> classes = getComponentClasses(plugin);
            for(Class<?> clazz : classes) {
                resolve(plugin, clazz);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void register(Plugin plugin, Object o) {
        if(o instanceof BaseCommand baseCommand) {
            final PaperCommandManager paperCommandManager = get(PaperCommandManager.class) != null ? get(PaperCommandManager.class) : new PaperCommandManager(plugin);
            loaded.putIfAbsent(paperCommandManager.getClass(), paperCommandManager);
            paperCommandManager.registerCommand(baseCommand);
        }
        if(o instanceof Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        if(o instanceof Config config) {
            config.load();
        }
        if(o instanceof SQLiteStorage sqLiteStorage) {
            sqLiteStorage.load();
        }
        loaded.put(o.getClass(), o);
    }

    public static <V> V get(Class<V> clazz) {
        return (V) loaded.get(clazz);
    }

}
