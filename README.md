# Algoritmo Genético distribuído para problema de Timetabling - Backend

Backend do trabalho de conclusão de curso referente ao curso de graduação Ciência da Computação.

## Execução (Centralizada e Distribuída):

### Requisitos:

- [IDE](https://www.jetbrains.com/pt-br/idea/download) para execução de aplicações Java;

- [JDK](https://www.oracle.com/br/java/technologies/javase-jdk11-downloads.html);

- Para execução centralizada, é necessário utilizar a branch master;

- Para execução distribuída, é necessário utilizar as branchs distributed-client e distributed-server para baixar o
  código das máquinas cliente e servidor, respectivamente.

### Execução:

- Caso desejado, o arquivo localizado em `src\assets\configuracoes.txt` pode ser editado para alterar os valores dos
  parâmetros de execução do sistema. Abaixo consta uma melhor explicação sobre os valores dentro do arquivo;

- Fazer a build e execução do sistema. Lembrando que caso seja executado em ambiente distribuído, primeiro devem ser
  executados os servidores para depois o cliente.

### Parâmetros de execução do sistema:

- *tamanhoPopulacao* : tamanho da população de cromossomos. Utilizado como um número inteiro.

- *tamanhoTurma* : tamanho de cada turma do IFSC. Utilizado como um número inteiro.

- *porcentagemElitismo* : valor que vai indicar quanto vai ser a "fatia de elite" da população de cromossomos. Utilizado como um
  valor em percentagem.
  - Ex.: Com tamanhoPopulacao=300 e porcentagemElitismo=10, então 10% dos 300 cromossomos serão de elite, ou seja, 30 cromossomos de elite e 270 para a roleta. 

- *porcentagemCruzamento* : valor que vai indicar a probabilidade de a etapa de cruzamento acontecer. Utilizado como um
  valor em percentagem.

- *porcentagemMutacao* : valor que vai indicar a probabilidade de a etapa de mutação acontecer. Utilizado como um valor
  em percentagem.

- *porcentagemJuncaoCurso* : valor que vai indicar a proporção de professores necessárias para realizar a junção num conjunto. Utilizado como um valor
  em percentagem.
  - Ex.: Com porcentagemJuncaoCurso=40, então cada vez que um curso é comparado com outro, digamos que numa iteração entre C1 e C2, caso 40% dos professores presentes em C1 também estejam em C2, então será gerado um conjunto entre eles.

- *totalGeracoes* : valor limite de gerações que serão processadas. Utilizado como um valor inteiro.

- *intervaloVerificacao* : valor que define a cada quantas gerações serão verificados os cromossomos, para identificar se existe uma melhora nas suas avaliações. Utilizado como um valor inteiro.