package dev.inmo.tgbotapi.types.message.ChatEvents

import dev.inmo.tgbotapi.types.Seconds
import dev.inmo.tgbotapi.types.message.ChatEvents.abstracts.*
import kotlinx.serialization.Serializable

@Serializable
data class MessageAutoDeleteTimerChanged(
    val newAutoDeleteTime: Seconds // TODO:: check that it is seconds
) : ChannelEvent, GroupEvent, SupergroupEvent