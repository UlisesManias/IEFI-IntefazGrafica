package batalla.controlador;

import batalla.vista.PantallaHistorial;
import batalla.vista.formHistorial;
 

/**
 * Controlador para la pantalla de historial
 */
public class ControladorHistorial {
    private PantallaHistorial vista;

    public ControladorHistorial(PantallaHistorial vista) {
        this.vista = vista;
        configurarEventos();
        cargarPartidas();
    }

    private void configurarEventos() {
        vista.getBtnCargarPartida().addActionListener(e -> cargarPartida());
        vista.getBtnVolver().addActionListener(e -> volver());
        // Registrar evento para borrar una partida seleccionada
        try {
            vista.getBtnBorrarPartida().addActionListener(e -> borrarPartida());
        } catch (Exception ex) {
            // Si la vista no expone el botón (compatibilidad), ignorar
        }
    }

    private void cargarPartidas() {
        // Listar los archivos individuales dentro de la carpeta HistorialPartidas/
        java.io.File dir = new java.io.File("HistorialPartidas");
        java.util.List<java.io.File> archivos = new java.util.ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                String name = f.getName();
                if (name.startsWith("Batalla") && name.toLowerCase().endsWith(".txt") && name.replaceAll("\\D+", "").length() > 0) {
                    archivos.add(f);
                }
            }
        }

        // Ordenar por número de batalla ascendente (extraer dígitos)
        archivos.sort((a, b) -> {
            int na = Integer.parseInt(a.getName().replaceAll("\\D+", "0"));
            int nb = Integer.parseInt(b.getName().replaceAll("\\D+", "0"));
            return Integer.compare(na, nb);
        });

        Object[][] datos = new Object[archivos.size()][5];
        String[] columnas = {"N° Batalla", "Héroe", "Villano", "Ganador", "Turnos"};

        for (int i = 0; i < archivos.size(); i++) {
            java.io.File f = archivos.get(i);
            String digits = f.getName().replaceAll("\\D+", "0");
            try {
                datos[i][0] = Integer.parseInt(digits);
            } catch (NumberFormatException e) {
                datos[i][0] = i + 1;
            }
            // Leer primeras líneas para obtener metadatos
            try (java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(f))) {
                String linea;
                String heroe = "N/A", villano = "N/A", ganador = "N/A", turnos = "0";
                while ((linea = r.readLine()) != null) {
                    if (linea.startsWith("HEROE: ")) heroe = linea.substring(7).trim();
                    else if (linea.startsWith("VILLANO: ")) villano = linea.substring(9).trim();
                    else if (linea.startsWith("GANADOR: ")) ganador = linea.substring(9).trim();
                    else if (linea.startsWith("TURNOS: ")) turnos = linea.substring(8).trim();
                    else if (linea.equals("COMBATLOG_START")) break;
                }
                datos[i][1] = heroe;
                datos[i][2] = villano;
                datos[i][3] = ganador;
                datos[i][4] = turnos;
            } catch (Exception ex) {
                datos[i][1] = "N/A";
                datos[i][2] = "N/A";
                datos[i][3] = "N/A";
                datos[i][4] = "0";
            }
        }

        vista.actualizarTabla(datos, columnas);
    }

    private void cargarPartida() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada < 0) {
            javax.swing.JOptionPane.showMessageDialog(vista, 
                "Debe seleccionar una partida", 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Buscar el archivo BatallaN.txt correspondiente a la fila seleccionada
        java.io.File dir = new java.io.File("HistorialPartidas");
        java.util.List<java.io.File> archivos = new java.util.ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                String name = f.getName();
                if (name.startsWith("Batalla") && name.toLowerCase().endsWith(".txt") && name.replaceAll("\\D+", "").length() > 0) {
                    archivos.add(f);
                }
            }
        }
        archivos.sort((a, b) -> Integer.compare(
                Integer.parseInt(a.getName().replaceAll("\\D+", "0")),
                Integer.parseInt(b.getName().replaceAll("\\D+", "0"))));

        if (filaSeleccionada >= archivos.size()) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Partida no encontrada",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.io.File partidaFile = archivos.get(filaSeleccionada);
        formHistorial form = new formHistorial();
        ControladorFormHistorial controlador = new ControladorFormHistorial(form, partidaFile);
        controlador.iniciar();
    }

    private void borrarPartida() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada < 0) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Debe seleccionar una partida para borrar",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = javax.swing.JOptionPane.showConfirmDialog(vista,
                "¿Está seguro que desea borrar la partida seleccionada?", "Confirmar borrado",
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;

        // Borrar el archivo correspondiente en HistorialPartidas/
        java.io.File dir = new java.io.File("HistorialPartidas");
        java.util.List<java.io.File> archivos = new java.util.ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                String name = f.getName();
                if (name.startsWith("Batalla") && name.toLowerCase().endsWith(".txt") && name.replaceAll("\\D+", "").length() > 0) {
                    archivos.add(f);
                }
            }
        }
        archivos.sort((a, b) -> Integer.compare(
                Integer.parseInt(a.getName().replaceAll("\\D+", "0")),
                Integer.parseInt(b.getName().replaceAll("\\D+", "0"))));

        if (filaSeleccionada >= archivos.size()) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "No se encontró la partida seleccionada.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.io.File aBorrar = archivos.get(filaSeleccionada);
        boolean ok = aBorrar.delete();
        if (!ok) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "No se pudo borrar el archivo: " + aBorrar.getName(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Refrescar tabla
        cargarPartidas();

        javax.swing.JOptionPane.showMessageDialog(vista,
                "Partida borrada correctamente.",
                "Éxito",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void volver() {
        vista.dispose();
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}

