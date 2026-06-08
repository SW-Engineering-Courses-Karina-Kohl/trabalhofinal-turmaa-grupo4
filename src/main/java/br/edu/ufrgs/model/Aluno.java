package br.edu.ufrgs.model;

public class Aluno implements AvaliavelAluno {
    private String nome;
    private double nota;

    public Aluno(String nome, double nota) {
        this.nome = nome;
        this.nota = nota;
    }

    @Override
    public String verificarSituacao() {
        return (this.nota >= 6.0) ? "Aprovado(a)" : "Reprovado(a)";
    }

    @Override
    public String getMensagemFinal() {
        return "O aluno " + nome + " está " + verificarSituacao() + " com nota " + nota;
    }
}
