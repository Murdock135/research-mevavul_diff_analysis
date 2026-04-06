class buscarClienteEmail {
public List<Cliente> buscarClienteEmail(String email) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(stmtBuscarEmail + "'%" + email + "%' and perfil = 1 and inativo=false order by nome");
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
