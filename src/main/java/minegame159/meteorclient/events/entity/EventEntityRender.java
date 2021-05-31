package minegame159.meteorclient.events.entity;

import minegame159.meteorclient.events.Event;
import net.minecraft.entity.Entity;

public class EventEntityRender extends Event {

    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public static class Render extends EventEntityRender {
        public Render(Entity entity) {
            this.entity = entity;
        }
    }

    public static class Label extends EventEntityRender {
        public Label(Entity entity) {
            this.entity = entity;
        }
    }
}
