package br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.input

import br.com.roletodoces.apinfe.domain.entity.InformacoesNotaFiscal

interface EmissaoNotaFiscal {

    fun emitir(informacoesNotaFiscal: InformacoesNotaFiscal)

}