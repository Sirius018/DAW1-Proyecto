package com.inventorygenius.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IProductoRepository extends CrudRepository<com.inventorygenius.model.Producto, String>{
	 @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE p.cod_unico_prod = :codUnicoProd")
	    boolean existsByCodUnicoProd(@Param("codUnicoProd") String codUnicoProd);


	 @Modifying
	 @Query("DELETE FROM Producto p WHERE p.cod_unico_prod = :codUnicoProd")
	 void deleteByCodUnicoProd(@Param("codUnicoProd") String codUnicoProd);
}
