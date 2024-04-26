package com.thomson.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Перечисление список сущностей острова */
@Getter
@AllArgsConstructor
public enum EntityType {
    WOLF("\uD83D\uDC3A"),
    BOA("\uD83D\uDC0D"),
    FOX("\uD83E\uDD8A"),
    BEAR("\uD83D\uDC3B"),
    EAGLE("\uD83E\uDD85"),
    HORSE("\uD83D\uDC0E"),
    DEER("\uD83E\uDD8C"),
    RABBIT("\uD83D\uDC07"),
    MOUSE("\uD83D\uDC01"),
    GOAT("\uD83D\uDC10"),
    SHEEP("\uD83D\uDC11"),
    BOAR("\uD83D\uDC17"),
    BUFFALO("\uD83D\uDC03"),
    DUCK("\uD83E\uDD86"),
    CATERPILLAR("\uD83D\uDC1B"),
    GRASS("\uD83C\uDF3F");

    /** Поле юникод эмодзи сущности */
    private final String unicodeSymbol;
}
