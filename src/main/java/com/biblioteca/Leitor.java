package com.biblioteca;

public class Leitor {
	private String codigo;
	private String nome;
	private String email;
	private static long autoincremento = System.currentTimeMillis();

	public Leitor(String nome, String email) {
		this.nome = nome;
		this.email = email;
		this.codigo = gerarCodigo();
	}

	public String getCodigo() {
		return codigo;
	}
	private String gerarCodigo() {
		return "L" + (autoincremento++);
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
