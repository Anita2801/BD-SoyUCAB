package com.soyucab.back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NexoId implements Serializable {
    @Column(name = "Cod_Inst_Nexo")
    private String codInstNexo;

    @Column(name = "CI_Nexo")
    private String ciNexo;
}
