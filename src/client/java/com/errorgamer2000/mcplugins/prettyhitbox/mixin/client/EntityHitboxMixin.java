package com.errorgamer2000.mcplugins.prettyhitbox.mixin.client;

import com.errorgamer2000.mcplugins.prettyhitbox.interfaces.EntityHitboxExtension;
import net.minecraft.client.render.entity.state.EntityHitbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityHitbox.class)
public abstract class EntityHitboxMixin implements EntityHitboxExtension {
    @Unique
    private float alpha = 1f;

    public void pretty_hitboxes$setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float pretty_hitboxes$alpha() {
        return alpha;
    }
}
