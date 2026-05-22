package com.quiz.app.controller;

import com.quiz.app.model.*;
import com.quiz.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QuizController {
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private AsociacionRepository asociacionRepository;
    
    @Autowired
    private CompeticionRepository competicionRepository;
    
    // ==================== PÁGINA PRINCIPAL ====================
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClubes", clubRepository.count());
        model.addAttribute("totalEntrenadores", entrenadorRepository.count());
        model.addAttribute("totalJugadores", jugadorRepository.count());
        model.addAttribute("totalAsociaciones", asociacionRepository.count());
        model.addAttribute("totalCompeticiones", competicionRepository.count());
        return "index";
    }
    
    // ==================== CRUD CLUBES ====================
    @GetMapping("/clubes")
    public String listClubes(Model model) {
        model.addAttribute("clubes", clubRepository.findAll());
        return "clubes";
    }
    
    @GetMapping("/club-detalle/{id}")
    public String clubDetalle(@PathVariable Long id, Model model) {
        Club club = clubRepository.findById(id).orElse(null);
        model.addAttribute("club", club);
        return "club-detalle";
    }
    
    @GetMapping("/club-form")
    public String clubForm(Model model) {
        model.addAttribute("club", new Club());
        model.addAttribute("entrenadores", entrenadorRepository.findAll());
        model.addAttribute("asociaciones", asociacionRepository.findAll());
        model.addAttribute("competiciones", competicionRepository.findAll());
        model.addAttribute("jugadores", jugadorRepository.findAll());
        return "club-form";
    }
    
    @GetMapping("/club-form/{id}")
    public String clubEditForm(@PathVariable Long id, Model model) {
        Club club = clubRepository.findById(id).orElse(null);
        model.addAttribute("club", club);
        model.addAttribute("entrenadores", entrenadorRepository.findAll());
        model.addAttribute("asociaciones", asociacionRepository.findAll());
        model.addAttribute("competiciones", competicionRepository.findAll());
        model.addAttribute("jugadores", jugadorRepository.findAll());
        return "club-form";
    }
    
    @PostMapping("/club/save")
    public String saveClub(@ModelAttribute Club club, 
                           @RequestParam(value = "entrenadorId", required = false) Long entrenadorId,
                           @RequestParam(value = "asociacionId", required = false) Long asociacionId,
                           @RequestParam(value = "competicionesIds", required = false) List<Long> competicionesIds,
                           @RequestParam(value = "jugadoresIds", required = false) List<Long> jugadoresIds,
                           RedirectAttributes redirectAttributes) {
        try {
            if (entrenadorId != null && entrenadorId > 0) {
                club.setEntrenador(entrenadorRepository.findById(entrenadorId).orElse(null));
            }
            if (asociacionId != null && asociacionId > 0) {
                club.setAsociacion(asociacionRepository.findById(asociacionId).orElse(null));
            }
            if (competicionesIds != null && !competicionesIds.isEmpty()) {
                club.setCompeticiones(competicionRepository.findAllById(competicionesIds));
            }
            if (jugadoresIds != null && !jugadoresIds.isEmpty()) {
                club.setJugadores(jugadorRepository.findAllById(jugadoresIds));
            }
            clubRepository.save(club);
            redirectAttributes.addFlashAttribute("success", "Club guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el club");
        }
        return "redirect:/clubes";
    }
    
    @GetMapping("/club/delete/{id}")
    public String deleteClub(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clubRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Club eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el club");
        }
        return "redirect:/clubes";
    }
    
    // ==================== CRUD ENTRENADORES ====================
    @GetMapping("/entrenadores")
    public String listEntrenadores(Model model) {
        model.addAttribute("entrenadores", entrenadorRepository.findAll());
        model.addAttribute("clubes", clubRepository.findAll());
        return "entrenadores";
    }
    
    @GetMapping("/entrenador-detalle/{id}")
    public String entrenadorDetalle(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorRepository.findById(id).orElse(null);
        model.addAttribute("entrenador", entrenador);
        
        Club club = clubRepository.findAll().stream()
            .filter(c -> c.getEntrenador() != null && c.getEntrenador().getId().equals(id))
            .findFirst().orElse(null);
        model.addAttribute("club", club);
        
        return "entrenador-detalle";
    }
    
    @GetMapping("/entrenador-form")
    public String entrenadorForm(Model model) {
        model.addAttribute("entrenador", new Entrenador());
        model.addAttribute("clubes", clubRepository.findAll());
        return "entrenador-form";
    }
    
    @GetMapping("/entrenador-form/{id}")
    public String entrenadorEditForm(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorRepository.findById(id).orElse(null);
        model.addAttribute("entrenador", entrenador);
        model.addAttribute("clubes", clubRepository.findAll());
        return "entrenador-form";
    }
    
    @PostMapping("/entrenador/save")
    public String saveEntrenador(@ModelAttribute Entrenador entrenador,
                                  @RequestParam(value = "clubId", required = false) Long clubId,
                                  RedirectAttributes redirectAttributes) {
        try {
            entrenadorRepository.save(entrenador);
            if (clubId != null && clubId > 0) {
                Club club = clubRepository.findById(clubId).orElse(null);
                if (club != null) {
                    club.setEntrenador(entrenador);
                    clubRepository.save(club);
                }
            }
            redirectAttributes.addFlashAttribute("success", "Entrenador guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el entrenador");
        }
        return "redirect:/entrenadores";
    }
    
    @GetMapping("/entrenador/delete/{id}")
    public String deleteEntrenador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<Club> clubes = clubRepository.findAll();
            for (Club club : clubes) {
                if (club.getEntrenador() != null && club.getEntrenador().getId().equals(id)) {
                    club.setEntrenador(null);
                    clubRepository.save(club);
                }
            }
            entrenadorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Entrenador eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el entrenador");
        }
        return "redirect:/entrenadores";
    }
    
    // ==================== CRUD JUGADORES ====================
    @GetMapping("/jugadores")
    public String listJugadores(Model model) {
        model.addAttribute("jugadores", jugadorRepository.findAll());
        model.addAttribute("clubes", clubRepository.findAll());
        return "jugadores";
    }
    
    @GetMapping("/jugador-detalle/{id}")
    public String jugadorDetalle(@PathVariable Long id, Model model) {
        Jugador jugador = jugadorRepository.findById(id).orElse(null);
        model.addAttribute("jugador", jugador);
        
        Club club = clubRepository.findAll().stream()
            .filter(c -> c.getJugadores() != null && c.getJugadores().stream().anyMatch(j -> j.getId().equals(id)))
            .findFirst().orElse(null);
        model.addAttribute("club", club);
        
        return "jugador-detalle";
    }
    
    @GetMapping("/jugador-form")
    public String jugadorForm(Model model) {
        model.addAttribute("jugador", new Jugador());
        model.addAttribute("clubes", clubRepository.findAll());
        return "jugador-form";
    }
    
    @GetMapping("/jugador-form/{id}")
    public String jugadorEditForm(@PathVariable Long id, Model model) {
        Jugador jugador = jugadorRepository.findById(id).orElse(null);
        model.addAttribute("jugador", jugador);
        model.addAttribute("clubes", clubRepository.findAll());
        return "jugador-form";
    }
    
    @PostMapping("/jugador/save")
    public String saveJugador(@ModelAttribute Jugador jugador,
                               @RequestParam(value = "clubId", required = false) Long clubId,
                               RedirectAttributes redirectAttributes) {
        try {
            jugadorRepository.save(jugador);
            if (clubId != null && clubId > 0) {
                Club club = clubRepository.findById(clubId).orElse(null);
                if (club != null) {
                    if (club.getJugadores() == null) {
                        club.setJugadores(new java.util.ArrayList<>());
                    }
                    if (!club.getJugadores().contains(jugador)) {
                        club.getJugadores().add(jugador);
                        clubRepository.save(club);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("success", "Jugador guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el jugador");
        }
        return "redirect:/jugadores";
    }
    
    @GetMapping("/jugador/delete/{id}")
    public String deleteJugador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<Club> clubes = clubRepository.findAll();
            for (Club club : clubes) {
                if (club.getJugadores() != null && club.getJugadores().removeIf(j -> j.getId().equals(id))) {
                    clubRepository.save(club);
                }
            }
            jugadorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Jugador eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el jugador");
        }
        return "redirect:/jugadores";
    }
    
    // ==================== CRUD ASOCIACIONES ====================
    @GetMapping("/asociaciones")
    public String listAsociaciones(Model model) {
        model.addAttribute("asociaciones", asociacionRepository.findAll());
        model.addAttribute("clubes", clubRepository.findAll());
        return "asociaciones";
    }
    
    @GetMapping("/asociacion-detalle/{id}")
    public String asociacionDetalle(@PathVariable Long id, Model model) {
        Asociacion asociacion = asociacionRepository.findById(id).orElse(null);
        model.addAttribute("asociacion", asociacion);
        
        List<Club> clubes = clubRepository.findAll().stream()
            .filter(c -> c.getAsociacion() != null && c.getAsociacion().getId().equals(id))
            .collect(Collectors.toList());
        model.addAttribute("clubes", clubes);
        
        return "asociacion-detalle";
    }
    
    @GetMapping("/asociacion-form")
    public String asociacionForm(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        model.addAttribute("clubes", clubRepository.findAll());
        return "asociacion-form";
    }
    
    @GetMapping("/asociacion-form/{id}")
    public String asociacionEditForm(@PathVariable Long id, Model model) {
        Asociacion asociacion = asociacionRepository.findById(id).orElse(null);
        model.addAttribute("asociacion", asociacion);
        model.addAttribute("clubes", clubRepository.findAll());
        return "asociacion-form";
    }
    
    @PostMapping("/asociacion/save")
    public String saveAsociacion(@ModelAttribute Asociacion asociacion,
                                  @RequestParam(value = "clubId", required = false) Long clubId,
                                  RedirectAttributes redirectAttributes) {
        try {
            asociacionRepository.save(asociacion);
            if (clubId != null && clubId > 0) {
                Club club = clubRepository.findById(clubId).orElse(null);
                if (club != null) {
                    club.setAsociacion(asociacion);
                    clubRepository.save(club);
                }
            }
            redirectAttributes.addFlashAttribute("success", "Asociación guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la asociación");
        }
        return "redirect:/asociaciones";
    }
    
    @GetMapping("/asociacion/delete/{id}")
    public String deleteAsociacion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<Club> clubes = clubRepository.findAll();
            for (Club club : clubes) {
                if (club.getAsociacion() != null && club.getAsociacion().getId().equals(id)) {
                    club.setAsociacion(null);
                    clubRepository.save(club);
                }
            }
            asociacionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Asociación eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la asociación");
        }
        return "redirect:/asociaciones";
    }
    
    // ==================== CRUD COMPETICIONES ====================
    @GetMapping("/competiciones")
    public String listCompeticiones(Model model) {
        model.addAttribute("competiciones", competicionRepository.findAll());
        model.addAttribute("clubes", clubRepository.findAll());
        return "competiciones";
    }
    
    @GetMapping("/competicion-detalle/{id}")
    public String competicionDetalle(@PathVariable Long id, Model model) {
        Competicion competicion = competicionRepository.findById(id).orElse(null);
        model.addAttribute("competicion", competicion);
        
        List<Club> clubes = clubRepository.findAll().stream()
            .filter(c -> c.getCompeticiones() != null && c.getCompeticiones().stream().anyMatch(comp -> comp.getId().equals(id)))
            .collect(Collectors.toList());
        model.addAttribute("clubes", clubes);
        
        return "competicion-detalle";
    }
    
    @GetMapping("/competicion-form")
    public String competicionForm(Model model) {
        model.addAttribute("competicion", new Competicion());
        model.addAttribute("clubes", clubRepository.findAll());
        return "competicion-form";
    }
    
    @GetMapping("/competicion-form/{id}")
    public String competicionEditForm(@PathVariable Long id, Model model) {
        Competicion competicion = competicionRepository.findById(id).orElse(null);
        model.addAttribute("competicion", competicion);
        model.addAttribute("clubes", clubRepository.findAll());
        return "competicion-form";
    }
    
    @PostMapping("/competicion/save")
    public String saveCompeticion(@ModelAttribute Competicion competicion,
                                   @RequestParam(value = "clubIds", required = false) List<Long> clubIds,
                                   RedirectAttributes redirectAttributes) {
        try {
            competicionRepository.save(competicion);
            if (clubIds != null && !clubIds.isEmpty()) {
                List<Club> clubes = clubRepository.findAllById(clubIds);
                for (Club club : clubes) {
                    if (club.getCompeticiones() == null) {
                        club.setCompeticiones(new java.util.ArrayList<>());
                    }
                    if (!club.getCompeticiones().contains(competicion)) {
                        club.getCompeticiones().add(competicion);
                        clubRepository.save(club);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("success", "Competición guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la competición");
        }
        return "redirect:/competiciones";
    }
    
    @GetMapping("/competicion/delete/{id}")
    public String deleteCompeticion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<Club> clubes = clubRepository.findAll();
            for (Club club : clubes) {
                if (club.getCompeticiones() != null && club.getCompeticiones().removeIf(c -> c.getId().equals(id))) {
                    clubRepository.save(club);
                }
            }
            competicionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Competición eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la competición");
        }
        return "redirect:/competiciones";
    }
    
    // ==================== API PARA DATOS DEMO ====================
    @PostMapping("/api/demo-data")
    @ResponseBody
    public String createDemoData() {
        try {
            Asociacion fcf = new Asociacion("FCF", "Colombia", "Ramón Jesurún");
            Asociacion afa = new Asociacion("AFA", "Argentina", "Claudio Tapia");
            asociacionRepository.saveAll(Arrays.asList(fcf, afa));
            
            Entrenador entrenador1 = new Entrenador("Alberto", "Gamero", 58, "Colombiano");
            Entrenador entrenador2 = new Entrenador("Miguel", "Russo", 65, "Argentino");
            Entrenador entrenador3 = new Entrenador("Néstor", "Pékerman", 74, "Colombiano");
            entrenadorRepository.saveAll(Arrays.asList(entrenador1, entrenador2, entrenador3));
            
            Jugador jugador1 = new Jugador("Radamel", "Falcao", 9, "Delantero");
            Jugador jugador2 = new Jugador("David", "Mackalister", 8, "Mediocampista");
            Jugador jugador3 = new Jugador("Juanfer", "Quintero", 10, "Mediapunta");
            Jugador jugador4 = new Jugador("James", "Rodríguez", 10, "Mediapunta");
            jugadorRepository.saveAll(Arrays.asList(jugador1, jugador2, jugador3, jugador4));
            
            Competicion libertadores = new Competicion("Copa Libertadores", 20000000, 
                LocalDate.of(2024, 2, 1), LocalDate.of(2024, 11, 30));
            Competicion sudamericana = new Competicion("Copa Sudamericana", 5000000,
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 12, 15));
            Competicion ligaColombia = new Competicion("Liga BetPlay", 3000000,
                LocalDate.of(2024, 1, 20), LocalDate.of(2024, 12, 20));
            competicionRepository.saveAll(Arrays.asList(libertadores, sudamericana, ligaColombia));
            
            Club millonarios = new Club("Millonarios", "El Campín", 1946);
            millonarios.setEntrenador(entrenador1);
            millonarios.setAsociacion(fcf);
            millonarios.setJugadores(Arrays.asList(jugador1, jugador3));
            millonarios.setCompeticiones(Arrays.asList(libertadores, ligaColombia));
            
            Club river = new Club("River Plate", "Monumental", 1901);
            river.setEntrenador(entrenador2);
            river.setAsociacion(afa);
            river.setJugadores(Arrays.asList(jugador2, jugador4));
            river.setCompeticiones(Arrays.asList(libertadores, sudamericana));
            
            Club nacional = new Club("Atlético Nacional", "Atanasio Girardot", 1947);
            nacional.setAsociacion(fcf);
            nacional.setJugadores(Arrays.asList(jugador3));
            nacional.setCompeticiones(Arrays.asList(libertadores, ligaColombia));
            
            clubRepository.saveAll(Arrays.asList(millonarios, river, nacional));
            
            return "Datos de ejemplo creados exitosamente!";
        } catch (Exception e) {
            return "Error al crear datos: " + e.getMessage();
        }
    }
}