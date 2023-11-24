package com.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorygenius.model.Usuario;
import com.inventorygenius.repository.IUsuarioRepository;
@Service
public class UsuarioService {
	@Autowired
    private IUsuarioRepository repo;
	
	public Usuario findByUsuario(String usuario) {
        return repo.findByUsuario(usuario);
    }
}
