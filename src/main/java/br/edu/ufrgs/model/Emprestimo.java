package br.edu.ufrgs.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// classe emprestimo
// cada emprestimo refere-se a uma linha do arquivo csv de entrada
public class Emprestimo {
    private int id;
    private String titulo;
    private String categoria;
    private LocalDate dataPrevista;
    private LocalDate dataDevolucao;
    private long diasAtraso;
    private double valorMulta;
    
    // método construtor
    public Emprestimo(int id,String titulo, String categoria, LocalDate dataPrevista, LocalDate dataDevolucao) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.dataPrevista = dataPrevista;
        this.dataDevolucao = dataDevolucao;
        this.diasAtraso = calcularDiasAtraso();
        this.valorMulta = 0.0;
    }

    private long calcularDiasAtraso() {
        if (dataDevolucao.isAfter(dataPrevista)) {
            return ChronoUnit.DAYS.between(dataPrevista, dataDevolucao);
        } else {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public long getDiasAtraso() {
        return diasAtraso;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta;
    }
}
