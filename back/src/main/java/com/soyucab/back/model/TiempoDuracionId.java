package com.soyucab.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiempoDuracionId implements Serializable {
    @Column(name = "Inst_T", length = 10)
    private String instT;

    @Column(name = "CI_T", length = 15)
    private String ciT;

    @Column(name = "Rol_T", length = 20)
    private String rolT;
}
