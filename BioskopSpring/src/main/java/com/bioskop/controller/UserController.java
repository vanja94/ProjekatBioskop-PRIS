package com.bioskop.controller;

import java.time.LocalDate;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bioskop.repository.FilmRepository;
import com.bioskop.repository.KomentarRepository;
import com.bioskop.repository.KorisnikRepository;
import com.bioskop.repository.UlogaRepository;
import com.bioskop.security.UserService;
import com.bioskop.security.UserValidator;

import model.Film;
import model.Komentar;
import model.Korisnik;
import model.Uloga;

@Controller
@RequestMapping(value = "/userController")
public class UserController {
	
	@Autowired
	UlogaRepository ur;
	
	@Autowired
	KorisnikRepository kr;
	
	@Autowired
	UserValidator uv;
	
	@Autowired
	KomentarRepository komr;
	
	@Autowired
	FilmRepository fr;
	
	@Autowired
	UserService us;
	
	@RequestMapping(value="loginPage", method=RequestMethod.GET) 
	public String loginPage() {
		return "Logovanje";
	}
	
	/*@RequestMapping(value="loginCheck", method=RequestMethod.GET)
	public String loginCheck(String username, String password, HttpServletRequest request) {
		Korisnik k = kr.findByUsername(username);
		if (k == null) {
			request.setAttribute("loginFailed", true);
		}		
		return "Logovanje";
	}*/
	
	 @RequestMapping(value = "registerUser", method = RequestMethod.GET)
		public String newUser(Model model) {
		Korisnik k = new Korisnik();
		model.addAttribute("korisnik", k);
		return "Registracija";
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String sacuvajKorisnika(@ModelAttribute("korisnik") Korisnik k, BindingResult bindingResult) {
		
		uv.validate(k, bindingResult);
		
		
		if (bindingResult.hasErrors()) {
            return "Registracija";
        }
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		k.setPassword(passwordEncoder.encode(k.getPassword()));

		Uloga u = ur.findById(1).get();

		k.setUloga(u);
		u.addKorisnik(k);
		
		kr.save(k);
		
		return "Logovanje";
	}
	
	
	@RequestMapping(value = "saveKomentar", method = RequestMethod.POST)
	public String sacuvajKomentar(Integer filmid, String komentar, String ocena, HttpServletRequest request){
		
		Film f = fr.findById(filmid).get();
		Korisnik k = us.getUserFromSession();
		Komentar kom = new Komentar();
		
		if(k == null) {
			return "NisiKorisnik";
		}
		
		int ocenaInt;
		
		LocalDate datumKom = null;
		LocalDate datumOc = null;
		String datumKomentara = null;
		String datumOcene = null;
		
		if(komentar != null && !komentar.contentEquals("")) {
			datumKom = LocalDate.now();
			datumKomentara = datumKom.toString();
			kom.setDatumKom(datumKomentara);
			kom.setKomentar(komentar);
		}
		
		if(ocena != null) {
			datumOc = LocalDate.now();
			datumOcene = datumOc.toString();
			kom.setDatumOc(datumOcene);
			ocenaInt =  Integer.parseInt(ocena);
			kom.setOcena(ocenaInt);
		}	
		
		kom.setFilm(f);
		kom.setKorisnik(k);

		Komentar komm = komr.save(kom);
		request.getSession().setAttribute("komentar", komm);
		
		return "InfoOFilmu";
	}
	
}
