package com.biblioteca.models;

public class Leitor {
	private String codigo;
	private String nome;
	private String email;
	private static int autoincremento = 1;

	public Leitor(String nome, String email) {
		this.nome = nome;
		this.email = email;
		this.codigo = gerarCodigo();
	}

	private String gerarCodigo() {
		return String.format("L%04d", autoincremento++);
	}

	public String getNome() { return nome; }
	public String getEmail() { return email; }

	@Override
	public String toString() {
		return "Leitor [codigo=" + codigo + ", nome=" + nome + ", email=" + email + "]";
	}
}