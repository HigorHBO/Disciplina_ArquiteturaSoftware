package br.com.academia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.academia.model.entity.Aluno;
import br.com.academia.model.service.AlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public String read(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("aluno", new Aluno());
        return "alunos";
    }

    @PostMapping
    public String create(@ModelAttribute Aluno aluno) {
        alunoService.create(aluno);
        return "redirect:/alunos";
    }

}
