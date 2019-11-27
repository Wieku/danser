package me.wieku.danser.beatmap.parsing

import me.wieku.framework.utils.EnumWithId
import org.joml.Vector2f

enum class Section(override val enumId: Int, val separator: String): EnumWithId {
    Unknown(0, ":"),
    General(1 shl 0, ":"),
    Colours(1 shl 1, ","),
    Editor(1 shl 2, ":"),
    Metadata(1 shl 3, ":"),
    TimingPoints(1 shl 4, ","),
    Events(1 shl 5, ","),
    HitObjects(1 shl 6, ","),
    Difficulty(1 shl 7, ":"),
    Variables(1 shl 8, ":");

    companion object: EnumWithId.Companion<Section>(Unknown)
}

enum class Events(override val enumId: Int): EnumWithId {
    Background(0),
    Video(1),
    Break(2),
    Colour(3),
    Sprite(4),
    Sample(5),
    Animation(6);

    companion object: EnumWithId.Companion<Events>(Background)
}

enum class StoryLayer(override val enumId: Int): EnumWithId {
    Background(0),
    Fail(1),
    Pass(2),
    Foreground(3);

    companion object: EnumWithId.Companion<StoryLayer>(Background)
}