package co.com.svl.controlador;

import co.com.svl.modelo.*;
import co.com.svl.servicio.*;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author JHOJAN L
 */

@Slf4j
@Controller
public class ControladorInventario {

    @Autowired
    private ProductoService productoService;

    /**
     *
     * @param model
     * @return inventario
     */
    @GetMapping("/inventario")
    public String inventario(Model model) {

        var productoEditar = new Producto();
        model.addAttribute("productoEditar", productoEditar);
        return "inventario";
    }

    /**
     *
     * @param productoEditado
     * @return redirect:/inventario
     */
    @PostMapping("/editarProducto")
    public String editarProducto(Producto productoEditado) {

        //Se debe validar que el costo no sea mayor al precio porque podria dar algun error
        log.info("producto a editar desde el inventario" + productoEditado.toString());

        short opcion = validarProducto(productoEditado);

        switch (opcion) {
            case 0:
                actualizarDatos(productoEditado);
                return "redirect:/inventario?exito=true";
            case 1:
                return "redirect:/inventario?error1=true";
            case 2:
                return "redirect:/inventario?error2=true";
            case 3:
                return "redirect:/inventario?error3=true";
            case 4:
                return "redirect:/inventario?error4=true";

            default:
                throw new AssertionError();
        }

    }

    private short validarProducto(Producto productoEditado) {

        if (productoEditado.getCosto() > 0 && productoEditado.getPrecio() > 0) {

            if (productoEditado.getPrecio() > productoEditado.getCosto()) {

                if (productoEditado.getCantidad() >= 0.0) {

                    if ((productoEditado.getCantidad() == (long) productoEditado.getCantidad()
                            && productoEditado.getUnidadMedida() == 1)
                            || (productoEditado.getUnidadMedida() == 2)) {

                        return 0;

                    } else {
                        return 4;
                    }

                } else {

                    return 3;
                }

            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    private void actualizarDatos(Producto productoEditado) {
        var admin = new Administrador();
        //establecemos el administrador en este caso es el cod 1 ya que la 
        //aplicacion solo cuenta con este administrador
        
        //formateamos la cantidad para que sean dos decimales maximo
        var df = new DecimalFormat("#.00");
        productoEditado.setCantidad(Double.parseDouble(df.format(productoEditado.getCantidad()).replace(",", ".")));
        
        admin.setCodigo((short) 1);
        productoEditado.setCodigoAdministrador(admin);
        productoService.guardar(productoEditado);
    }
}
