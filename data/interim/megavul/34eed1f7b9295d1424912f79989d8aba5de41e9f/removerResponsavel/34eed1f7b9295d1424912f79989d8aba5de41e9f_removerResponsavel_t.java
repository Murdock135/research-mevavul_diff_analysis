class removerResponsavel {
public void removerResponsavel(String emailResponsavel) throws Exception{
		ResultSet result = this.bancoConec.execConsulta("Select * from ACI_Responsavel where Email='"
				+emailResponsavel.replace("'", "")+"'");
		
		if(!result.first()){
			result.close();
			throw new Exception("Responsável com esse Email inexistente");
		}
		result.close();
		
		String comSql = "select * from ACI_Aluno where Responsavel='" + emailResponsavel + "'";
		result = this.bancoConec.execConsulta(comSql);
		
		if(result.first()){
			result.close();
			throw new Exception("Esse Responsável Possui Alunos Cadastrados, Delete Os Alunos Primeiro");
		}
		
		result.close();
		
		comSql = "delete from ACI_Responsavel where Email='"+emailResponsavel + "'";
		this.bancoConec.execComando(comSql);
	}
}
