package br.com.roletodoces.apinfe.adapters.entrypoint.controller

import br.com.roletodoces.apinfe.adapters.entrypoint.controller.dto.InformacoesNotaFiscalDto
import br.com.roletodoces.apinfe.domain.usecases.emissaonotafiscal.ports.input.EmissaoNotaFiscal
import org.springframework.web.bind.annotation.*
import toInformacoesNotaFiscal

@RestController
@RequestMapping("/notasFiscais")
class NFeController(
    private val emissaoNotaFiscal: EmissaoNotaFiscal
) {

    @PostMapping("/criar")
    fun salvaNotaFiscal(@RequestBody informacoesNotaFiscalDto: InformacoesNotaFiscalDto){

        emissaoNotaFiscal.emitir(informacoesNotaFiscalDto.toInformacoesNotaFiscal())
        
    }

}