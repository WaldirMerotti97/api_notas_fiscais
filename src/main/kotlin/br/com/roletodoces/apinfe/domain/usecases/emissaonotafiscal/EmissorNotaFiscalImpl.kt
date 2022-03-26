package br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal

import br.com.roletodoces.apinfe.domain.entity.InformacoesNotaFiscal
import br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.input.EmissaoNotaFiscal
import br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.output.MontadorNotaFiscal
import org.springframework.stereotype.Service

@Service
class EmissaoNotaFiscalImpl(
    private val montadorNotaFiscal: MontadorNotaFiscal
) : EmissaoNotaFiscal {

    override fun emitir(informacoesNotaFiscal: InformacoesNotaFiscal) {

        montadorNotaFiscal.montarNotaFiscal(informacoesNotaFiscal)

    }

}