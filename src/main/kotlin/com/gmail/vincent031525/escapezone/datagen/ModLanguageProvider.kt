package com.gmail.vincent031525.escapezone.datagen

import com.gmail.vincent031525.escapezone.EscapeZone
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class ModLanguageProvider(output: PackOutput) : LanguageProvider(output, EscapeZone.ID, "en_us") {
    override fun addTranslations() {
        add("key.${EscapeZone.ID}.rotateItem", "Rotate Item")
        add("key.categories.${EscapeZone.ID}", "EscapeZone")
    }


}