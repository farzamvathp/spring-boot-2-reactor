import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.configs.Constants;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.panel.dto.ChangePasswordDto;
import com.emersun.imi.panel.dto.LoginDto;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.panel.service.UserAccountService;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.security.TokenModel;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAccountIntegTest extends AbstractMongodbPurging {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test(expected = BaseException.class)
    public void createUserAccount_userAlreadyExists_fail() {
        UserAccountDto userAccountDto = new UserAccountDto("farzam","vatanzadeh","09124337522","farzamvat",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        userAccountRepository.save(new UserAccount(userAccountDto))
                .block();
        userAccountService.createUserAccount(userAccountDto).block();
    }

    @Test
    public void checkCurrentUserPermissionsBasedOnQueryType_failed() {
        Arrays.asList(Permission.values()).forEach(p -> {
            assertThat(userAccountService.checkCurrentUserPermissionsBasedOnQueryType(adminUsername, p.name()).block().getPermissions())
                    .contains(p);
        });
    }

    @Test
    public void checkCurrentUserPermissionsBasedOnQueryTypr_success() {
        assertThat(userAccountService.checkCurrentUserPermissionsBasedOnQueryType(adminUsername,Permission.CURRENT_VAS_SUB.name()).block()).isNotNull();
    }

    @Test
    public void createUserAccount_success() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        userAccountDto.setCanReadFromDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")));
        assertThat(userAccountService.createUserAccount(userAccountDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userAccountRepository.findByUsername(userAccountDto.getUsername()).block()).isNotNull();
    }

    @Test
    public void editUserAccount_success() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        userAccountDto.setCanReadFromDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")));
        assertThat(userAccountService.createUserAccount(userAccountDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        userAccountDto.setFirstname("farzam");
        userAccountDto.setLastname("zarinfam");
        assertThat(userAccountService.editUserAccount(userAccountDto).block().getBody().getFirstname()).isEqualTo(userAccountDto.getFirstname());
    }


    @Test
    public void login_success() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        assertThat(userAccountService.createUserAccount(userAccountDto).block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
        LoginDto loginDto = new LoginDto(userAccountDto.getUsername(),userAccountDto.getPassword());
        assertThat(userAccountService.login(loginDto).block().getBody()).isInstanceOf(TokenModel.class);
    }

    @Test(expected = BaseException.class)
    public void login_passwordNotMatch() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        assertThat(userAccountService.createUserAccount(userAccountDto).block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
        LoginDto loginDto = new LoginDto(userAccountDto.getUsername(),"incorrectPassword");
        assertThat(userAccountService.login(loginDto).block().getBody()).isInstanceOf(TokenModel.class);
    }

    @Test
    public void currentUserChangePassword_success() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("pass");
        changePasswordDto.setNewPassword("password");
        changePasswordDto.setRetryNewPassword("password");
        userAccountRepository.save(new UserAccount(userAccountDto)).block();
        assertThat(userAccountService.changeCurrentUserPassword(changePasswordDto,"saeedzarin").block().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test(expected = BaseException.class)
    public void currentUserChangePassword_passwordNotMatch() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("passW0Rd");
        changePasswordDto.setNewPassword("password");
        changePasswordDto.setRetryNewPassword("password");
        userAccountRepository.save(new UserAccount(userAccountDto)).block();
        userAccountService.changeCurrentUserPassword(changePasswordDto,"saeedzarin").block();
    }

    @Test(expected = BaseException.class)
    public void currentUserChangePassword_newPassRetryPass_notEqual() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("pass");
        changePasswordDto.setNewPassword("password");
        changePasswordDto.setRetryNewPassword("password1");
        userAccountRepository.save(new UserAccount(userAccountDto)).block();
        userAccountService.changeCurrentUserPassword(changePasswordDto,"saeedzarin").block();
    }

    @Test(expected = BaseException.class)
    public void currentUserChangePassword_newPassOldPassEqual() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("password"),false,Constants.OPERATOR_ROLE);
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("password");
        changePasswordDto.setNewPassword("password");
        changePasswordDto.setRetryNewPassword("password");
        userAccountRepository.save(new UserAccount(userAccountDto)).block();
        userAccountService.changeCurrentUserPassword(changePasswordDto,"saeedzarin").block();
    }

    @Test
    public void deleteUserAccount_success() {
        UserAccountDto userAccountDto = new UserAccountDto("saeed","zarinfam","09121234567","saeedzarin",passwordEncoder.encode("pass"),false,Constants.OPERATOR_ROLE);
        UserAccount userAccount = userAccountRepository.save(new UserAccount(userAccountDto)).block();
        userAccountService.deleteUserAccount(userAccount.getId()).block();
        assertThat(userAccountRepository.findByUsername(userAccount.getUsername()).block()).isNull();
    }
}
