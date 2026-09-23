package br.com.academia.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.academia.model.entity.Aluno;
import br.com.academia.model.repository.AlunoRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Aluno create(Aluno aluno){
        return alunoRepository.save(aluno);
    }

    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }

}
