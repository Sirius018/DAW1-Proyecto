package com.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventorygenius.model.Producto;
import com.inventorygenius.model.Proveedor;
import com.inventorygenius.repository.ICategoriaRepository;
import com.inventorygenius.repository.IProductoRepository;
import com.inventorygenius.repository.IProveedorRepository;
import com.inventorygenius.service.ProductoService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

//@RestController
@Controller
public class ProductoController {
	/*crear los objetos para el repositorio*/

	@Autowired private IProductoRepository repoProducto;
	@Autowired 	private IProveedorRepository repoProveedor;
	@Autowired 	private ICategoriaRepository repoCategoria; 
	@Autowired 	private DataSource dataSource;
	@Autowired 	private ResourceLoader resourceLoader;
	@Autowired private ProductoService produServ;

	@GetMapping("/producto/listadopdf")
	public void reporteProducto(HttpServletResponse response) {
		// descargar directamente en un archivo
		// response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");

		// el pdf se muestre en pantalla
		response.setHeader("Content-Disposition", "inline;");
		// tipo de contenido
		response.setContentType("application/pdf");
		try {
			// obtener el recurso a utilizar -> jasper
			String ru = resourceLoader.getResource("classpath:reportes/ReporteProductos.jasper").getURI().getPath();
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



	@PostMapping("/producto/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, @RequestParam("boton") String boton, Model model, RedirectAttributes redirect) {
        model.addAttribute("boton", boton);

        try {
            if (boton.equals("Registrar")) {
                // botón Registrar
                if (repoProducto.existsByCodUnicoProd(producto.getCod_unico_prod())) {
                    model.addAttribute("mensaje", "El código único ya existe");
                    model.addAttribute("clase", "alert alert-danger");
                } else {
                    repoProducto.save(producto);
                    model.addAttribute("mensaje", "Operación Exitosa");
                    model.addAttribute("clase", "alert alert-success");
                }
            } else if (boton.equals("Actualizar")) {
                // botón Actualizar
                if (repoProducto.existsByCodUnicoProd(producto.getCod_unico_prod())) {
                    repoProducto.save(producto);
                    model.addAttribute("mensaje", "Producto actualizado");
                    model.addAttribute("clase", "alert alert-success");

                    // Redireccionamiento a la URL deseada
                    redirect.addFlashAttribute("mensaje", "Producto actualizado");
                    redirect.addFlashAttribute("clase", "alert alert-success");
                    return "redirect:/home/HomeAcount/Producto";
                } else {
                    model.addAttribute("mensaje", "El código único no existe, no se puede actualizar");
                    model.addAttribute("clase", "alert alert-danger");
                }
            }

            model.addAttribute("listaProducto", repoProducto.findAll());
            model.addAttribute("lstCategoria", repoCategoria.findAll());
            model.addAttribute("lstProveedor", repoProveedor.findAll());
        } catch (Exception e) {
            model.addAttribute("listaProducto", repoProducto.findAll());
            model.addAttribute("lstCategoria", repoCategoria.findAll());
            model.addAttribute("lstProveedor", repoProveedor.findAll());
            model.addAttribute("mensaje", "No se pudo registrar o actualizar");
            model.addAttribute("clase", "alert alert-danger");
        }

        //return "Productos";
        return "redirect:/home/HomeAcount/Producto";

    }


	@PostMapping("/buscarproducto")
	public String buscarProveedor(@RequestParam (name= "cod_unico_prod") String cod_unico_prod, Model model) {
		/*System.out.println(cod_unico_prod);*/
		model.addAttribute("producto", repoProducto.findById(cod_unico_prod));
		model.addAttribute("listaProducto", repoProducto.findAll());
		model.addAttribute("lstCategoria", repoCategoria.findAll());
		model.addAttribute("lstProveedor", repoProveedor.findAll());
		model.addAttribute("boton","Actualizar");
		return "Productos";
	}
	
	

	@GetMapping("/home/HomeAcount/Producto")
	public String listarProductos(Model model) {
		model.addAttribute("listaProducto", repoProducto.findAll());
		model.addAttribute("lstCategoria", repoCategoria.findAll());
		model.addAttribute("lstProveedor", repoProveedor.findAll());
		model.addAttribute("producto", new Producto());
		model.addAttribute("boton","Registrar");
		return "Productos";
	}
	
	
	
	
	
	@PostMapping("/eliminar/producto")
	public String eliminarProducto(@RequestParam("cod_unico_prod") String codUnicoProd, String boton,RedirectAttributes redirect, Model model) {
		model.addAttribute("boton", boton);
	    try {
	        produServ.eliminarProductoPorCodUnicoProd(codUnicoProd);
	        System.out.println("Se eliminó el producto con código único: " + codUnicoProd);
	        redirect.addFlashAttribute("mensaje", "Producto eliminado exitosamente "+codUnicoProd);
	        model.addAttribute("clase", "alert alert-success");
	    } catch (DataIntegrityViolationException e) {
	        System.out.println(e);
	        System.out.println("No se puede eliminar el producto debido a restricciones de integridad "+codUnicoProd);
	        redirect.addFlashAttribute("mensaje", "No se puede eliminar el producto debido a restricciones de integridad "+codUnicoProd);
	        model.addAttribute("clase", "alert alert-danger");
	    } catch (Exception ex) {
	        System.out.println(ex);
	        System.out.println("Error al intentar eliminar el producto "+codUnicoProd);
	        //redirect.addFlashAttribute("MENSAJE", "Error al intentar eliminar el producto "+codUnicoProd);
	    }
	    return "redirect:/home/HomeAcount/Producto";
	}

	
	
	
}
