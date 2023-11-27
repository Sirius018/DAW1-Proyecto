package com.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorygenius.repository.IProveedorRepository;

import jakarta.transaction.Transactional;

@Service
public class ProveedorService {
	@Autowired
    private IProveedorRepository proveedorRepository;

	
    @Transactional
    public void eliminarProveedorPorCod(int cod_prov) {
        proveedorRepository.deleteByCodProv(cod_prov);
    }

}
