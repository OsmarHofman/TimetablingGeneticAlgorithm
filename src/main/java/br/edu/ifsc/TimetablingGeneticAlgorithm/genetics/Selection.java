package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Selection {

    /**
     * Seleção por método da Roleta.
     *
     * @param chromosomes       população de {@link Chromosome}s.
     * @param ratingHandler     Vetor que contém a avaliação individual de cada {@link Chromosome}.
     * @param faA               Valor da Função de Avaliação Acumulada.
     * @param elitismProportion O número de {@link Chromosome} que passaram pelo elitismo.
     * @return Subpopulação de {@link Chromosome}s selecionados.
     * @throws ClassNotFoundException Erro ao tentar obter o pai através do {@code ratingHandler}.
     */
    public static Chromosome[] rouletteWheel(Chromosome[] chromosomes, int[] ratingHandler, int faA, byte elitismProportion) throws ClassNotFoundException {
        //O número de cromossomos que serão selecionados é o descontado de quantos já passaram pelo elitismo
        Chromosome[] parents = new Chromosome[(chromosomes.length - elitismProportion)];

        for (int i = 0; i < parents.length; i++) {
            Random random = new Random();
            int parentRNG = random.nextInt(faA) + 1;
            parents[i] = generateParent(chromosomes, ratingHandler, parentRNG);
        }
        return parents;
    }

    /**
     * Gera um pai.
     *
     * @param chromosomes   população de {@link Chromosome}s.
     * @param ratingHandler Vetor que contém a avaliação individual de cada {@link Chromosome}.
     * @param parentRNG     Valor aleatório que vai indicar a partir do {@code ratingHandler} qual é o {@link Chromosome}
     *                      escolhido.
     * @return pai ({@link Chromosome}) gerado.
     * @throws ClassNotFoundException
     */
    private static Chromosome generateParent(Chromosome[] chromosomes, int[] ratingHandler, int parentRNG) throws ClassNotFoundException {
        for (int i = 0; i < ratingHandler.length; i++) {
            if (i == 0) {
                if (parentRNG <= ratingHandler[i])
                    return chromosomes[i];
            } else {
                if (parentRNG > ratingHandler[i - 1] && parentRNG <= ratingHandler[i])
                    return chromosomes[i];
            }
        }
        throw new ClassNotFoundException("Erro ao gerar cromossomo");
    }

    /**
     * Seleção por Elitismo.
     *
     * @param chromosomes população de {@link Chromosome}s.
     * @param proportion  O número de {@link Chromosome} que serão selecionados.
     * @return Subpopulação de {@link Chromosome}s selecionados.
     */
    public static Chromosome[] elitism(Chromosome[] chromosomes, byte proportion) {

        Chromosome[] chosenChromosomes = new Chromosome[proportion];
        Chromosome[] eliteChromosomes = new Chromosome[chromosomes.length];

        //Faz a cópia da população original
        for (int i = 0; i < chromosomes.length; i++) {
            eliteChromosomes[i] = new Chromosome(chromosomes[i].getGenes(), chromosomes[i].getAvaliation());
        }

        //Faz a ordenação ascendente da população a partir da avaliação
        Arrays.sort(eliteChromosomes, Comparator.comparing(Chromosome::getAvaliation).reversed());
        for (int i = 0; i < proportion; i++) {
            chosenChromosomes[i] = eliteChromosomes[i];
        }
        return chosenChromosomes;
    }
}
