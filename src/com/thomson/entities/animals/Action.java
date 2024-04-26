package com.thomson.entities.animals;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Перечисление действий животного */
@Getter
@AllArgsConstructor
public enum Action {
    MOVE(90),
    EAT(100),
    REPRODUCE(50),
    SLEEP(100);

    /** Поле шанса на действие */
    private final int actionChanceIndex;
}
