package com.herokuapp.kamabye.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.herokuapp.kamabye.models.dto.LobDTO;
import com.herokuapp.kamabye.models.dto.ObraDTO;
import com.herokuapp.kamabye.models.dto.PartituraDTO;
import com.herokuapp.kamabye.models.postgresql.Partitura;

public interface IPartituraService {
	
	Partitura save(Partitura partitura);
	
	Partitura guardarConMarcaDeAgua(Partitura partitura);

	List<Partitura> saveAll(List<Partitura> partituras);

	List<Partitura> findAll();

	Page<Partitura> findAllPage(Integer page, Integer size);

	List<Partitura> findByExample(Partitura partitura);

	Page<Partitura> findByExampleWithPage(Partitura partitura, Integer page, Integer size);

	void delete(Long idPartitura);

	Partitura findById(Long idPartitura);
	
	List<PartituraDTO> jpqlFindAll();
	
	//List<PartituraDTO> jpqlFindAll();
	
	Set<PartituraDTO> jpqlfindByIdObra(Long idObra);

	List<Partitura> findByInstrumento(String instrumento);

	ObraDTO jpqlfindObraByIdObra(Long idObra);

	Set<String> jpqlfindInstrumentos(Long idObra);
	
	PartituraDTO jpqlfindById(Long idPartitura);

	List<LobDTO> jpqlLobFindByIdObra(Long idObra);

}