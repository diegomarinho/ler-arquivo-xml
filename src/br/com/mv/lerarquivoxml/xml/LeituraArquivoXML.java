package br.com.mv.lerarquivoxml.xml;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe responsável por realizar a leitura do arquivo XML e retornar os seguintes campos:
 * 
 * Exemplo:
 * 
 * ---------------------------------------------------------
 * Arquivo de retorno processado com sucesso!
 * ---------------------------------------------------------
 * Número NFSE.................: 242
 * Código de Verificacao NFSE..: FPVJHEALOS
 * Razao Social Prestador......: HOSPITALIS NUCLEO HOSPITALAR DE BARUERI LTDA
 * Cnpj Prestador..............: 59.042.622/0004-78
 * Razao Social Tomador........: MARCELO IANOFF DAS DORES BARATA
 * Cnpj Tomador................: 0092663354791
 * Data de Emissao NFSE........: 03/10/2016 08:34:05
 * ---------------------------------------------------------
 * 
 * @author diego.almeida
 * @since 11/10/2016
 *
 */
public class LeituraArquivoXML {

	private static final String CONST_CODIGO_VERIFICACAO 	= "CodigoVerificacao";
	private static final String CONST_DATA_EMISSAO_NFSE		= "DataEmissao";
	private static final String CONST_NUMERO_NFSE 			= "Numero";
	private static final String CONST_RAZAO_SOCIAL 			= "RazaoSocial";
	private static final String CONST_CNPJ 					= "Cnpj";
	private static final String CONST_CPF 					= "Cpf";
	
	private List<String> listaConteudoPrefeitura 			= null;		// TAG_ALVO_1 - Retorna informações do Identificacao Nfse (Retorno da prefeitura)
	private List<String> listaConteudoRazaoSocialPrestador  = null;		// TAG_ALVO_2 - Retorna informações da Razao Social do Prestador
	private List<String> listaConteudoCpfCnpjPrestador  	= null;		// TAG_ALVO_3 - Retorna informações da Cpf/Cnpj do Prestador
	private List<String> listaConteudoRazaoSocialTomador 	= null;		// TAG_ALVO_4 - Retorna informações da Razao Social do Tomador
	private List<String> listaConteudoCpfCnpjTomador 		= null;		// TAG_ALVO_5 - Retorna informações da Cpf/Cnpj do Tomador
	private List<String> listaConteudoNota 		 			= null;		// TAG_ALVO_6 - Retorna informações da Nota

	/**
	 * [ETAPA 2]
	 * 
	 * @param nomeArquivoXml Nome do arquivo XML a ser processado.
	 * @param tagXmlAlvo	 Tag Pai do XML a ser processado como alvo na arquivo XML.
	 * @return
	 */
	public List<String> obterElementosRetorno(String nomeArquivoXml, String... tagXmlAlvo) {
		
		List<String> listaResultadoFinal = new ArrayList<String>();
		
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[0], listaConteudoPrefeitura, CONST_NUMERO_NFSE, CONST_CODIGO_VERIFICACAO));
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[1], listaConteudoRazaoSocialPrestador, CONST_RAZAO_SOCIAL));
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[2], listaConteudoCpfCnpjPrestador, CONST_CNPJ.isEmpty() ? CONST_CPF : CONST_CNPJ));
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[3], listaConteudoRazaoSocialTomador, CONST_RAZAO_SOCIAL));
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[4], listaConteudoCpfCnpjTomador, CONST_CNPJ.isEmpty() ? CONST_CPF : CONST_CNPJ));
		listaResultadoFinal.addAll(processaArvoreXmlPorTagAlvo(nomeArquivoXml, tagXmlAlvo[5], listaConteudoNota, CONST_NUMERO_NFSE, CONST_DATA_EMISSAO_NFSE));
		
		return listaResultadoFinal;
	}

	/**
	 * [ETAPA 3]
	 * 
	 * @param nomeArquivoXml Nome do arquivo XML a ser processado.
	 * @param tagXmlAlvo	 Tag Pai do XML a ser processado.
	 * @param listaRetorno	 Lista de resultados.
	 * @param tagsChaves	 Variaveis usadas para informações no processamento.
	 * @return 				 Lista de informações da NFSe para o usuário final.
	 */
	private List<String> processaArvoreXmlPorTagAlvo(String nomeArquivoXml, String tagXmlAlvo, List<String> listaRetorno, String... tagsChaves) {
		
		NodeList nodeList 	= null;
		Document documento 	= null;

		try {

			documento = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(nomeArquivoXml);
			documento.getDocumentElement().normalize();
			nodeList = documento.getElementsByTagName(tagXmlAlvo.toString());

			listaRetorno = new ArrayList<String>();

			for (int contElemento = 0; contElemento < nodeList.getLength(); contElemento++) {

				Node elementoPai = nodeList.item(contElemento);

				if (documento.hasChildNodes()) {

					for (int contSubElemento = 0; contSubElemento < elementoPai.getChildNodes().getLength(); contSubElemento++) {

						Node elementoFilho = elementoPai.getChildNodes().item(contSubElemento);

						if (elementoFilho.getNodeType() == Node.ELEMENT_NODE) {

							String nomeNodo = elementoFilho.getNodeName();

							Collections.sort(Arrays.asList(tagsChaves));
							
							for (String variavelAlvo : tagsChaves) {
								
								if (nomeNodo.equals(variavelAlvo)) {
									
									carregarDadosNfse(listaRetorno, elementoPai, elementoFilho);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listaRetorno;
	}

	private void carregarDadosNfse(List<String> listaRetorno, Node elementoPai, Node elementoFilho) {
		
		// NFSE
		if (elementoFilho.getNodeName().equals(CONST_NUMERO_NFSE)) {
			listaRetorno.add("Numero NFSE.................: " + elementoFilho.getTextContent());
		} else if (elementoFilho.getNodeName().equals(CONST_CODIGO_VERIFICACAO)) {
			listaRetorno.add("Codigo de Verificacao NFSE..: " + elementoFilho.getTextContent());
		} else if (elementoFilho.getNodeName().equals(CONST_DATA_EMISSAO_NFSE)) {
			listaRetorno.add("Data de Emissao NFSE........: " + elementoFilho.getTextContent());
		}
		
		// PRESTADOR
		if (elementoPai.getNodeName().equals("PrestadorServico")) {
			if (elementoFilho.getNodeName().equals(CONST_RAZAO_SOCIAL)) {
				listaRetorno.add("Razao Social Prestador......: " + elementoFilho.getTextContent());
			} 
		}
		
		if (elementoPai.getNodeName().equals("IdentificacaoPrestador")) {
			if (elementoFilho.getNodeName().equals(CONST_CNPJ)) {
				listaRetorno.add("Cnpj Prestador..............: " + elementoFilho.getTextContent());
			} else if (elementoFilho.getNodeName().equals(CONST_CPF)) {
				listaRetorno.add("Cpf Prestador...............: " + elementoFilho.getTextContent());
			}
		}
		
		// TOMADOR
		if (elementoPai.getNodeName().equals("TomadorServico")) {
			if (elementoFilho.getNodeName().equals(CONST_RAZAO_SOCIAL)) {
				listaRetorno.add("Razao Social Tomador........: " + elementoFilho.getTextContent());
			}
		}
		
		if (elementoPai.getNodeName().equals("IdentificacaoTomador")) {
			if (elementoFilho.getNodeName().equals(CONST_CNPJ)) {
				listaRetorno.add("Cnpj Tomador................: " + elementoFilho.getTextContent());
			} else if (elementoFilho.getNodeName().equals(CONST_CPF)) {
				listaRetorno.add("Cpf Tomador.................: " + elementoFilho.getTextContent());
			}
		}
	}
	
}