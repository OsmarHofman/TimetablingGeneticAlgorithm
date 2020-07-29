package br.edu.ifsc.TimetablingGeneticAlgorithm.resources;


import br.edu.ifsc.TimetablingGeneticAlgorithm.util.DTOSchedule;
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


    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public ResponseEntity<List<DTOSchedule>> getGA() {
        try {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            //TODO verificar se a List<DTOSchedule> sem formatação explícita causa problema no frontend
            return new ResponseEntity<>(geneticAlgorithm.process("src\\assets\\configuracoes.txt"), HttpStatus.OK);
        } catch (IOException iEx) {
            iEx.printStackTrace();
            System.err.println("Erro de IO");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ClassNotFoundException cEx) {
            cEx.printStackTrace();
            System.err.println("Erro de alguma entidade não encontrada");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Erro na execução");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
