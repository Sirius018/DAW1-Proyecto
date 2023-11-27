package com.inventorygenius.repository;

import com.inventorygenius.model.Proveedor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IProveedorRepository extends CrudRepository<Proveedor, Integer> {
	@Query("SELECT p FROM Proveedor p WHERE p.ruc_prov = :rucProv")
    Proveedor findByRucProv(@Param("rucProv") String rucProv);
	
	 @Modifying
	 @Query("DELETE FROM Proveedor p WHERE p.cod_prov = :codProv")
	 void deleteByCodProv(@Param("codProv") int codProv);

}
