package br.com.empresa.vendas.controller;

import br.com.empresa.vendas.model.Pedido;
import br.com.empresa.vendas.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        model.addAttribute("novoPedido", new Pedido());
        return "pedidos";
    }

    @PostMapping
    public String criar(@ModelAttribute("novoPedido") Pedido pedido, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.criarPedido(pedido.getCliente(), pedido.getValorTotal());
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Pedido aberto com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/pedidos";
    }

    /**
     * Rota de fechamento do pedido:
     * Dispara a chamada REST síncrona ao Hub Hexagonal e guarda o ID retornado.
     */
    @PostMapping("/{id}/fechar")
    public String fecharPedido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UUID faturaId = pedidoService.fecharPedido(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Pedido #" + id + " fechado com sucesso! Faturado no Hub com ID: " + faturaId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", 
                    "Erro ao faturar pedido #" + id + ": " + e.getMessage());
        }
        return "redirect:/pedidos";
    }
}
