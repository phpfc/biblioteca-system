package com.biblioteca;

import java.util.Date;

public class Emprestimo {
	private Leitor leitor;
	private Livro livro;
	private Date dataEmprestimo;
	private Date dataDevolucao;
	
	public Emprestimo(Leitor leitor, Livro livro, Date dataEmprestimo, Date dataDevolucao) {
		this.leitor = leitor;
		this.livro = livro;
		this.dataEmprestimo = dataEmprestimo;
		this.dataDevolucao = dataDevolucao;
	}
	
	public Leitor getLeitor() {
		return leitor;
	}
	public void setLeitor(Leitor leitor) {
		this.leitor = leitor;
	}
	public Livro getLivro() {
		return livro;
	}
	public void setLivro(Livro livro) {
		this.livro = livro;
	}
	public Date getDataEmprestimo() {
		return dataEmprestimo;
	}
	public void setDataEmprestimo(Date dataEmprestimo) {
		this.dataEmprestimo = dataEmprestimo;
	}
	public Date getDataDevolucao() {
		return dataDevolucao;
	}
	public void setDataDevolucao(Date dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}


	@Override
	public String toString() {
		String status = dataDevolucao == null ? "Em andamento" : "Devolvido";
		return "Livro: " + livro.getTitulo() +
				"\nData do empréstimo: " + dataEmprestimo +
				"\nStatus: " + status +
				(dataDevolucao != null ? "\nData de devolução: " + dataDevolucao : "") +
				"\n-----------------------------";
	}
}
