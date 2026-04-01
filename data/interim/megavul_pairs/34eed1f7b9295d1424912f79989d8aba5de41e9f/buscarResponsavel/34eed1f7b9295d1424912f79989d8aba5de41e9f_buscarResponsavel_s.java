class buscarResponsavel {
public ArrayList<Responsavel> buscarResponsavel(String email, String nome, String telefone, String endereco) throws Exception{
		
		if ((email == null) && (nome == null) && (telefone == null) && (endereco == null))
			throw new Exception("Preencha pelo menos um dos campos para realizar a busca");
		
		String cmd = "";
		
		cmd += "select * from ACI_Responsavel where ";
		
		if (email != null) {
			cmd += "email like '%"+email+"%'";
			if ((nome != null) || (telefone != null) || (endereco != null))
				cmd += " and ";
		}
			
		if (nome != null) {
			cmd += "nome like '%"+nome+"%'";
			if ((telefone != null) || (endereco != null))
				cmd += " and ";
		}	

		if (telefone != null) {
			cmd += "telefone like '%"+telefone+"%'";
			if (endereco != null)
				cmd += " and ";
		}
		
		if (endereco != null)
			cmd += "endereco like '%"+endereco+"%'";
		
		ResultSet result = this.bancoConec.execConsulta(cmd);
		
		if (result.first()) {
			String rEmail, rNome, rTelefone, rEndereco;
				ArrayList<Responsavel> aResp = new ArrayList<Responsavel>();
				rEmail = result.getString("email");
				rNome = result.getString("nome");
				rTelefone = result.getString("telefone");
				rEndereco = result.getString("endereco");
				Responsavel r = new Responsavel(rEmail,rNome,rTelefone,rEndereco);
				aResp.add(r);
				
				while (result.next()) {
					rEmail = result.getString("email");
					rNome = result.getString("nome");
					rTelefone = result.getString("telefone");
					rEndereco = result.getString("endereco");
					Responsavel resp = new Responsavel(rEmail,rNome,rTelefone,rEndereco);
					aResp.add(resp);
				}
				
				return aResp;
		} else {
			throw new Exception("Nenhum resultado");
		}
	}
}
