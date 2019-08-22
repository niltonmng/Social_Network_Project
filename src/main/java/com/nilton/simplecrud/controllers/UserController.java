package com.nilton.simplecrud.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nilton.simplecrud.domain.User;
import com.nilton.simplecrud.domain.services.UserService;
import com.nilton.simplecrud.dto.UserDTO;

@RestController // define que é um controller da api rest a ser construida
@RequestMapping(value="/users") // caminho do endpoint
public class UserController {
	
	@Autowired
	private UserService service;
	
	@RequestMapping(method = RequestMethod.GET) // definindo um método get para o caminho acima.
	public ResponseEntity<List<UserDTO>> findAll(){
		List<User> list = service.findAll(); // isto permanece, pois temos que carregar normalmente a lista de users
//		List<UserDTO> listDto = list.stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
		List<UserDTO> listDto = this.transformListUserDtoToListUser(list);
		return ResponseEntity.ok().body(listDto); // response entity são as respostas padrao http
	}
	
	private List<UserDTO> transformListUserDtoToListUser(List<User> list) {
		List<UserDTO> output = new ArrayList<UserDTO>();
		for (User user : list) {
			UserDTO current = new UserDTO(user);
			output.add(current);
		}
		return output;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
 	public ResponseEntity<UserDTO> findById(@PathVariable String id) { // Pathvariable indica que o id passado como parametro casa com o da url
		User obj = service.findById(id);
		return ResponseEntity.ok().body(new UserDTO(obj));
	}
	
	
	@RequestMapping(method=RequestMethod.POST)
 	public ResponseEntity<Void> insert(@RequestBody UserDTO objDto) { // @requestbody pega as informacoes passadas no parametro
		User obj = service.fromDtoToUser(objDto);
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri(); // colocamos um cabeçalho com a url do novo recurso criado, isso é uma boa prática, para isso usamos a linha 57, do spring.
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
 	public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO objDto) {
		User obj = service.findById(id);
		obj.setEmail(objDto.getEmail());
		obj.setName(objDto.getName());
		obj = service.update(obj);
		return ResponseEntity.ok().body(new UserDTO(obj));
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
 	public ResponseEntity<Void> delete(@PathVariable String id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
