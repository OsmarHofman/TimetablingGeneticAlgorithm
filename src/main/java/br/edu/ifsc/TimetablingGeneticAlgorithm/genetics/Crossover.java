package br.edu.ifsc.TimetablingGeneticAlgorithm.genetics;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crossover {

    /**
     * Realiza o cruzamento por recombinação ordenada.
     *
     * @param chromosomes     vetor de {@link Chromosome} que representa a população de cromossomos
     * @param classSize       valor que representa a quantidade de aulas semanais de todos os cursos.
     * @param crossPercentage porcentagem de cruzamento, ou seja, 10%, 20%, etc.
     * @return nova população de {@link Chromosome}.
     */
    public static Chromosome[] cross(Chromosome[] chromosomes, int classSize, int crossPercentage) {
        Random random = new Random();
        Chromosome[] newGeneration = new Chromosome[chromosomes.length];
        int size = chromosomes[0].getGenes().length;


        List<Integer> changeListC1;
        List<Integer> changeListC2;

        for (int i = 0; i < chromosomes.length; i += 2) {

            int crossChance = random.nextInt(100);
            Chromosome c1 = new Chromosome(chromosomes[i].getGenes(), 0);
            Chromosome c2 = new Chromosome(chromosomes[i + 1].getGenes(), 0);

            //Se cruzar
            if (crossChance < crossPercentage) {

                //Listas que irão conter os genes que serão passados aos filhos respectivos
                changeListC1 = new ArrayList<>();
                changeListC2 = new ArrayList<>();

                //O cutpoint1 é totalmente aleatório, e o cutpoint2 será na mesma turma do cutpoint1
                int cutPoint1 = random.nextInt(size);

                //Limite inferior, ou seja, a primeira posição da turma
                int infLimit = Math.floorDiv(cutPoint1, classSize) * 10;

                //Limite superior, ou seja, a última posição da turma
                int supLimit = infLimit + classSize;

                int cutPoint2 = random.nextInt(classSize) + infLimit + 1;

                if (cutPoint2 < cutPoint1) {
                    int aux = cutPoint1;
                    cutPoint1 = cutPoint2;
                    cutPoint2 = aux;
                }

                //Adiciona somente os genes das turmas que serão cruzadas para os changeLists
                for (int j = infLimit; j < supLimit; j++) {
                    if (c2.getGenes()[j] != 0)
                        changeListC1.add(c2.getGenes()[j]);
                    if (c1.getGenes()[j] != 0)
                        changeListC2.add(c1.getGenes()[j]);
                }

                //Remove das changeLists os genes do meio dos pontos de cortes dos filhos
                for (int j = cutPoint1; j < cutPoint2; j++) {
                    changeListC1.remove((Integer) c1.getGenes()[j]);
                    changeListC2.remove((Integer) c2.getGenes()[j]);
                }

                //Passa os gênes dos pais para seus dois filhos
                insertChangeListGenes(c1, cutPoint1, cutPoint2, changeListC1, infLimit, supLimit);
                insertChangeListGenes(c2, cutPoint1, cutPoint2, changeListC2, infLimit, supLimit);

            }
            newGeneration[i] = c1;
            newGeneration[i + 1] = c2;
        }
        return newGeneration;
    }

    /**
     * Faz a inserção dos genes da {@code changeList} para um {@code child}.
     *
     * @param child      {@link Chromosome} que representa o cromossomo que está sendo tracado entre si.
     * @param cutPoint1  valor numérico que representa o primeiro ponto de corte.
     * @param cutPoint2  valor numérico que representa o segundo ponto de corte.
     * @param changeList Vetor que contém os ids das turmas a serem inseridos no {@code child}.
     * @param infLimit   {@link Integer} que representa o limite inferior da turma a ser mudada (primeira posição da turma).
     * @param supLimit   {@link Integer} que representa o limite superior da turma a ser mudada (última posição da turma).
     */

    private static void insertChangeListGenes(Chromosome child, int cutPoint1, int cutPoint2,
                                              List<Integer> changeList, int infLimit, int supLimit) {
        int parentIterator = cutPoint2;
        int changeListIndex = 0;
        for (int j = infLimit; j < supLimit; j++) {

            //Caso chega no final da turma, volta para o começo
            if (parentIterator == supLimit)
                parentIterator = infLimit;

            //Coloca do changeList para o child
            if (child.getGenes()[j] != 0) {
                if (j < cutPoint1 || j >= cutPoint2) {
                    child.getGenes()[j] = changeList.get(changeListIndex);
                    changeListIndex++;
                }
            }
        }
    }
}
