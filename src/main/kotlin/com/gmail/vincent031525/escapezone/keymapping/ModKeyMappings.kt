package com.gmail.vincent031525.escapezone.keymapping

import com.gmail.vincent031525.escapezone.EscapeZone
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.common.util.Lazy
import org.lwjgl.glfw.GLFW

object ModKeyMappings {
    val rotateItemKeyMapping: Lazy<KeyMapping> = Lazy.of { ->
        KeyMapping(
            "key.${EscapeZone.ID}.rotateItem",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.${EscapeZone.ID}"
        )
    }

    fun register(event: RegisterKeyMappingsEvent) {
        event.register(rotateItemKeyMapping.get())
    }
}