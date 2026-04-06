class buscarClienteCPF {
public List<Cliente> buscarClienteCPF(String cpf) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();
            cpf = "%"+cpf+"%";
            stmt = con.prepareStatement(stmtBuscarCPFCliente);
            stmt.setString(1, cpf);
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
