package kr.co.broadwave.desk.configs;

import kr.co.broadwave.desk.accounts.Account;
import kr.co.broadwave.desk.accounts.AccountRole;
import kr.co.broadwave.desk.accounts.AccountService;
import kr.co.broadwave.desk.bscodes.ApprovalType;
import kr.co.broadwave.desk.bscodes.CodeType;
import kr.co.broadwave.desk.mastercode.MasterCode;
import kr.co.broadwave.desk.mastercode.MasterCodeService;
import kr.co.broadwave.desk.teams.Team;
import kr.co.broadwave.desk.teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author InSeok
 * Date : 2019-04-01
 * Time : 14:21
 * Remark : 구동전실행(팀과 관리자 추가)
 */
@Component
public class AppRunner implements ApplicationRunner {
    @Autowired
    TeamService teamService;
    @Autowired
    AccountService accountService;
    @Autowired
    MasterCodeService masterCodeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //직급
        MasterCode position1 = MasterCode.builder()
                .id(1L)
                .codeType(CodeType.C0001)
                .code("ADMIN")
                .name("관리자")
                .remark("최초시스템자동생성")
                .insert_id("system")
                .insertDateTime(LocalDateTime.now())
                .modify_id("system")
                .modifyDateTime(LocalDateTime.now())
                .build();
        Optional<MasterCode> optionalMasterCode = masterCodeService.findByCoAndCodeTypeAndCode(position1.getCodeType(), position1.getCode());
        if (!optionalMasterCode.isPresent()){
            masterCodeService.save(position1);
        }else{
            position1 = optionalMasterCode.get();
        }


        //팀저장

        Team team1 = Team.builder()
                .teamcode("T00001")
                .teamname("시스템관리")
                .remark("최초생성")
                .insertDateTime(LocalDateTime.now())
                .insert_id("system")
                .build();
        if(!teamService.findByTeamcode(team1.getTeamcode()).isPresent()) {
            teamService.tesmSave(team1);
        }else{
            team1 = teamService.findByTeamcode(team1.getTeamcode()).get();
        }
//노후인프라가 없어진 관계로 주석처리함. 2020-08-03 김민규
//        Team team2 = Team.builder()
//                .teamcode("T00002")
//                .teamname("노후인프라센터")
//                .remark("최초생성")
//                .insertDateTime(LocalDateTime.now())
//                .insert_id("system")
//                .build();
//        if(!teamService.findByTeamcode(team2.getTeamcode()).isPresent()) {
//            teamService.tesmSave(team2);
//
//        }else{
//            team2 = teamService.findByTeamcode(team2.getTeamcode()).get();
//        }
        //사용자저장
        Account account1 = Account.builder()
                .userid("admin")
                .username("관리자")
                .email("admin@mail.com")
                .cellphone("010-1111-2222")
                .password("123789")
                .position(position1)
                .approvalType(ApprovalType.AT02)
                .insertDateTime(LocalDateTime.now())
                .insert_id("system")
                .role(AccountRole.ROLE_ADMIN)
                .build();
        account1.setTeam(team1);
        if(!accountService.findByUserid(account1.getUserid()).isPresent()){
            accountService.saveAccount(account1);
        }








    }

}
