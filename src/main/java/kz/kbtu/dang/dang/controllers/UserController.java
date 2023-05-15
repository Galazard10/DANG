package kz.kbtu.dang.dang.controllers;

import kz.kbtu.dang.dang.config.JwtService;
import kz.kbtu.dang.dang.entities.User;
import kz.kbtu.dang.dang.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    JwtService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable (name = "id") Integer id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return ResponseEntity.ok().body(user);
        }
        return new ResponseEntity<>("ERROR: USER NOT FOUND", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/user/password")
    public ResponseEntity<Object> updatePassword(@RequestHeader(name = "Authorization") String authorizationHeader,
                                                 @RequestParam("oldPassword") String oldPass,
                                                 @RequestParam("newPassword") String newPass) throws Exception{
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>("ERROR: USER NOT FOUND", HttpStatus.NOT_FOUND);
        }
        if(!passwordEncoder.matches(oldPass, user.get().getPassword())){
            return new ResponseEntity<>("ERROR: Invalid Old Password", HttpStatus.BAD_REQUEST);
        }
        String encodedNewPassword = passwordEncoder.encode(newPass);
        user.get().setPassword(encodedNewPassword);
        userRepository.save(user.get());
        return new ResponseEntity<>("SUCCESS: Password updated successfully", HttpStatus.OK);
    }

    @PutMapping("/user/email")
    public ResponseEntity<Object> updateEmail(@RequestHeader(name = "Authorization") String authorizationHeader,
                                              @RequestParam("newEmail") String newEmail) throws Exception{
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>("ERROR: USER NOT FOUND", HttpStatus.NOT_FOUND);
        }
        user.get().setEmail(newEmail);
        userRepository.save(user.get());
        return new ResponseEntity<>("SUCCESS: E-MAIL UPDATED SUCCESSFULLY", HttpStatus.OK);
    }

    @PutMapping("/user/user-info")
    public ResponseEntity<Object> updateUserInfo(@RequestHeader(name = "Authorization") String authorizationHeader,
                                                 @RequestBody User info) throws Exception{
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>("ERROR: USER NOT FOUND", HttpStatus.NOT_FOUND);
        }
        user.get().setFirstname(info.getFirstname());
        user.get().setLastname(info.getLastname());
        userRepository.save(user.get());
        return new ResponseEntity<>("SUCCESS: USER INFO UPDATED SUCCESSFULLY", HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Object> deleteUser(@RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(email);
        if(user == null){
            return new ResponseEntity<>("ERROR: USER NOT FOUND", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(user.get().getId());
        return new ResponseEntity<>("SUCCEsS: USER DELETED SUCCESSFULLY", HttpStatus.OK);
    }

}
