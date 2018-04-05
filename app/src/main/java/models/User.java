package models;

public class User {



    private String Nome;
    private String Cpf;

    public void SetNome(String nome)
    {
        this.Nome=nome;
    }
    public String getNome()
    {
        return this.Nome;
    }
    public String getCpf() {
        return Cpf;
    }

    public void setCpf(String cpf) {
        this.Cpf = cpf;
    }

}
