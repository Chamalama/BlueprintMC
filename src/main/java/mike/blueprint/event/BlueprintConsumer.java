package mike.blueprint.event;

import java.util.function.Consumer;

public record BlueprintConsumer<T>(int priority, Consumer<T> consumer) {
}
