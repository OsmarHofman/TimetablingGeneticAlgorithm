package br.edu.ifsc.TimetablingGeneticAlgorithm.postprocessing;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.Chromosome;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOIFSC;
import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOITC;

import java.util.List;
import java.util.Stack;

/**
 * Classe referente ao pós-processamento
 */
public class PostProcessing {
    private final DTOITC dtoitc;
    private final DTOIFSC dtoifsc;

    public PostProcessing(Chromosome chromosome, DTOITC dtoitc, DTOIFSC dtoifsc, int initialAvaliation) throws ClassNotFoundException {
        this.dtoitc = dtoitc;
        this.dtoifsc = dtoifsc;
        Avaliation.rate(chromosome, dtoitc, initialAvaliation, false);
    }

    /**
     * Indica se um cromossomo tem conflito, baseado na sua avaliação.
     *
     * @param chromosome        {@link Chromosome} que será verificado.
     * @param initialAvaliation valor base inicial do cromossomo, ou seja, seu resultado sem conflitos.
     * @return true caso o cromossomo tenha algum conflito e false caso contrário.
     */
    public boolean hasConflicts(Chromosome chromosome, int initialAvaliation) {
        return chromosome.getAvaliation() != initialAvaliation;
    }

    /**
     * Resolve os conflitos de um cromossomo.
     *
     * @param chromosome        {@link Chromosome} que os conflitos serão resolvidos.
     * @param initialAvaliation valor base inicial do cromossomo, ou seja, seu resultado sem conflitos.
     * @return {@link Chromosome} com os conflitos resolvidos.
     * @throws ClassNotFoundException Caso alguma entidade não seja encontrada.
     */
    public Chromosome resolveConflicts(Chromosome chromosome, int initialAvaliation) throws ClassNotFoundException {
        //Obtém as restrições violadas
        List<ViolatedConstraint> violatedConstraints = chromosome.checkScheduleConflicts(this.dtoifsc, this.dtoitc);

        //Pilha que faz efetivamente o controle da árvore
        Stack<Chromosome> stack = new Stack<>();

        stack.add(chromosome);

        Avaliation.rate(chromosome, this.dtoitc, initialAvaliation, true);

        //Faz a busca em profundidade para resolver os conflitos
        return this.depthSearchTree(stack, violatedConstraints, 0, initialAvaliation, 0);

    }

    /**
     * Árvore de busca em profundidade com profundidade limitada que resolve os conflitos remanescentes do cromossomo.
     *
     * @param chromosomeStack          {@link Stack} de {@link Chromosome} com todos os cromossomos da árvore.
     * @param violatedConstraints      {@link List} de {@link ViolatedConstraint} que contém os conflitos a serem resolvidos.
     * @param violatedConstraintsIndex número que indica a qual conflito em específico está sendo verificado para resolver.
     * @param perfectResult            valor base inicial do cromossomo, ou seja, seu resultado sem conflitos.
     * @param geneIndex                posição do gene com o conflito a ser tratado.
     * @return {@link Chromosome} com os conflitos resolvidos.
     * @throws ClassNotFoundException Caso alguma entidade não seja encontrada.
     */
    public Chromosome depthSearchTree(Stack<Chromosome> chromosomeStack, List<ViolatedConstraint> violatedConstraints, int violatedConstraintsIndex, int perfectResult, int geneIndex) throws ClassNotFoundException {
        Chromosome chromosome = chromosomeStack.peek();

        if (chromosome.getAvaliation() == perfectResult)
            return chromosome;

        //Organiza as restrições baseado nas que os professores tem mais disponibilidades e pega a restrição violada a ser resolvida
        ViolatedConstraint currentViolatedConstraint = violatedConstraints.get(violatedConstraintsIndex);
        List<Integer> sortedConflicts = currentViolatedConstraint.getConflictedClassWithGreaterAvailableTime(dtoifsc, chromosome);
        for (Integer courseWithConflict : sortedConflicts) {

            for (int j = 0; j < chromosome.getGenes().length; j += 10) {

                //Se é um possível conflito
                if (this.dtoitc.isLessonInCourse(chromosome.getGenes()[j], courseWithConflict)) {

                    //Tenta gerar um filho não gerando um outro conflito
                    Chromosome child = generateChild(chromosome, j, currentViolatedConstraint, perfectResult, geneIndex);

                    //Se o filho gerou outro conflito
                    if (child == null) {
                        chromosomeStack.pop();
                        return depthSearchTree(chromosomeStack, violatedConstraints, violatedConstraintsIndex, perfectResult, ++geneIndex);
                    } else {
                        chromosomeStack.push(child);
                        return depthSearchTree(chromosomeStack, violatedConstraints, ++violatedConstraintsIndex, perfectResult, 0);
                    }
                }
            }
        }
        return chromosome;
    }

    /**
     * Gera um filho, ou seja, tenta fazer uma mudança que resolva um conflito.
     *
     * @param chromosome           {@link Chromosome} que será resolvido o conflito.
     * @param classInitialPosition primeira posição da turma.
     * @param violatedConstraint   {@link ViolatedConstraint} que será resolvida
     * @param perfectResult        valor base inicial do cromossomo, ou seja, seu resultado sem conflitos.
     * @param geneIndex            posição do gene com o conflito a ser tratado.
     * @return {@link Chromosome} caso conseguiu resolver o conflito sem causar outro ou null caso contrario.
     * @throws ClassNotFoundException Caso alguma entidade não seja encontrada.
     */
    private Chromosome generateChild(Chromosome chromosome, int classInitialPosition, ViolatedConstraint violatedConstraint, int perfectResult, int geneIndex) throws ClassNotFoundException {
        int conflictPosition = violatedConstraint.getChromosomePositionByDayPeriod() + classInitialPosition;
        int possibleChildIndex = geneIndex + classInitialPosition;

        //Conflito de horários, então a partir de uma posição, vai vericando de 10 em 10, de turma em turma
        for (int i = possibleChildIndex; i < classInitialPosition + 10; i++) {

            if (i != conflictPosition && chromosome.getGenes()[i] != 0) {

                Chromosome possibleChild = new Chromosome(chromosome.getGenes(), chromosome.getAvaliation());

                //Faz uma troca da posição do conflito com uma outra
                int aux = possibleChild.getGenes()[conflictPosition];
                possibleChild.getGenes()[conflictPosition] = possibleChild.getGenes()[i];
                possibleChild.getGenes()[i] = aux;

                //Verifica se essa troca não gerou outro conflito
                if (Avaliation.rateConflicts(possibleChild, this.dtoitc, new int[]{conflictPosition, i})) {
                    Avaliation.rate(possibleChild, this.dtoitc, perfectResult, false);
                    return possibleChild;
                }
            }
        }
        return null;
    }
}
