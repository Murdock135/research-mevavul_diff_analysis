class buscarFuncionarioNome {
public List<Cliente> buscarFuncionarioNome(String nome) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();
            nome = "%"+nome+"%";
            stmt = con.prepareStatement(stmtBuscarNomeFuncionario);
            stmt.setString(1,nome);
            rs = stmt.executeQuery();
            return montaListaClientes(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
                System.out.println("Erro ao fechar result set.Erro: " + ex.getMessage());
            }
            try {
                stmt.close();
            } catch (SQLException ex) {
                System.out.println("Erro ao fechar statement. Ex = " + ex.getMessage());
            }
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println("Erro ao fechar a conexao. Ex = " + ex.getMessage());
            }
        }
    }
}
