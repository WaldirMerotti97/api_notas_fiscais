package br.com.roletodoces.apinfe.adapters.service.notaFiscal

import br.com.roletodoces.apinfe.domain.entity.InformacoesNotaFiscal
import br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.output.MontadorNotaFiscal
import br.com.swconsultoria.nfe.schema_4.enviNFe.TInfRespTec
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe
import org.springframework.stereotype.Service

@Service
class MontadorNotaFiscalImpl(
    private val identificador: TNFe.InfNFe.Ide,
    private val emitente: TNFe.InfNFe.Emit,
    private val destinatario: TNFe.InfNFe.Dest,
    private val detalhesProdutos: TNFe.InfNFe.Det,
    private val transportadora: TNFe.InfNFe.Transp,
    private val pagamento: TNFe.InfNFe.Pag,
    private val responsavelTecnico: TInfRespTec,
    private val totalNFe: TNFe.InfNFe.Total
) : MontadorNotaFiscal {

    override fun montarNotaFiscal(informacoesNotaFiscal: InformacoesNotaFiscal) {

    }

}