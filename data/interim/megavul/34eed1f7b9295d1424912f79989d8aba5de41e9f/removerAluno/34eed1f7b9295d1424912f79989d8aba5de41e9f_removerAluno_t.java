class removerAluno {
public void removerAluno(String RA)throws Exception{
		// checa se o aluno já foi cadastrado
		String comSql = "select * from ACI_Aluno where RA='" + RA.replace("'", "") + "'";
		ResultSet result = this.bancoConec.execConsulta(comSql);
		if(!result.first()){
			result.close();
			throw new Exception("Aluno Com Esse RA Não Existente");
		}
		result.close();
		
		comSql = "delete from ACI_Aluno where RA='" + RA + "'";
		this.bancoConec.execComando(comSql);
	}
}
