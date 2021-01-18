package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime;

import java.util.Arrays;
import java.util.Optional;

/**
 * Representação dos horários que uma turma pode ter, sendo eles:
 * <ul>
 *     <li>Primeiro Horário</li>
 *     <li>Segundo Horário</li>
 * </ul>
 */
public enum Period {

    PRIMEIRO_HORARIO(0),
    SEGUNDO_HORARIO(1);


    private final int value;

    Period(int value) {
        this.value = value;
    }

    /**
     * Obtém qual a representação em {@link String} de um Enum a partir do seu valor.
     *
     * @param value inteiro que representa o valor da representação.
     * @return {@link String} que contém a representação do {@code value}.
     */
    public static Optional<Period> valueOf(int value) {
        return Arrays.stream(values())
                .filter(period -> period.value == value)
                .findFirst();
    }
}
