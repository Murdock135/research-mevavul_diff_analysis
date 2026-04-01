class getResponsavel {
public Responsavel getResponsavel(String email) throws Exception{
		ResultSet result = this.bancoConec.execConsulta("Select * from ACI_Responsavel where Email='"+email.replace("'", "")+"'");
		if(result.first()){
			Responsavel responsavel = new Responsavel(result.getString("Email"), result.getString("Nome"),result.getString("Telefone")
					,result.getString("Endereco"));
			return responsavel;
		}else{
			return null;
		}
	}
}
