package br.com.empresa.estoque.controller;

import br.com.empresa.estoque.model.Produto;
import br.com.empresa.estoque.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("novoProduto", new Produto());
        return "produtos";
    }

    @PostMapping
    public String cadastrar(@ModelAttribute("novoProduto") Produto produto, RedirectAttributes redirectAttributes) {
        produtoService.cadastrarProduto(produto.getNome(), produto.getPreco());
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto cadastrado com sucesso! Status inicial: INDISPONÍVEL (estoque 0).");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/descontinuar")
    public String descontinuar(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean isAdmin, RedirectAttributes redirectAttributes) {
        try {
            produtoService.descontinuar(id, isAdmin);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto descontinuado pelo Administrador.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/produtos";
    }
}
