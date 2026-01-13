package com.soyucab.back.service;

import com.soyucab.back.model.SeRelaciona;
import com.soyucab.back.model.SeRelacionaId;
import com.soyucab.back.repository.SeRelacionaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeRelacionaService {

    @Autowired
    private SeRelacionaRepository seRelacionaRepository;

    public SeRelaciona save(SeRelaciona relacion) {
        return seRelacionaRepository.save(relacion);
    }

    public List<SeRelaciona> getFollowers(String userId) {
        // Assuming "Seguimiento" and userId is the Receptor
        return seRelacionaRepository.findByReceptor_Cuenta(userId);
    }

    public List<SeRelaciona> getFollowing(String userId) {
        // Assuming "Seguimiento" and userId is the Solicitante
        return seRelacionaRepository.findBySolicitante_Cuenta(userId);
    }
}
