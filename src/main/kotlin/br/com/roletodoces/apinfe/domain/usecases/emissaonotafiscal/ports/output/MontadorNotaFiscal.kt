package br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.output

import br.com.roletodoces.apinfe.domain.entity.InformacoesNotaFiscal

interface MontadorNotaFiscal {

    fun montarNotaFiscal(informacoesNotaFiscal: InformacoesNotaFiscal)

}
