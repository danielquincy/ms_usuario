package com.microservicio.service.impl;

import com.microservicio.exception.EmailException;
import com.microservicio.exception.PasswordException;
import com.microservicio.exception.UserExistsException;
import com.microservicio.model.Telefono;
import com.microservicio.model.Usuario;
import com.microservicio.model.dto.RegistroRequest;
import com.microservicio.model.dto.UsuarioResponseDto;
import com.microservicio.repository.TelefonoDao;
import com.microservicio.repository.UsuarioDao;
import com.microservicio.service.UsuarioService;
import com.microservicio.utils.JwtUtil;
import com.microservicio.utils.UsuarioMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao oUsuarioDao;
    private final TelefonoDao oTelefonoDao;
    private final PasswordEncoder oPass;
    private final Pattern emailPattern;
    private final JwtUtil jwtUtil;
    private final UsuarioMapper mapper;
    @Value("${security.password.regex}")
    private String passwordRegex;

    @Autowired
    public UsuarioServiceImpl(final UsuarioDao oUsuarioDao,
                              final TelefonoDao oTelefonoDao,
                              final PasswordEncoder oPass,
                              final Pattern emailPattern,
                              final JwtUtil jwtUtil,
                              final UsuarioMapper mapper) {
        this.oUsuarioDao = oUsuarioDao;
        this.oTelefonoDao = oTelefonoDao;
        this.oPass = oPass;
        this.emailPattern = emailPattern;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    @Override
    public Boolean validarEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    @Override
    public UsuarioResponseDto registraUsuario(RegistroRequest prUsuario) throws Exception {

        if (oUsuarioDao.findByUsername(prUsuario.getUsername()).isPresent()) {
            throw new UserExistsException("El nombre de usuario ya existe.");
        }

        if (!validarEmail(prUsuario.getEmail())) {
            throw new EmailException("Email inválido: El formato debe ser tu_correo@dominio.cl");
        }

        if (oUsuarioDao.findAll().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(prUsuario.getEmail()))) {
            throw new EmailException("El correo ingresado ya se encuentra registrado.");
        }

        Pattern passPattern = Pattern.compile(passwordRegex);

        if (!passPattern.matcher(prUsuario.getPassword()).matches()) {
            throw new PasswordException("La contraseña no cumple con el formato correcto");
        }

        String token = jwtUtil.generarToken(prUsuario);

        prUsuario.setPassword(oPass.encode(prUsuario.getPassword()));

        Usuario oNuevo = Usuario.builder()
                .username(prUsuario.getUsername())
                .email(prUsuario.getEmail())
                .password(prUsuario.getPassword())
                .token(token)
                .phones(prUsuario.getPhones())
                .build();

        oUsuarioDao.save(oNuevo);

        oNuevo.getPhones().forEach(
                x -> {
                    Telefono oTel = new Telefono();
                    oTel.setNumber(x.getNumber());
                    oTel.setCitycode(x.getCitycode());
                    oTel.setContrycode(x.getContrycode());
                    oTelefonoDao.save(oTel);
                }
        );

        return mapper.toDto(oNuevo);

    }

    @Override
    public Usuario update(UUID id, Usuario prUsuario) throws Exception {
        Usuario oUsuario = findUsuarioById(id);
        if (Objects.nonNull(oUsuario)) {

            if (prUsuario.getUsername() != null) {
                if (oUsuarioDao.findAll().stream().anyMatch(x -> x.getUsername().equalsIgnoreCase(prUsuario.getUsername())
                        && !x.getId().equals(prUsuario.getId()))) {
                    throw new UserExistsException("El nombre de usuario ya existe.");
                }
                oUsuario.setUsername(prUsuario.getUsername());
            }

            if (oUsuarioDao.findAll().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(prUsuario.getEmail())
                    && !x.getId().equals(prUsuario.getId()))) {

                if (!validarEmail(prUsuario.getEmail())) {
                    throw new EmailException("Email inválido: El formato debe ser tu_correo@dominio.cl");
                }

                if (oUsuarioDao.findAll().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(prUsuario.getEmail())
                        && !x.getId().equals(prUsuario.getId()))) {
                    throw new EmailException("El correo ingresado ya se encuentra registrado.");
                }

                oUsuario.setEmail(prUsuario.getEmail());
            }

            if (prUsuario.getPassword() != null) {
                // Si manda password nuevo, hay que encriptarlo de nuevo
                oUsuario.setPassword(oPass.encode(prUsuario.getPassword()));
            }

            oTelefonoDao.saveAll(oUsuario.getPhones());

            return oUsuarioDao.save(oUsuario);
        } else {
            throw new UserExistsException("Usuario no encontrado");
        }
    }

    @Override
    public Usuario patch(UUID id, RegistroRequest userDetails) throws Exception {
        Usuario oUsuario = findUsuarioById(id);
        if (Objects.nonNull(oUsuario)) {

            if (userDetails.getUsername() != null) {
                if (oUsuarioDao.findByUsername(userDetails.getUsername()).isPresent()) {
                    throw new UserExistsException("El nombre de usuario ya existe.");
                }
                oUsuario.setUsername(userDetails.getUsername());

            }

            if (userDetails.getEmail() != null) {
                if (!validarEmail(userDetails.getEmail())) {
                    throw new EmailException("Email inválido: El formato debe ser tu_correo@dominio.cl");
                }

                if (oUsuarioDao.findAll().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(userDetails.getEmail()))) {
                    throw new EmailException("El correo ingresado ya se encuentra registrado.");
                }

                oUsuario.setEmail(userDetails.getEmail());
            }

            if (userDetails.getPassword() != null) {
                // Si manda password nuevo, hay que encriptarlo de nuevo
                oUsuario.setPassword(oPass.encode(userDetails.getPassword()));
            }

            return oUsuarioDao.save(oUsuario);
        } else {
            throw new UserExistsException("Usuario no encontrado");
        }
    }

    @Override
    public void delete(UUID id) throws Exception {
        Usuario oUsuario = findUsuarioById(id);
        if (Objects.nonNull(oUsuario)) {
            oUsuarioDao.delete(oUsuario);
        } else {
            throw new UserExistsException("Usuario no encontrado");
        }
    }

    @Override
    public List<Usuario> findAll() {
        return oUsuarioDao.findAll();
    }

    @Override
    public Usuario findById(UUID id) throws UserExistsException {
        Usuario oUsuario = findUsuarioById(id);
        if (Objects.nonNull(oUsuario)) {
            return oUsuario;
        } else {
            throw new UserExistsException("Usuario no encontrado");
        }
    }

    private Usuario findUsuarioById(UUID id) {
        return oUsuarioDao.findAll().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

}
