package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing.ScheduleTime;

import java.util.Arrays;
import java.util.Optional;

/**
 * Representação dos dias da semana, sendo eles:
 * <ul>
 *     <li>Segunda-feira</li>
 *     <li>Terça-feira</li>
 *     <li>Quarta-feira</li>
 *     <li>Quinta-feira</li>
 *     <li>Sexta-feira</li>
 * </ul>
 */
public enum Day {
    SEGUNDA_FEIRA(0),
    TERCA_FEIRA(1),
    QUARTA_FEIRA(2),
    QUINTA_FEIRA(3),
    SEXTA_FEIRA(4);


    private final int value;

    Day(int value) {
        this.value = value;
    }

    /**
     * Obtém qual a representação em {@link String} de um Enum a partir do seu valor.
     *
     * @param value inteiro que representa o valor da representação.
     * @return {@link String} que contém a representação do {@code value}.
     */
    public static Optional<Day> valueOf(int value) {
        return Arrays.stream(values())
                .filter(horario -> horario.value == value)
                .findFirst();
    }
}
