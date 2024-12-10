package com.biblioteca.models;

import java.util.Date;

public class Emprestimo {
	private Leitor leitor;
	private Livro livro;
	private Date dataEmprestimo;
	private Date dataPrevistaDevolucao;
	private Date dataDevolucao;

	public Emprestimo(Leitor leitor, Livro livro, Date dataEmprestimo, Date dataPrevistaDevolucao) {
		this.leitor = leitor;
		this.livro = livro;
		this.dataEmprestimo = dataEmprestimo;
		this.dataPrevistaDevolucao = dataPrevistaDevolucao;
	}

	public Leitor getLeitor() { return leitor; }
	public void setLeitor(Leitor leitor) { this.leitor = leitor; }
	public Livro getLivro() { return livro; }
	public void setLivro(Livro livro) { this.livro = livro; }
	public Date getDataEmprestimo() { return dataEmprestimo; }
	public void setDataEmprestimo(Date dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
	public Date getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
	public void setDataPrevistaDevolucao(Date dataPrevistaDevolucao) { this.dataPrevistaDevolucao = dataPrevistaDevolucao; }
	public Date getDataDevolucao() { return dataDevolucao; }
	public void setDataDevolucao(Date dataDevolucao) { this.dataDevolucao = dataDevolucao; }

	@Override
	public String toString() {
		return "Emprestimo [leitor=" + leitor.getNome() +
				", livro=" + livro.getTitulo() +
				", dataEmprestimo=" + dataEmprestimo +
				", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
				", dataDevolucao=" + dataDevolucao + "]";
	}
}