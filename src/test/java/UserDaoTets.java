import com.emersun.imi.Application;
import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.User;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.configs.Constants;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.repositories.UserRepository;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


public class UserDaoTets extends AbstractMongodbPurging {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Test
    public void insertUser_success() {
        User user = new User(false,"09124337522");
        userRepository.save(user).block();
        assertThat(userRepository.findByPhoneNumber("09124337522").block()).isNotNull();
    }

    @Test
    public void UserConstructor_success() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setFirstname("farzam");
        userAccountDto.setLastname("vatanzadeh");
        userAccountDto.setUsername("farzamvat");
        userAccountDto.setMobile("09124337522");
        userAccountDto.setBanned(false);
        userAccountDto.setRole("");
        UserAccount userAccount = new UserAccount(userAccountDto);
        assertThat(userAccount.getRole()).isEqualTo(Constants.OPERATOR_ROLE);
        assertThat(userAccount.getPermissions()).contains(Permission.values());
        assertThat(userAccount.getCanReadFromDate()).isNotNull();

        userAccountDto.setPermissions(Sets.newHashSet(Permission.values()));
        UserAccount withPermission = new UserAccount(userAccountDto);
        assertThat(withPermission.getPermissions()).contains(Permission.values());
    }
}
