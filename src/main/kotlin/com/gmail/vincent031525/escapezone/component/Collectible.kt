package com.gmail.vincent031525.escapezone.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class Collectible(val quality: Int = -1, val width: Int = 1, val height: Int = 1) {
    companion object {
        val codec: Codec<Collectible> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("quality").forGetter(Collectible::quality),
                Codec.INT.fieldOf("width").forGetter(Collectible::width),
                Codec.INT.fieldOf("height").forGetter(Collectible::height)
            ).apply(instance, ::Collectible)
        }
        val streamCodec: StreamCodec<ByteBuf, Collectible> =
            StreamCodec.composite(
                ByteBufCodecs.INT,
                Collectible::quality,
                ByteBufCodecs.INT,
                Collectible::width,
                ByteBufCodecs.INT,
                Collectible::height,
                ::Collectible
            )
    }
}
