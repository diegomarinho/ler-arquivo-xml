# Objetivo
Aplicação fará o processamento de um arquivo no formato XML de retorno no padrão da prefeitura de CAJAMAR/SP (NFSE).


# Requisitos
Eclipse e Java (1.7 or superior).


# Configuração do arquivo [config.properties]
    $ Abra o config.properties e configure com os dados de conexao da base de dados desejada

# Resultado esperado
Abaixo segue exemplo via PROMPT DE COMANDO que será exibida para o usário via aplicação.
Ao final aplicação deverá exibir via PROMPT COMANDO as informações carregadas do XML e atualizar coluna NR_NOTA_FISCAL e CD_VERIFICACAO_NFE da tabela DBAMV.NOTA_FISCAL.

---------------------------------------------------------
Arquivo de retorno processado com sucesso!
---------------------------------------------------------
Numero NFSE.................: 242
Codigo de Verificacao NFSE..: FPVJHEALOS
Razao Social Prestador......: HOSPITALIS NUCLEO HOSPITALAR DE BARUERI LTDA
Cnpj Prestador..............: 59.042.622/0004-78
Razao Social Tomador........: MARCELO IANOFF DAS DORES BARATA
Cnpj Tomador................: 0092663354791
Data de Emissao NFSE........: 03/10/2016 08:34:05
---------------------------------------------------------
Out 20, 2016 9:40:50 AM br.com.mv.lerarquivoxml.App updateNotaFiscal
INFORMAÇÕES: Registro 242 atualizado com sucesso!
Pressione qualquer tecla para continuar. . .


# Exportar
    $ Abra o eclipse Eclipse vá no projeto ler-arquivo-xml
    $ Clique em cima do projeto Export...
    $ Vá na opção Runnable JAR file > Finish
    
    
# Instalação no cliente
Utilize a chave global do FFCV > DIRETORIO_SERVIDOR_NFE com o seguinte valor modificável de acordo com o cliente E:\mv2000\ffcv\ServidorNfse_sml\     