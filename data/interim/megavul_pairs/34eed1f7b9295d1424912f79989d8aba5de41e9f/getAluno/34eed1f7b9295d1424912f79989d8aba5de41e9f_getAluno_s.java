class getAluno {
public Aluno getAluno(String ra)throws Exception{
		String comSql = "select * from ACI_Aluno where ra='"+ra+"'";
		ResultSet result = this.bancoConec.execConsulta(comSql);
		if(result.first()){
			Aluno aluno = new Aluno(result.getString("RA"),
					result.getString("nome"),result.getString("Email"), result.getString("telefone")
					, result.getString("Endereco"),result.getString("Responsavel"));
			result.close();
			return aluno;
		}else{
			result.close();
			return null;
		}
	}
}
