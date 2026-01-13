package com.soyucab.back.controller;

import com.soyucab.back.dto.SeRelacionaDTO;
import com.soyucab.back.mapper.SocialMapper;
import com.soyucab.back.model.SeRelaciona;
import com.soyucab.back.service.SeRelacionaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
public class SeRelacionaController {

    @Autowired
    private SeRelacionaService seRelacionaService;

    @Autowired
    private SocialMapper socialMapper;

    @PostMapping("/relacion")
    public SeRelacionaDTO createRelacion(@RequestBody SeRelacionaDTO dto) {
        SeRelaciona entity = socialMapper.toEntity(dto);
        return socialMapper.toDTO(seRelacionaService.save(entity));
    }

    @GetMapping("/{userId}/followers")
    public List<SeRelacionaDTO> getFollowers(@PathVariable String userId) {
        return seRelacionaService.getFollowers(userId).stream()
                .map(socialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/following")
    public List<SeRelacionaDTO> getFollowing(@PathVariable String userId) {
        return seRelacionaService.getFollowing(userId).stream()
                .map(socialMapper::toDTO)
                .collect(Collectors.toList());
    }
}
