package br.edu.ifsc.TimetablingGeneticAlgorithm.preprocessing.entities;

/**
 * Representação se um professor dá aulas somente para um curso, para vários cursos, ou nenhum
 */
public enum ProfessorCourseStatus {
    EXCLUSIVE,
    SHARED,
    NOTRELATED
}
