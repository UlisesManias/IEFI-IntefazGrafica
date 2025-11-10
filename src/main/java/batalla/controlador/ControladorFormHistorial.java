package batalla.controlador;

import batalla.modelo.GestorPersistencia;
import batalla.vista.formHistorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Controlador para el formulario de historial
 */
public class ControladorFormHistorial {
    private formHistorial vista;
    private File partidaFile;

    public ControladorFormHistorial(formHistorial vista, File partidaFile) {
        this.vista = vista;
        this.partidaFile = partidaFile;
        configurarEventos();
        cargarDatos();
    }

    private void configurarEventos() {
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    private void cargarDatos() {
        // Leer directamente desde el archivo individual de la partida
        StringBuilder combatLog = new StringBuilder();
        int mayorDanio = 0;
        String personajeMayorDanio = "N/A";
        int batallaMasLarga = 0;
        String ganadorBatallaMasLarga = "N/A";
        int totalArmasHeroe = 0;
        int totalArmasVillano = 0;
        int totalSupremosHeroe = 0;
        int totalSupremosVillano = 0;
        int victoriasHeroe = 0;
        int victoriasVillano = 0;
        int totalBatallas = 0;

        boolean enCombatLog = false;
    String nombreHeroe = "N/A";
    String nombreVillano = "N/A";

        if (partidaFile != null && partidaFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(partidaFile))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.startsWith("HEROE: ")) {
                        nombreHeroe = linea.substring(7).trim();
                    } else if (linea.startsWith("VILLANO: ")) {
                        nombreVillano = linea.substring(9).trim();
                    } else if (linea.startsWith("GANADOR: ")) {
                        ganadorBatallaMasLarga = linea.substring(9).trim();
                    } else if (linea.startsWith("TURNOS: ")) {
                        try { batallaMasLarga = Integer.parseInt(linea.substring(8).trim()); } catch (NumberFormatException ignored) {}
                    } else if (linea.equals("COMBATLOG_START")) {
                        enCombatLog = true; continue;
                    } else if (linea.equals("COMBATLOG_END")) {
                        enCombatLog = false; break;
                    }

                    if (enCombatLog) combatLog.append(linea).append("\n");
                }
            } catch (Exception e) {
                combatLog.append("[Error al leer archivo de partida: " + e.getMessage() + "]\n");
            }
        }

        if (combatLog.length() == 0) {
            combatLog.append("=== COMBAT LOG ===\n");
            combatLog.append("[Combat log no disponible para esta partida]\n");
        }

        vista.setCombatLog(combatLog.toString());

    // Actualizar encabezados con héroe/villano/ganador/turnos
    vista.setHeroeNombre(nombreHeroe);
    vista.setVillanoNombre(nombreVillano);
    vista.setGanador(ganadorBatallaMasLarga);
    vista.setTurnos(String.valueOf(batallaMasLarga));

        // Calcular winrates desde personajes (compatibilidad)
        List<batalla.modelo.Personaje> personajes = GestorPersistencia.cargarPersonajes();
        for (batalla.modelo.Personaje p : personajes) {
            if (p instanceof batalla.modelo.Heroe) victoriasHeroe += p.getVictorias();
            else victoriasVillano += p.getVictorias();
            totalBatallas += p.getVictorias();
        }

        // Actualizar labels: algunos ya se calcularon
        vista.setMayorDanio(mayorDanio > 0 ? (mayorDanio + " (" + personajeMayorDanio + ")") : "N/A");
        vista.setBatallaMasLarga(batallaMasLarga > 0 ? (batallaMasLarga + " turnos (Ganador: " + ganadorBatallaMasLarga + ")") : "N/A");
        vista.setArmasHeroe(String.valueOf(totalArmasHeroe));
        vista.setArmasVillano(String.valueOf(totalArmasVillano));
        vista.setSupremosHeroe(String.valueOf(totalSupremosHeroe));
        vista.setSupremosVillano(String.valueOf(totalSupremosVillano));

        if (totalBatallas > 0) {
            double winrateHeroe = (victoriasHeroe * 100.0) / totalBatallas;
            double winrateVillano = (victoriasVillano * 100.0) / totalBatallas;
            vista.setWinrateHeroe(String.format("%.2f", winrateHeroe) + "%");
            vista.setWinrateVillano(String.format("%.2f", winrateVillano) + "%");
        } else {
            vista.setWinrateHeroe("0.00%");
            vista.setWinrateVillano("0.00%");
        }
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}

