package kr.co.broadwave.desk.accounts;

import kr.co.broadwave.desk.teams.Team;
import kr.co.broadwave.desk.teams.TeamRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author InSeok
 * Date : 2019-04-23
 * Time : 10:27
 * Remark :
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    @WithMockUser(value = "testuser",roles = {"ADMIN"})
    public void account_API_list() throws Exception{

        //givn
        Team t1 = Team.builder()
                .teamcode("A001")
                .teamname("TestTeam1")
                .remark("비고").build();
        teamRepository.save(t1);
        Account a1 = Account.builder()
                .userid("S0001")
                .username("테스트유저")
                .password("1234")
                .email("test@naver.com")
                .role(AccountRole.ROLE_ADMIN)
                .team(t1)
                .build();
        Account a2 = Account.builder()
                .userid("S0002")
                .username("테스트유저2")
                .password("1234")
                .email("test2@naver.com")
                .role(AccountRole.ROLE_ADMIN)
                .team(t1)
                .build();
        Account a3 = Account.builder()
                .userid("S0003")
                .username("신규유저")
                .password("1234")
                .email("test3@naver.com")
                .role(AccountRole.ROLE_ADMIN)
                .team(t1)
                .build();
        accountRepository.save(a1);
        accountRepository.save(a2);
        accountRepository.save(a3);


        //whenthen
        mockMvc.perform(post("/api/account/list")
                .with(csrf())
                .param("size","2")
                .param("page","0")
                .param("userid","S00")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.datalist").exists())
                .andExpect(jsonPath("data.total_rows").exists())
                .andExpect(jsonPath("data.total_rows").value("3"))

        ;

        accountRepository.delete(a1);
        accountRepository.delete(a2);
        accountRepository.delete(a3);
        teamRepository.delete(t1);

    }

    @Test
    @WithMockUser(value = "testuser",roles = {"ADMIN"})
    public void account_API_account() throws Exception{

        mockMvc.perform(post("/api/account/account")
                .with(csrf())
                .param("userid","admin")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.datarow").exists())
                .andExpect(jsonPath("data.datarow.userid").exists())
                .andExpect(jsonPath("data.datarow.team").exists())
                .andExpect(jsonPath("data.datarow.team.teamcode").exists())
                .andExpect(jsonPath("data.datarow.userid").value("admin"))
                ;
    }
    @Test
    @WithMockUser(value = "testuser",roles = {"ADMIN"})
    public void account_API_reg_and_modify_and_del() throws Exception{

        //given
        Team t1 = Team.builder()
                .teamcode("A001")
                .teamname("TestTeam1")
                .remark("비고").build();
        teamRepository.save(t1);

        //=========   save     =============

        //when then
        mockMvc.perform(post("/api/account/reg")
                .with(csrf())
                .param("userid","testcis")
                .param("username","테스트유저")
                .param("password","112233")
                .param("email","test@mail.com")
                .param("teamcode","A001")
                .param("mode","N")
                .param("role","ROLE_USER ")
        )
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Account> optionalAccount = accountRepository.findByUserid("testcis");
        Account account = optionalAccount.get();

        assertThat(optionalAccount.isPresent()).isEqualTo(true);
        assertThat(account.getUserid()).isEqualTo("testcis");

        //=========   modify     =============

        //when then
        mockMvc.perform(post("/api/account/modifyemail")
                .with(csrf())
                .param("userid","testcis")
                .param("email","modifytest@mail.com")
                .param("teamcode","A001")
                .param("mode","N")
                .param("role","ROLE_USER ")
        )
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Account> optionalAccountModify = accountRepository.findByUserid("testcis");
        Account accountModify = optionalAccountModify.get();

        assertThat(optionalAccountModify.isPresent()).isEqualTo(true);
        assertThat(accountModify.getUserid()).isEqualTo("testcis");
        assertThat(accountModify.getEmail()).isEqualTo("modifytest@mail.com");

        //=========== del ============

        //when
        mockMvc.perform(post("/api/account/del")
                .with(csrf())
                .param("userid",account.getUserid())
        )
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Optional<Account> optionalAccount1 = accountRepository.findByUserid(account.getUserid());
        assertThat(optionalAccount1.isPresent()).isEqualTo(false);

        teamRepository.delete(t1);
    }


}