[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/RBBavBFg)
# Gestor de Acervo e Multas 


## Objetivo

  O objetivo deste trabalho é desenvolver uma aplicação em Java para processar devoluções de livros, calculando multas por atraso baseadas em categorias e datas, automatizando o controle financeiro da biblioteca.

## Descrição do Cenário

A biblioteca possui diferentes políticas de multa para livros comuns, acadêmicos ou obras raras. O sistema deve automatizar a identificação de empréstimos que geraram multas a partir de uma base de dados em formato CSV, aplicando valores de multas diárias configurados em um arquivo externo.

## Requisitos Funcionais (RF)

  RF01 - Seleção de Arquivo: A interface deve permitir que o usuário navegue nas pastas locais e selecione um arquivo .csv contendo os registros de empréstimos.

  RF02 - Motor de Cálculo (Lógica de Negócio): O sistema deve processar cada linha e calcular os dias de atraso entre a data esperada e a data real de entrega.

    Configuração Externa: Os valores das multas diárias por categoria (ex: Acadêmico, Raro, Comum) devem ser lidos obrigatoriamente de um arquivo separado (config_biblioteca.csv).

  RF03 - Exibição em Grade: Os livros processados devem ser listados em uma tabela na interface gráfica, mostrando os dados da obra, os dias de atraso e o valor total da multa calculado.

  RF04 - Exportação de Resultados: A aplicação deve permitir salvar um novo arquivo CSV atualizado, contendo todas as colunas originais e a coluna adicional valor_multa.

### Dica

  Usem o stitch.withgoogle.com para gerar a tela. Ele vai gerar um html que vocês podem renomear para jsp e alterar para carregar os dados de vocês.

  Para poder testar várias entradas de dados diferentes, usem ferramentas de IA generativa para gerar dados sintéticos no formato de entrada que vocês precisam

## Exemplos de Arquivos

A. Arquivo de Entrada (emprestimos.csv)

id, titulo, categoria, data_esperada, data_real
01, Java Clean Code, Academico, 2026-03-20, 2026-03-25
02, Dom Casmurro, Comum, 2026-03-15, 2026-03-15
03, Manuscrito Original, Raro, 2026-03-10, 2026-03-12

B. Arquivo de Configuração (config_biblioteca.csv)

categoria, valor_diario
Academico, 2.00
Comum, 1.00
Raro, 10.00

C. Arquivo de Saída (multas_processadas.csv)

Formato esperado após o processamento pela aplicação.

id, titulo, categoria, data_esperada, data_real, valor_multa
01, Java Clean Code, Academico, 2026-03-20, 2026-03-25, 10.00
02, Dom Casmurro, Comum, 2026-03-15, 2026-03-15, 0.00
03, Manuscrito Original, Raro, 2026-03-10, 2026-03-12, 20.00

## Entregas

  1ª entrega: 06/04/2026

  - Grupos definidos, definição dos representantes dos grupos, questionários iniciais

  - Possivelmente, dia 08/04/2026, a professora irá sortear a ordem de apresentação final dos grupos.

  2ª entrega:

  - 10/05/2026: Diagrama de classes

  - 11 e 13/05/2026: reuniões com grupos que quiserem orientação. Será disponibilizado calendário

  3ª entrega: 12/06/2026

  - Código final no main do Github do grupo até às 23:59. Se tiver código comitado no main pós 23:59, vou pegar a última versão anterior a esse horário

  4ª entrega: 15 e 17/06/2026

  - Apresentações dos grupos apenas para a professora. Será agendado o tempo adequado e divulgado oportunamente



### Preparando o ambiente

Instalar o Docker Desktop.
https://www.docker.com/products/docker-desktop/
Depois de instalar, testar no terminal:
docker --version
Se aparecer a versão, está OK.
Baixar o projeto template do Github Classroom
Entrar na pasta do projeto no terminal
Exemplo:
cd Downloads/projeto-oo-web

Entendendo os arquivos do projeto

Observem que existem:

    Dockerfile

    docker-compose.yaml

    pom.xml ou estrutura Java Web

    pasta src

    talvez um .war ou estrutura para gerar
    O Dockerfile e o docker-compose.yml ensinam o Docker a montar o ambiente com Tomcat e esse projeto.

Rodando o projeto (a mágica)

Dentro da pasta do projeto:
Construir a imagem
docker-compose up --build -d
Isso significa:
"Docker, construa uma máquina com base nesse Dockerfile"
Vai demorar um pouco na primeira vez.
Abrir no navegador
http://localhost:8080
E a aplicação vai abrir

Sequencia de docker importante durante o trabalho:
docker compose down
docker compose build --no-cache
docker compose up

O que está acontecendo por baixo dos panos

Quando rodamos o que está abaixo, estamos rodando uma aplicação Java Web real.
Navegador -> localhost:8080
-> Docker Container
-> Tomcat
-> Servlet
-> JSP
-> HTML volta
Parar o container
docker compose down

Diretrizes para avaliação baseada em contribuições individuais no GitHub

! ATENÇÃO: A avaliação deste trabalho considerará não apenas o resultado final, mas também as contribuições individuais de cada membro do grupo.

Planilha com a rubrica que será usada para avaliar o trabalho estará no Moodle
A planilha apresenta todos os requisitos que serão avaliados. Em alto nível:

    Todo aluno deve ter commits registrados no repositório do grupo

    A ausência de contribuições verificáveis via GitHub resultará em redução da nota individual (ou até zerar)

    Os commits devem ser feitos com o usuário GitHub cadastrado pelo aluno no sistema
    Penalidades por falta de contribuição:

    Alunos sem nenhum commit registrado no repositório do grupo receberão nota zero na avaliação individual, independentemente da qualidade do trabalho final

    Contribuições desproporcionais entre membros do grupo (quantidade/qualidade de commits) poderão resultar em notas diferentes para cada integrante (de acordo com a fórmula apresentada na planilha)

    Commits concentrados apenas nos dias finais antes da entrega serão considerados como indicativo de participação inadequada

Recomendações:

    Distribuam as tarefas entre os membros do grupo logo no início do projeto

    Realizem commits frequentes com mensagens descritivas do que foi implementado

    Evitem que apenas um membro do grupo faça upload de todo o código

    Não compartilhem credenciais do GitHub - cada aluno deve usar sua própria conta
    Esta política visa garantir o envolvimento de todos os membros da equipe e assegurar uma avaliação justa, baseada no esforço individual no trabalho coletivo.

Passo a Passo para Aceitar o GitHub Classroom Group Assignment

    Passo 1: Acessar o link do convite

    Passo 2: Aceitar o trabalho em grupo

        Clique no botão "Accept this assignment."

    Passo 3: Selecionar ou criar um grupo (parte crucial)

        Se você for o primeiro membro do seu grupo:

            Clique em "Create a new team"

            Digite um nome para o seu grupo

                Regra para o nome do Grupo: grupoXX (onde XX é o número do seu grupo, definido no moodle)

            Clique em "Create team" (Criar equipe)

            Você receberá uma mensagem confirmando que seu grupo foi criado

        Se você for se juntar a um grupo existente:

            Na lista de grupos disponíveis, localize o nome do grupo ao qual deseja se juntar

            Clique no nome do grupo

            Clique em "Join team" (Juntar-se à equipe)

    Passo 5: Confirmação e acesso ao repositório

        Após criar ou juntar-se a um grupo, você verá uma tela de confirmação

        O GitHub Classroom criará um repositório para o seu grupo (isso pode levar alguns segundos)

        Quando estiver pronto, clique em "Acessar repositório" ou no link fornecido

    Passo 6: Trabalhar com o repositório do grupo

        Agora todos os membros do grupo têm acesso ao mesmo repositório

        Colaborem no código seguindo as práticas de Git (pull, commit, push, branch, etc.)

    Observação importante:

        Comunicação: Coordene com seus colegas antes de criar um grupo para evitar duplicações
