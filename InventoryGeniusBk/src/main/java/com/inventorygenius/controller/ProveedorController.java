package com.inventorygenius.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventorygenius.model.Proveedor;
import com.inventorygenius.repository.IPaisRepository;
import com.inventorygenius.repository.IProveedorRepository;
import com.inventorygenius.repository.ITipoRepository;
import com.inventorygenius.service.ProveedorService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
public class ProveedorController {

	@Autowired private IProveedorRepository repoProveedor;
	@Autowired private ITipoRepository repoTipo; 
	@Autowired private IPaisRepository repoPais; 
	@Autowired private DataSource dataSource;	
	@Autowired private ResourceLoader resourceLoader;
	@Autowired private ProveedorService proveedorService; 
	
	
	@GetMapping("/proveedor/listadopdf")
	public void reporteProveedor(HttpServletResponse response) {
		// descargar directamente en un archivo
		// response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
		
		// el pdf se muestre en pantalla
		response.setHeader("Content-Disposition", "inline;"); 
		// tipo de contenido
		response.setContentType("application/pdf");
		try {
			// obtener el recurso a utilizar -> jasper
			String ru = resourceLoader.getResource("classpath:reportes/ReporteProveedor.jasper").getURI().getPath();
			// combina el jasper + data / Ojo!!! null -> la conexión no tiene parámetros
			JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
			// genera un archivo temporal
			OutputStream outStream = response.getOutputStream();
			// muestra el archivo
			JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	

	@GetMapping("/home/HomeAcount/Proveedores")
	public String listarProveedor(Model model) {

		model.addAttribute("listaProveedor", repoProveedor.findAll());
		model.addAttribute("lstTipo", repoTipo.findAll());
		model.addAttribute("lstPais", repoPais.findAll());

		model.addAttribute("proveedor", new Proveedor());
		model.addAttribute("boton","Registrar");

		return "Proveedor";
	}
	

	@PostMapping("/proveedor/guardar")
	public String guardarProveedor(@ModelAttribute Proveedor proveedor, @RequestParam("boton") String boton, Model model, RedirectAttributes redirect) {
	    model.addAttribute("boton", boton);

	    try {
	        if (boton.equals("Registrar")) {
	            // botón Registrar
	            if (repoProveedor.findByRucProv(proveedor.getCod_unico_prov()) != null) {
	                model.addAttribute("mensaje", "El código único ya existe");
	                model.addAttribute("clase", "alert alert-danger");
	            } else if (repoProveedor.findByRucProv(proveedor.getRuc_prov()) != null) {
	                model.addAttribute("mensaje", "El RUC ya existe");
	                model.addAttribute("clase", "alert alert-danger");
	            } else {
	                repoProveedor.save(proveedor);
	                model.addAttribute("mensaje", "Operación Exitosa");
	                model.addAttribute("clase", "alert alert-success");
	                return "redirect:/home/HomeAcount/Proveedores";
	            }
	        } else if (boton.equals("Actualizar")) {
	            // botón Actualizar
	            repoProveedor.save(proveedor);
	            model.addAttribute("mensaje", "Operación Exitosa");
	            model.addAttribute("clase", "alert alert-success");

	            // Redireccionamiento a la URL deseada
	            redirect.addFlashAttribute("mensaje", "Operación Exitosa");
	            redirect.addFlashAttribute("clase", "alert alert-success");
	            return "redirect:/home/HomeAcount/Proveedores";
	        }

	        model.addAttribute("listaProveedor", repoProveedor.findAll());
	        model.addAttribute("lstTipo", repoTipo.findAll());
	        model.addAttribute("lstPais", repoPais.findAll());
	    } catch (Exception e) {
	        model.addAttribute("listaProveedor", repoProveedor.findAll());
	        model.addAttribute("lstTipo", repoTipo.findAll());
	        model.addAttribute("lstPais", repoPais.findAll());
	        model.addAttribute("mensaje", "No se pudo registrar");
	        model.addAttribute("clase", "alert alert-danger");
	    }
	    return "Proveedor"; 
	}



	@PostMapping("/buscarproveedor")
	public String buscarProveedor(@RequestParam (name= "cod_prov") int cod_prov, Model model) {
		System.out.println(cod_prov);
		model.addAttribute("proveedor", repoProveedor.findById(cod_prov));

		model.addAttribute("listaProveedor", repoProveedor.findAll());
		model.addAttribute("lstTipo", repoTipo.findAll());
		model.addAttribute("lstPais", repoPais.findAll());
		model.addAttribute("boton","Actualizar");
		//return "redirect:/Proveedor";
		return "Proveedor";
	}

	
	
	
	@PostMapping("/eliminar/proveedor")
	public String eliminarProveedor(@RequestParam("cod_prov") int codProv, String boton,RedirectAttributes redirect, Model model) {
		 model.addAttribute("boton", boton);
	    try {
	        proveedorService.eliminarProveedorPorCod(codProv);
	        System.out.println("Se eliminó el proveedor con cod_prov: " + codProv);
	        redirect.addFlashAttribute("mensaje", "Proveedor eliminado correctamente " +codProv);
	        model.addAttribute("boton", boton);
	    } catch (DataIntegrityViolationException e) {
	        System.out.println(e);
	        System.out.println("No se puede eliminar el proveedor debido a restricciones de integridad con "+codProv);
	        redirect.addFlashAttribute("mensaje", "No se puede eliminar el proveedor debido a restricciones de integridad "+codProv);
	        model.addAttribute("boton", boton);
	    } catch (Exception ex) {
	        System.out.println(ex);
	        redirect.addFlashAttribute("MENSAJE", "Error al intentar eliminar el proveedor "+codProv);	        
	        model.addAttribute("clase", "alert alert-danger");
	        model.addAttribute("boton", boton);
	        System.out.println("Error al intentar eliminar el proveedor "+codProv);
	    }
	    return "redirect:/home/HomeAcount/Proveedores";
	}


	
	
}