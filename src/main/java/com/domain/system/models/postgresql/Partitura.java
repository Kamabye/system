package com.domain.system.models.postgresql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.domain.system.models.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.io.ByteStreams;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Partituras")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor // Genera un constructor sin parámetros
//@RequiredArgsConstructor //Genera un constructor por cada parámetro de uso especial final o no nulo
@AllArgsConstructor // Genera un cosntructor para cada parámetro finales o no nulos
public class Partitura extends Auditable implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // GenerationType.IDENTITY AutoIncrement MYSQL MariaDB
	@Column(name = "idPartitura",updatable = false, nullable = false)
	private Long id;
	
	private String instrumento;
	
	@Lob
    private byte[] partituraPDF;
	
	@Lob
    private byte[] vistaPreviaPDF;
	
	@Lob
    private byte[] xml;
	
	@ManyToOne
    @JoinColumn(name = "idObra")
	@JsonBackReference
    private Obra obra;
	
	private static final long serialVersionUID = 1L;
	
	public void setPartituraPDFFromInputStream(InputStream inputStream) throws IOException {
        this.partituraPDF = ByteStreams.toByteArray(inputStream);
    }

}
