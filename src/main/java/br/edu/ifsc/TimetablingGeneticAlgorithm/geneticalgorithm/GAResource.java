package br.edu.ifsc.TimetablingGeneticAlgorithm.geneticalgorithm;


import br.edu.ifsc.TimetablingGeneticAlgorithm.dtos.DTOSchedule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/ga")
public class GAResource {
    public GAResource() {
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public ResponseEntity<List<DTOSchedule>> getGA() {
        try {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            return new ResponseEntity<>(geneticAlgorithm.process("src//assets//configuracoes.txt"), HttpStatus.OK);
        } catch (IOException iEx) {
            System.err.println("Erro de IO");
            iEx.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ClassNotFoundException cEx) {
            System.err.println("Erro de alguma entidade não encontrada");
            cEx.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            System.err.println("Erro na execução");
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
