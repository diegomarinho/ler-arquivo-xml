package br.com.mv.lerarquivoxml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.mv.lerarquivoxml.repositorio.pool.DBConnectionPool;
import br.com.mv.lerarquivoxml.xml.LeituraArquivoXML;

/**
 * @author diego.almeida
 * @since 11/10/2016
 *
 */
public class App {

	private static final String TAG_XML_ALVO_1  = "IdentificacaoNfse";			// Retorna informações do Identificacao Nfse (Retorno da prefeitura)
	private static final String TAG_XML_ALVO_2  = "PrestadorServico";			// Retorna informações da Razao Social do Prestador
	private static final String TAG_XML_ALVO_3  = "IdentificacaoPrestador";		// Retorna informações da Cpf/Cnpj do Prestador
	private static final String TAG_XML_ALVO_4  = "TomadorServico";				// Retorna informações da Razao Social do Tomador
	private static final String TAG_XML_ALVO_5  = "IdentificacaoTomador";		// Retorna informações da Cpf/Cnpj do Tomador
	private static final String TAG_XML_ALVO_6  = "Nfse";						// Retorna informações da Nota

	/**
	 * [ETAPA 1]
	 * 
	 * @param args[0] Nome do arquivo XML que ser� processado.
	 */
	public static void main(String[] args) {
		
		try {
			
			//args[0] = "C:\\RETORNO_CAJAMAR.xml"
			List<String> obterElementosRetorno = new LeituraArquivoXML().obterElementosRetorno(args[0], TAG_XML_ALVO_1, TAG_XML_ALVO_2, TAG_XML_ALVO_3, TAG_XML_ALVO_4, TAG_XML_ALVO_5, TAG_XML_ALVO_6);
			
			if (obterElementosRetorno != null) {
				
				int numNfse = Integer.parseInt(obterElementosRetorno.get(0).substring(30, obterElementosRetorno.get(0).toString().length()));
				String codVerifNfse = obterElementosRetorno.get(1).substring(30, obterElementosRetorno.get(1).toString().length());
				int numRps = Integer.parseInt(obterElementosRetorno.get(0).substring(30, obterElementosRetorno.get(0).toString().length()));
				
				// Abre conexao com banco e atualiza registro
				updateNotaFiscal(obterElementosRetorno, numNfse, codVerifNfse, numRps);
			}

		} catch (final Exception ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Arquivo de retorno com erros!", ex);
		}
	}

	private static void updateNotaFiscal(List<String> obterElementosRetorno, int numeroNfse, String codigoVerificacaoNfse, int numeroRps) {

		Connection conn = null;
		PreparedStatement preparedStmt = null;
		String updateSQL = "UPDATE DBAMV.NOTA_FISCAL SET NR_NOTA_FISCAL_NFE = ?, CD_VERIFICACAO_NFE = ? WHERE NR_ID_NOTA_FISCAL = ? ";

		try {

			conn = DBConnectionPool.getConnection();
			preparedStmt = conn.prepareStatement(updateSQL);
			preparedStmt.setInt(1, numeroNfse);
			preparedStmt.setString(2, codigoVerificacaoNfse);
			preparedStmt.setInt(3, numeroRps);
			int executeUpdate = preparedStmt.executeUpdate();
			
			if (executeUpdate == 2) {
				
				System.out.println("---------------------------------------------------------");
				System.out.println("Arquivo de retorno processado com sucesso!");
				System.out.println("---------------------------------------------------------");

				for (String string : obterElementosRetorno) {
					System.out.println(string);
				}

				System.out.println("---------------------------------------------------------");
				Logger.getLogger(App.class.getName()).info("Registro " + numeroNfse + " atualizado com sucesso!");
			}

		} catch (SQLException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (conn != null) {
				DBConnectionPool.releaseConnection(conn);
			}
		}
	}
}
