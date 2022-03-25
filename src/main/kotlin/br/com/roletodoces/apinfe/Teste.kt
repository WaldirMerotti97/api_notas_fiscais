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

        cnpj = "42306334000138"
        modelo = "55"
        serie = 1
        numero = 1
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
        var retornoConsulta: TRetConsReciNFe? = null
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
        nfe.infNFe = infNFe
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

        icmsTot.vbc = "0.00"
        icmsTot.vicms = "0.00"
        icmsTot.vicmsDeson = "0.00"
        icmsTot.vfcp = "0.00"
        icmsTot.vbcst = "0.00"
        icmsTot.vst = "0.00"
        icmsTot.vfcpstRet = "0.00"
        icmsTot.vfcpst = "0.00"
        icmsTot.vProd = "24.00"
        icmsTot.vFrete = "0.00"
        icmsTot.vSeg = "0.00"
        icmsTot.vDesc = "0.00"
        icmsTot.vii = "0.00"
        icmsTot.vipi = "0.00"
        icmsTot.vipiDevol = "0.00"
        icmsTot.vpis = "0.00"
        icmsTot.vcofins = "0.00"
        icmsTot.vOutro = "0.00"
        icmsTot.vnf = "24.00"
        total.icmsTot = icmsTot

        return total

    }

    private fun montaRespTecnico(): TInfRespTec? {

        val respTec = TInfRespTec()

        respTec.cnpj = "42306334000138"
        respTec.xContato = "Andre Volk Yoshida"
        respTec.fone = "11933792174"
        respTec.email = "andre.volk@hotmail.com"

        return respTec
    }

    private fun montaPagamento(): TNFe.InfNFe.Pag? {

        val pag = TNFe.InfNFe.Pag()
        val detPag = TNFe.InfNFe.Pag.DetPag()

        detPag.tPag = "15"
        detPag.vPag = "24.00"
        pag.detPag.add(detPag)

        return pag

    }

    private fun montaTransportadora(): TNFe.InfNFe.Transp? {

        val transp = TNFe.InfNFe.Transp()
        val volume = TNFe.InfNFe.Transp.Vol()
        volume.qVol = "2"
        volume.pesoL = "0.000"
        volume.pesoB = "0.000"
        transp.modFrete = "9"
        transp.vol.add(volume)
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
        pisAliq.vbc = "0.00"
        pisAliq.ppis = "0.0000"
        pisAliq.vpis = "0.00"
        pis.pisAliq = pisAliq

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis))

    }

    private fun criaImpostoCofins(imposto: TNFe.InfNFe.Det.Imposto) {

        val cofins = TNFe.InfNFe.Det.Imposto.COFINS()
        val cofinsAliq = TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq()

        cofinsAliq.cst = "01"
        cofinsAliq.vbc = "0.00"
        cofinsAliq.pcofins = "0.0000"
        cofinsAliq.vcofins = "0.00"
        cofins.cofinsAliq = cofinsAliq

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins))

    }

    private fun criaImpostoIcms(imposto: TNFe.InfNFe.Det.Imposto) {
        val icms = TNFe.InfNFe.Det.Imposto.ICMS()
        val icmssn102 = TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102()

        icmssn102.orig = "0"
        icmssn102.csosn = "400"

        icms.icmssN102 = icmssn102

        imposto.content.add(ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms))
    }

    private fun montaProduto(): TNFe.InfNFe.Det.Prod {
        val produto = TNFe.InfNFe.Det.Prod()

        produto.cProd = "07"
        produto.cean = "0606529014575"
        produto.xProd = "ROLETO GOIABADA 270 GRAMAS"
        produto.ncm = "19059090"
        produto.cfop = "5101"
        produto.uCom = "UN"
        produto.qCom = "2.00"
        produto.vUnCom = "12.00"
        produto.vProd = "24.00"
        produto.ceanTrib = "0606529014575"
        produto.uTrib = "UN"
        produto.qTrib = "2.00"
        produto.vUnTrib = "12.00"
        produto.indTot = "1"
        produto.xPed = "826"
        produto.nItemPed = "1"

        return produto
    }

    private fun montaDestinatario(): TNFe.InfNFe.Dest? {

        val dest = TNFe.InfNFe.Dest()

        dest.xNome = "NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL"
        dest.cnpj = "24321644000140"
        dest.ie = "140592692115"
        dest.indIEDest = "1"

        val enderecoDestinatario = TEndereco()
        enderecoDestinatario.nro = "132"
        enderecoDestinatario.xLgr = "R DA GLORIA"
        enderecoDestinatario.xBairro = "LIBERDADE"
        enderecoDestinatario.cMun = "3550308"
        enderecoDestinatario.xMun = "Sao Paulo"
        enderecoDestinatario.uf = TUf.valueOf(configuracoesNfe.estado.name)
        enderecoDestinatario.cep = "01510000"
        enderecoDestinatario.cPais = "1058"
        enderecoDestinatario.xPais = "Brasil"
        enderecoDestinatario.cPais = "1058"
        enderecoDestinatario.fone = "1199999999"
        dest.enderDest = enderecoDestinatario

        return dest

    }

    private fun montaEmitente(): TNFe.InfNFe.Emit? {

        val emit = TNFe.InfNFe.Emit()

        emit.xNome = "ROSE ALICE VOLK"
        emit.xFant = "ROLETO DOCES"
        emit.cnpj = cnpj
        emit.ie = "799432127110"
        emit.crt = "1"
        val enderecoEmitente = TEnderEmi()
        enderecoEmitente.nro = "611"
        enderecoEmitente.xLgr = "Rua Vicente de Carvalho"
        enderecoEmitente.xCpl = "Casa B"
        enderecoEmitente.xBairro = "Anchieta"
        enderecoEmitente.cMun = "3548708"
        enderecoEmitente.xMun = "Sao Bernardo do Campo"
        enderecoEmitente.uf = TUfEmi.valueOf(configuracoesNfe.estado.name)
        enderecoEmitente.cep = "09732600"
        enderecoEmitente.fone = "11984291259"
        enderecoEmitente.cPais = "1058"
        enderecoEmitente.xPais = "Brasil"
        emit.enderEmit = enderecoEmitente

        return emit

    }

    private fun montaIde(): TNFe.InfNFe.Ide? {

        val ide = TNFe.InfNFe.Ide()

        ide.cuf = configuracoesNfe.estado.codigoUF
        ide.cnf = cnf
        ide.natOp = "VENDA DE PRODUCAO DO ESTABELECIMENTO"
        ide.mod = modelo
        ide.serie = serie.toString()
        ide.nnf = numero.toString()
        ide.dhEmi = XmlNfeUtil.dataNfe(dataEmissao)
        ide.tpNF = "1"
        ide.idDest = "1"
        ide.cMunFG = "3548708"
        ide.tpImp = "1"
        ide.tpEmis = tipoEmissao
        ide.cdv = chaveUtil.digitoVerificador
        ide.tpAmb = configuracoesNfe.ambiente.codigo
        ide.finNFe = "1"
        ide.indFinal = "0"
        ide.indPres = "1"
        ide.procEmi = "0"
        ide.verProc = "3.10.37"

        return ide
    }

    private fun criaConfiguracoes() {
        val certificado = CertificadoService.certificadoPfx("certs/certificado.pfx", "ebxjcq86")
        configuracoesNfe = ConfiguracoesNfe.criarConfiguracoes(
            EstadosEnum.SP,
            AmbienteEnum.HOMOLOGACAO,
            certificado,
            "schemas"
        )
    }


}