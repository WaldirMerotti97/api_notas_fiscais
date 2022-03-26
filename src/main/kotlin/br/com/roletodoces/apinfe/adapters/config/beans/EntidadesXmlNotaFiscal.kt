package br.com.roletodoces.apinfe.adapters.config.beans

import br.com.swconsultoria.nfe.schema_4.enviNFe.TInfRespTec
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EntidadesXmlNotaFiscal {

    @Bean
    fun criaIdentificador() = TNFe.InfNFe.Ide()

    @Bean
    fun criaEmitente() = TNFe.InfNFe.Emit()

    @Bean
    fun criaDestinatario() = TNFe.InfNFe.Dest()

    @Bean
    fun criaListaDetalhesProduos() = mutableListOf<TNFe.InfNFe.Det>()

    @Bean
    fun criaTransportadora() = TNFe.InfNFe.Transp()

    @Bean
    fun criaPagamento() = TNFe.InfNFe.Pag()

    @Bean
    fun criaResponsavelTecnico() = TInfRespTec()

    @Bean
    fun criaTotalNFe() = TNFe.InfNFe.Total()

}