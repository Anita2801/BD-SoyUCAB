package com.soyucab.back.repository;

import com.soyucab.back.model.Reacciona;
import com.soyucab.back.model.ReaccionaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReaccionaRepository extends JpaRepository<Reacciona, ReaccionaId> {
    List<Reacciona> findByContenido_Usuario_CuentaAndUsuario_CuentaNot(String cuentaOwner, String cuentaReactor);

    @Modifying
    @Query("DELETE FROM Reacciona r WHERE r.usuario.cuenta = :cuenta")
    void deleteByUsuarioCuenta(@Param("cuenta") String cuenta);
}
