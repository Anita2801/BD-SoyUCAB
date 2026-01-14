package com.soyucab.back.repository;

import com.soyucab.back.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Integer> {

    @Modifying
    @Query("DELETE FROM Denuncia d WHERE d.denunciante.cuenta = :cuenta")
    void deleteByDenuncianteCuenta(@Param("cuenta") String cuenta);

    @Modifying
    @Query("DELETE FROM Denuncia d WHERE d.denunciado.cuenta = :cuenta")
    void deleteByDenunciadoCuenta(@Param("cuenta") String cuenta);
}
