package com.ninos.repository.implementation;

import com.ninos.dto.UserDTO;
import com.ninos.exception.ApiException;
import com.ninos.model.Role;
import com.ninos.model.User;
import com.ninos.model.UserPrinciple;
import com.ninos.repository.RoleRepository;
import com.ninos.repository.UserRepository;
import com.ninos.rowmapper.UserRowMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static com.ninos.enumeration.RoleType.ROLE_USER;
import static com.ninos.enumeration.verificationType.ACCOUNT;
import static com.ninos.query.UserQuery.*;
import static com.ninos.utils.SmsUtils.sendSMS;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@AllArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {


    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        // check the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0 ) throw new ApiException("Email already in use. Please use a different email and try again");
        // Save new user
        try {
            KeyHolder holder = new GeneratedKeyHolder(); // it will give the id in database
            SqlParameterSource parameters = getSqlParameterSource(user); // it will set all parameters that we want to set with the request
            jdbc.update(INSERT_USER_QUERY, parameters, holder);
            user.setId(Objects.requireNonNull(holder.getKey()).longValue());
            // Add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            // Send verification URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // Save URL in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            // Send email to user with verification URL
//            emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            // Return the newly created user
            return user;
            // If any error, throw exception with proper message
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }

    }




    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User found by email: " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }


    @Override
    public void sendVerificationCode(UserDTO user) {
        // DateFormatUtils and RandomStringUtils came from org.apache.commons library
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT); // 1: equivalent to 24 hours or it's mean 1 day
        String verificationCode = RandomStringUtils.randomAlphabetic(8);

        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code",verificationCode, "expirationDate",expirationDate));
            //sendSMS(user.getPhone(), "From Invoice application \nVerification code\n" + verificationCode);
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }

    }


    // methods section

    private Integer getEmailCount(String email) {
        // it will count how many emails we have with this specific email
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }


    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }


    private String getVerificationUrl(String key, String type){
       return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else {
            log.info("User found in the database {}", email);
            return new UserPrinciple(user,roleRepository.getRoleByUserId(user.getId()).getPermission());
        }

    }








}
