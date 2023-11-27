package com.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorygenius.repository.IProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {
	@Autowired
    private IProductoRepository repo;

    @Transactional
    public void eliminarProductoPorCodUnicoProd(String codUnicoProd) {
    	repo.deleteByCodUnicoProd(codUnicoProd);
    }
}
