package com.inventorygenius.controller;


import com.inventorygenius.model.Usuario;
import com.inventorygenius.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {
    @Autowired
    private IUsuarioRepository repoUsuario;
    
    @Autowired com.inventorygenius.service.UsuarioService usuarioService;
	@Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

	
	
	/*
    @PostMapping("/home/Acount")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("passwordUser") String password,
                        Model model) {
        Usuario usuario = repoUsuario.findByUsuarioAndPassword(userName, password);
        if (usuario != null) {
            return "Home_Acount";
        } else {
            model.addAttribute("mensaje", "Usuario y/o contraseña incorrectos");
            model.addAttribute("clase", "alert alert-danger");
            return "loginSign";
        }
    }




    @PostMapping("/home/NewAcount/Create")
    public String registerNewUsuario(@RequestParam("userName") String userName,
                                     @RequestParam("passwordUser") String password,
                                     Model model) {
        // Verifica si ya existe un usuario con el mismo nombre de usuario
        if (repoUsuario.findByUsuario(userName) != null) {
            model.addAttribute("mensaje", "Ya existe un usuario con el mismo nombre de usuario");
            model.addAttribute("clase", "alert alert-danger");
            return "loginCreate";
        }

        // Crea un nuevo objeto Usuario y guarda los datos en la base de datos
        Usuario usuario = new Usuario();
        usuario.setUsuario(userName);
        usuario.setPassword(password);
        repoUsuario.save(usuario);
        model.addAttribute("mensaje", "Operación Exitosa");
        model.addAttribute("clase", "alert alert-success");
        return "loginCreate";
    }
    */
    
    
	@PostMapping("/validarLogin")
	public String validarLogin(@RequestParam String usuario, @RequestParam String contrasena, Model model, RedirectAttributes redirect) {
	    Usuario usuarioFromDB = usuarioService.findByUsuario(usuario);
	    System.out.println(usuario);
	    System.out.println(contrasena);

	    if (usuarioFromDB != null && bCryptPasswordEncoder.matches(contrasena, usuarioFromDB.getPassword())) {
	    	model.addAttribute("mensaje", "Login exitoso.");
	        return "redirect:/home/HomeAcount";
	    } else {
	    	model.addAttribute("clase", "alert-danger");
	        model.addAttribute("mensaje", "Credenciales inválidas. Por favor, intenta de nuevo.");
	        return "redirect:/home/Acount";
	    }
	}


	
	
	@PostMapping("/home/NewAcount/Create")
	public String registerNewUsuario(@RequestParam("userName") String userName,
	                                 @RequestParam("passwordUser") String password,
	                                 Model model) {
	    // Verifica si ya existe un usuario con el mismo nombre de usuario
	    if (repoUsuario.findByUsuario(userName) != null) {
	        model.addAttribute("mensaje", "Ya existe un usuario con el mismo nombre de usuario");
	        model.addAttribute("clase", "alert alert-danger");
	        return "loginCreate";
	    }

	    // Crea un nuevo objeto Usuario y guarda los datos en la base de datos
	    Usuario usuario = new Usuario();
	    usuario.setUsuario(userName);
	    usuario.setPassword(bCryptPasswordEncoder.encode(password)); 
	    repoUsuario.save(usuario);
	    model.addAttribute("mensaje", "Operación Exitosa");
	    model.addAttribute("clase", "alert alert-success");
	    return "loginCreate";
	}
	
	
	/*
	 * User: adin
	 * Pass: admin
		$2a$10$DkK/1Syvyk7XHqdb12iLg.jrlcd8fudBn.opcydRRgrU/ogJB3y9S

		User: midd
		Pass: 123
		$2a$10$D/hWqT.Ivh14e7WF6bBa0eJQIjk0kQ/3wxAx/7Mk6jomm5nbWTLzO

		User: user
		Pass: 000
		$2a$10$iApMwRLbt8DFVZrTYKS13ucUsQ9I4I09dC9MIPQCnV3F3qfB.ER4K
		*/

}
