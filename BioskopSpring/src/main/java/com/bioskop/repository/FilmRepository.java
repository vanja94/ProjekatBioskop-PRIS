package com.bioskop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Film;
import model.Projekcija;

public interface FilmRepository extends JpaRepository<Film, Integer> {
	
	@Query("SELECT DISTINCT f FROM Film f, Komentar k WHERE k.film = f.filmID AND k.ocena > 0 AND k.datumKom BETWEEN :datum2 AND :datum1")
	public List<Film> vratiFilmoveMesecDana(@Param("datum1") String datum1, @Param("datum2") String datum2);
	
}
