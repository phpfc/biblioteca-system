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

	public String getCodigo() { return codigo; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	@Override
	public String toString() {
		return "Leitor [codigo=" + codigo + ", nome=" + nome + ", email=" + email + "]";
	}
}