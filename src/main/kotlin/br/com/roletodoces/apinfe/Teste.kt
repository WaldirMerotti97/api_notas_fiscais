package br.com.roletodoces.apinfe

import br.com.swconsultoria.certificado.CertificadoService
import br.com.swconsultoria.nfe.Nfe
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum
import br.com.swconsultoria.nfe.schema_4.enviNFe.*
import br.com.swconsultoria.nfe.schema_4.retConsReciNFe.TRetConsReciNFe
import br.com.swconsultoria.nfe.util.ChaveUtil
import br.com.swconsultoria.nfe.util.ConstantesUtil
import br.com.swconsultoria.nfe.util.RetornoUtil
import br.com.swconsultoria.nfe.util.XmlNfeUtil
import java.time.LocalDateTime
import java.util.*

class Teste {

    companion object {
        private lateinit var chaveUtil: ChaveUtil
        private lateinit var configuracoesNfe: ConfiguracoesNfe
        private lateinit var cnpj: String
        private lateinit var modelo: String
        private lateinit var tipoEmissao: String
        private lateinit var cnf: String
        private var serie = -1
        private var numero = -1
        private lateinit var dataEmissao: LocalDateTime
    }


    fun facade() {

        try {
            emiteNfe();
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun emiteNfe() {

        cnpj = "cpnj roleto"
        modelo = "55"
        serie = 1
        numero = 92723
        dataEmissao = LocalDateTime.now()
        tipoEmissao = "1"
        cnf = String.format("%08d", Random().nextInt(99999999))

        // Inicia as configuracoes
        criaConfiguracoes()

        // Inicia chave
        chaveUtil = montaChaveNFe(configuracoesNfe)
        // Cria dados da nota
        var enviNFe = criaEnviNfe()
        // Efetua assinatura e liberacao
        enviNFe = Nfe.montaNfe(configuracoesNfe, enviNFe, true)

        // Envio da nota fiscal
        val retorno = Nfe.enviarNfe(configuracoesNfe, enviNFe, DocumentoEnum.NFE)

        //Faz a verificacao se o retorno eh assincrono e consulta o recibo
        if (RetornoUtil.isRetornoAssincrono(retorno)) {
            val tRetConsReciNFe = verificaEnvioAssincrono(retorno)
            RetornoUtil.validaAssincrono(tRetConsReciNFe)
            println("Status: ${tRetConsReciNFe!!.protNFe[0].infProt.cStat}")
            println("Protocolo: ${tRetConsReciNFe.protNFe[0].infProt.nProt}")
            val xmlFinal = XmlNfeUtil.criaNfeProc(enviNFe, tRetConsReciNFe.protNFe[0])
            println("Xml Final: ${xmlFinal}")
        } else {
            RetornoUtil.validaSincrono(retorno)
            println("Status: ${retorno.protNFe.infProt.cStat}")
            println("Protocolo: ${retorno.protNFe.infProt.nProt}")
            val xmlFinal = XmlNfeUtil.criaNfeProc(enviNFe, retorno.protNFe)
            println("Xml Final: ${xmlFinal}")
        }


    }

    private fun verificaEnvioAssincrono(retorno: TRetEnviNFe): TRetConsReciNFe? {

        val recibo = retorno.infRec.nRec

        var tentativa: Int = 0
        var retornoConsulta: br.com.swconsultoria.nfe.schema_4.retConsReciNFe.TRetConsReciNFe? = null
        while (tentativa < 10) {
            retornoConsulta = Nfe.consultaRecibo(configuracoesNfe, recibo, DocumentoEnum.NFE)
            if (retornoConsulta.cStat.equals(StatusEnum.LOTE_EM_PROCESSAMENTO.codigo)) {
                Thread.sleep(1000)
                tentativa++
            } else {
                break;
            }

        }

        return retornoConsulta

    }

    private fun montaChaveNFe(configuracoesNfe: ConfiguracoesNfe?) = ChaveUtil(
        configuracoesNfe?.estado,
        cnpj,
        modelo,
        serie,
        numero,
        tipoEmissao,
        cnf,
        dataEmissao
    )

    private fun criaEnviNfe(): TEnviNFe {
        val enviNFe = TEnviNFe()
        enviNFe.versao = ConstantesUtil.VERSAO.NFE
        enviNFe.idLote = "1"
        enviNFe.indSinc = "1"

        val nfe = TNFe()
        val infNFe = getInfNFe()


        enviNFe.nFe.add(nfe)

        return enviNFe
    }

    private fun getInfNFe(): TNFe.InfNFe {
        val infNFe = TNFe.InfNFe()

        infNFe.id = chaveUtil.chaveNF
        infNFe.versao = ConstantesUtil.VERSAO.NFE
        infNFe.ide = montaIde()
        infNFe.emit = montaEmitente()
        infNFe.dest = montaDestinatario()
        infNFe.det.addAll(montaDet())
        infNFe.transp = montaTransportadora()
        infNFe.pag = montaPagamento()
        infNFe.infRespTec = montaRespTecnico()
        infNFe.total = montaTotal()

        return infNFe
    }

    private fun montaTotal(): TNFe.InfNFe.Total? {

        val total = TNFe.InfNFe.Total()

        val icmsTot = TNFe.InfNFe.Total.ICMSTot()
        icmsTot.vbc = "10.00"
        icmsTot.vicms = "1.00"
        icmsTot.vicmsDeson = "0.00"
        icmsTot.vfcp = "0.00"
        icmsTot.vbcst = "0.00"
        icmsTot.vst = "0.00"
        icmsTot.vfcpstRet = "0.00"
        icmsTot.vfcpst = "0.00"
        icmsTot.vProd = "10.00"
        icmsTot.vFrete = "0.00"
        icmsTot.vSeg = "0.00"
        icmsTot.vDesc = "0.00"
        icmsTot.vii = "0.00"
        icmsTot.vipi = "0.00"
        icmsTot.vipiDevol = "0.00"
        icmsTot.vpis = "0.17"
        icmsTot.vcofins = "0.76"
        icmsTot.vOutro = "0.00"
        icmsTot.vnf = "10.00"
        total.icmsTot = icmsTot

        return total

    }

    private fun montaRespTecnico(): TInfRespTec? {

        val respTec = TInfRespTec()

        respTec.cnpj = ""
        respTec.xContato = ""
        respTec.fone = ""
        respTec.email = ""

        return respTec
    }

    private fun montaPagamento(): TNFe.InfNFe.Pag? {

        val pag = TNFe.InfNFe.Pag()

        val detPag = TNFe.InfNFe.Pag.DetPag()
        detPag.vPag = "01"
        detPag.tPag = "10.00"
        pag.detPag.add(detPag)


        return pag

    }

    private fun montaTransportadora(): TNFe.InfNFe.Transp? {

        val transp = TNFe.InfNFe.Transp()

        transp.modFrete = "9"

        return transp


    }

    private fun montaDet(): List<TNFe.InfNFe.Det> {

        val listaProdutos = mutableListOf<TNFe.InfNFe.Det>()

        val det = TNFe.InfNFe.Det()
        det.nItem = "1"

        det.prod = montaProduto()
        det.imposto = montaImposto()

        listaProdutos.add(det)

        return listaProdutos


    }

    private fun montaImposto(): TNFe.InfNFe.Det.Imposto? {

        val imposto = TNFe.InfNFe.Det.Imposto()
        criaImpostoIcms(imposto)
        criaImpostoPis(imposto)
        criaImpostoCofins(imposto)

        return imposto

    }

    private fun criaImpostoPis(imposto: TNFe.InfNFe.Det.Imposto) {

        val pis = TNFe.InfNFe.Det.Imposto.PIS()
        val pisAliq = TNFe.InfNFe.Det.Imposto.PIS.PISAliq()

        pisAliq.cst = "01"
        pisAliq.vbc = "10.00"
        pisAliq.ppis = "1.65"
        pisAliq.vpis = "0.16"
        pis.pisAliq = pisAliq

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis))

    }

    private fun criaImpostoCofins(imposto: TNFe.InfNFe.Det.Imposto) {

        val cofins = TNFe.InfNFe.Det.Imposto.COFINS()
        val cofinsAliq = TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq()

        cofinsAliq.cst = "01"
        cofinsAliq.vbc = "10.00"
        cofinsAliq.pcofins = "7.60"
        cofinsAliq.vcofins = "0.76"
        cofins.cofinsAliq = cofinsAliq

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins))

    }

    private fun criaImpostoIcms(imposto: TNFe.InfNFe.Det.Imposto) {
        val icms = TNFe.InfNFe.Det.Imposto.ICMS()
        val icms00 = TNFe.InfNFe.Det.Imposto.ICMS.ICMS00()

        icms00.orig = "0"
        icms00.modBC = "0"
        icms00.cst = "00"
        icms00.vbc = "10.00"
        icms00.picms = "10"
        icms00.vicms = "1"

        icms.icmS00 = icms00

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms))
    }

    private fun montaProduto(): TNFe.InfNFe.Det.Prod {
        val produto = TNFe.InfNFe.Det.Prod()

        produto.cProd = "123"
        produto.cean = "Sem GTIN -> codigo de barras"
        produto.xProd = "Produto x"
        produto.ncm = "27101932"
        produto.cest = "028297392"
        produto.indEscala = "S"
        produto.cfop = "5405"
        produto.uCom = "UN"
        produto.qCom = "1"
        produto.vUnCom = "10"
        produto.vProd = "10.00"
        produto.ceanTrib = "Sem GTIN"
        produto.uTrib = "UN"
        produto.qTrib = "1"
        produto.vUnTrib = "10"
        produto.indTot = "1"

        return produto
    }

    private fun montaDestinatario(): TNFe.InfNFe.Dest? {

        val dest = TNFe.InfNFe.Dest()

        val emit = TNFe.InfNFe.Emit()

        dest.xNome = "Nome empresa"
        dest.cnpj = cnpj
        dest.ie = "Inscrição estadual"
        dest.indIEDest = "1"

        val enderecoEmitente = TEndereco()
        enderecoEmitente.nro = "0"
        enderecoEmitente.xLgr = "Rua Teste"
        enderecoEmitente.xCpl = "Qd 1 Lote 1"
        enderecoEmitente.xBairro = "Centro"
        enderecoEmitente.cMun = "Codigo Municipio"
        enderecoEmitente.xMun = "Municipio de acordo com o IBGE"
        enderecoEmitente.uf = TUf.valueOf(configuracoesNfe.estado.name)
        enderecoEmitente.cep = "09732600"
        dest.enderDest = enderecoEmitente

        return dest

    }

    private fun montaEmitente(): TNFe.InfNFe.Emit? {

        val emit = TNFe.InfNFe.Emit()

        emit.xNome = "Nome empresa"
        emit.cnpj = cnpj
        emit.ie = "Inscrição estadual"
        emit.crt = "3"
        val enderecoEmitente = TEnderEmi()
        enderecoEmitente.nro = "0"
        enderecoEmitente.xLgr = "Rua Teste"
        enderecoEmitente.xCpl = "Qd 1 Lote 1"
        enderecoEmitente.xBairro = "Centro"
        enderecoEmitente.cMun = "Codigo Municipio"
        enderecoEmitente.xMun = "Municipio de acordo com o IBGE"
        enderecoEmitente.uf = TUfEmi.valueOf(configuracoesNfe.estado.name)
        enderecoEmitente.cep = "09732600"
        emit.enderEmit = enderecoEmitente

        return emit

    }

    private fun montaIde(): TNFe.InfNFe.Ide? {

        val ide = TNFe.InfNFe.Ide()

        ide.cuf = configuracoesNfe.estado.codigoUF
        ide.cnf = cnf
        ide.natOp = "Venda NFe"
        ide.mod = modelo
        ide.serie = serie.toString()
        ide.nnf = numero.toString()
        ide.dhEmi = XmlNfeUtil.dataNfe(dataEmissao)
        ide.tpNF = "1"
        ide.idDest = "2"
        ide.cMunFG = "5219753"
        ide.tpImp = "1"
        ide.tpEmis = tipoEmissao
        ide.cdv = chaveUtil.digitoVerificador
        ide.tpAmb = configuracoesNfe.ambiente.codigo
        ide.finNFe = "1"
        ide.indFinal = "1"
        ide.indPres = "1"
        ide.procEmi = "0"
        ide.verProc = "1.0.0"

        return ide
    }

    private fun criaConfiguracoes() {
        val certificado = CertificadoService.certificadoPfx("/d/teste/certificado.pfx", "12345")
        configuracoesNfe = ConfiguracoesNfe.criarConfiguracoes(
            EstadosEnum.GO,
            AmbienteEnum.HOMOLOGACAO,
            certificado,
            "/d/teste/schemas"
        )
    }


}