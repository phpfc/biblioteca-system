package com.biblioteca;

import java.util.Date;

public class Emprestimo {
	private Leitor leitor;
	private Livro livro;
	private Date dataEmprestimo;
	private Date dataDevolucao;

	public Emprestimo(Leitor leitor, Livro livro, Date dataEmprestimo, Date dataDevolucao) {
		if (leitor == null) {
			throw new IllegalArgumentException("Leitor não pode ser nulo");
		}
		if (livro == null) {
			throw new IllegalArgumentException("Livro não pode ser nulo");
		}
		if (dataEmprestimo == null) {
			throw new IllegalArgumentException("Data de empréstimo não pode ser nula");
		}

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

	public String informaEmprestimo() {
		StringBuilder info = new StringBuilder();
		info.append("Código do Livro: ").append(livro.getCodigoIsbn()).append("\n");
		info.append("Título: ").append(livro.getTitulo()).append("\n");
		info.append("Leitor: ").append(leitor.getNome()).append("\n");
		info.append("Email do Leitor: ").append(leitor.getEmail()).append("\n");
		info.append("Código do Leitor: ").append(leitor.getCodigo()).append("\n");
		info.append("Data do empréstimo: ").append(dataEmprestimo).append("\n");

		if (dataDevolucao != null) {
			info.append("Status: Devolvido\n");
			info.append("Data de devolução: ").append(dataDevolucao).append("\n");
		} else {
			info.append("Status: Em andamento\n");
		}

		info.append("-----------------------------\n");
		return info.toString();
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
