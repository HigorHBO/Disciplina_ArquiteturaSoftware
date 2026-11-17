package br.com.empresa.estoque.controller;

import br.com.empresa.estoque.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final ProdutoService produtoService;

    public EstoqueController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String telaEstoque(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "estoque";
    }

    @PostMapping("/movimentar")
    public String movimentarEstoque(
            @RequestParam("produtoId") Long produtoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("quantidade") int quantidade,
            RedirectAttributes redirectAttributes) {

        try {
            if ("ENTRADA".equalsIgnoreCase(tipo)) {
                produtoService.darEntrada(produtoId, quantidade);
                redirectAttributes.addFlashAttribute("mensagemSucesso", 
                        "Entrada de " + quantidade + " unidade(s) realizada com sucesso!");
            } else if ("SAIDA".equalsIgnoreCase(tipo)) {
                produtoService.darSaida(produtoId, quantidade);
                redirectAttributes.addFlashAttribute("mensagemSucesso", 
                        "Saída de " + quantidade + " unidade(s) realizada com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Tipo de movimentação inválido.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/estoque";
    }
}
