package kr.co.broadwave.desk.accounts;

import kr.co.broadwave.desk.common.AjaxResponse;
import kr.co.broadwave.desk.common.CommonUtils;
import kr.co.broadwave.desk.common.ResponseErrorCode;
import kr.co.broadwave.desk.teams.Team;
import kr.co.broadwave.desk.teams.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;


/**
 * @author InSeok
 * Date : 2019-03-25
 * Time : 10:07
 * Remark : Account 용 Rest Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/account")
public class AccountRestController {

    private AjaxResponse res = new AjaxResponse();
    HashMap<String, Object> data = new HashMap<>();

    private final AccountService accountService;
    private final TeamService teamService;
    private final ModelMapper modelMapper;
    private final LoginlogService loginlogService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountRestController(AccountService accountService, ModelMapper modelMapper
            , TeamService teamService, LoginlogService loginlogService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.teamService = teamService;
        this.loginlogService = loginlogService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("reg")
    public ResponseEntity accountSave(@ModelAttribute AccountMapperDto accountMapperDto, HttpServletRequest request){


        Account account = modelMapper.map(accountMapperDto, Account.class);
        Optional<Team> optionalTeam = teamService.findByTeamcode(accountMapperDto.getTeamcode());

        //패스워드를 입력하세요.
        if (accountMapperDto.getPassword() == null || accountMapperDto.getPassword() ==""){
            log.info(ResponseErrorCode.E006.getDesc());
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E006.getCode(), ResponseErrorCode.E006.getDesc()));
        }
        //아이디를 입력하세요.
        if (accountMapperDto.getUserid() == null || accountMapperDto.getUserid() ==""){
            log.info(ResponseErrorCode.E007.getDesc());
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E007.getCode(), ResponseErrorCode.E007.getDesc()));
        }

        //부서코드가 존재하지않으면
        if (!optionalTeam.isPresent()) {
            log.info(" 선택한 부서 DB 존재 여부 체크.  부서코드: '" + accountMapperDto.getTeamcode() +"'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E005.getCode(), ResponseErrorCode.E005.getDesc()));
        }else{
            Team team = optionalTeam.get();
            account.setTeam(team);
        }

        Optional<Account> optionalAccount = accountService.findByUserid(account.getUserid());

        String currentuserid = CommonUtils.getCurrentuser(request);


        //신규일때
        if (accountMapperDto.getMode().equals("N")) {
            //userid 중복체크
            if (optionalAccount.isPresent()) {
                log.info("사용자저장실패(사용자아이디중복) 사용자아이디: '" + account.getUserid() + "'");
                return ResponseEntity.ok(res.fail(ResponseErrorCode.E001.getCode(), ResponseErrorCode.E001.getDesc()));
            }
            account.setInsert_id(currentuserid);
            account.setInsertDateTime(LocalDateTime.now());
        }else{
            //수정일때
            if(!optionalAccount.isPresent()){
                log.info("사용자정보수정실패 : 사용자아이디: '" + account.getUserid() + "'");
                return ResponseEntity.ok(res.fail(ResponseErrorCode.E004.getCode(), ResponseErrorCode.E004.getDesc()));
            }else{
                account.setId(optionalAccount.get().getId());
                account.setInsert_id(optionalAccount.get().getInsert_id());
                account.setInsertDateTime(optionalAccount.get().getInsertDateTime());
            }
            account.setModify_id(currentuserid);
            account.setModifyDateTime(LocalDateTime.now());

        }



        Account accountSave =  this.accountService.saveAccount(account);

        log.info("사용자 저장 성공 : " + accountSave.toString() );
        return ResponseEntity.ok(res.success());

    }

    @PostMapping("modifyemail")
    public ResponseEntity accountSaveEmail(@ModelAttribute AccountMapperDtoModify accountMapperDto, HttpServletRequest request){


        Account account = modelMapper.map(accountMapperDto, Account.class);


        //아이디를 입력하세요.
        if (accountMapperDto.getUserid() == null || accountMapperDto.getUserid().equals("")){
            log.info(ResponseErrorCode.E009.getDesc());
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E009.getCode(), ResponseErrorCode.E009.getDesc()));
        }


        Optional<Account> optionalAccount = accountService.findByUserid(account.getUserid());

        String currentuserid = CommonUtils.getCurrentuser(request);


        //수정일때
        if(!optionalAccount.isPresent()){
            log.info("사용자정보(이메일)수정실패 : 사용자아이디: '" + account.getUserid() + "'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E004.getCode(), ResponseErrorCode.E004.getDesc()));
        }else{
            account.setId(optionalAccount.get().getId());
            account.setInsert_id(optionalAccount.get().getInsert_id());
            account.setInsertDateTime(optionalAccount.get().getInsertDateTime());
            account.setPassword(optionalAccount.get().getPassword());
            account.setTeam(optionalAccount.get().getTeam());
            account.setRole(optionalAccount.get().getRole());
            account.setUsername(optionalAccount.get().getUsername());

        }
        account.setModify_id(currentuserid);
        account.setModifyDateTime(LocalDateTime.now());




        Account accountSave =  this.accountService.modifyAccount(account);

        log.info("사용자 저장 성공 : " + accountSave.toString() );
        return ResponseEntity.ok(res.success());

    }

    @PostMapping("modifypassword")
    public ResponseEntity accountSavepassword(@ModelAttribute AccountMapperDtoModify accountMapperDto, HttpServletRequest request){


        Account account = modelMapper.map(accountMapperDto, Account.class);



        //아이디를 입력하세요.
        if (accountMapperDto.getUserid() == null || accountMapperDto.getUserid() ==""){
            log.info(ResponseErrorCode.E009.getDesc());
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E009.getCode(), ResponseErrorCode.E009.getDesc()));
        }


        Optional<Account> optionalAccount = accountService.findByUserid(account.getUserid());

        String currentuserid = CommonUtils.getCurrentuser(request);



        //수정일때
        if(!optionalAccount.isPresent()){
            log.info("사용자정보(이메일)수정실패 : 사용자아이디: '" + account.getUserid() + "'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E004.getCode(), ResponseErrorCode.E004.getDesc()));
        }else{
            //현재암호비교
            if (!passwordEncoder.matches(accountMapperDto.getOldpassword(),optionalAccount.get().getPassword())){
                return ResponseEntity.ok(res.fail(ResponseErrorCode.E010.getCode(), ResponseErrorCode.E010.getDesc()));
            }
            if( !accountMapperDto.getPassword().equals(accountMapperDto.getPasswordconfirm()) ){
                return ResponseEntity.ok(res.fail(ResponseErrorCode.E011.getCode(), ResponseErrorCode.E011.getDesc()));
            }

            account.setId(optionalAccount.get().getId());
            account.setInsert_id(optionalAccount.get().getInsert_id());
            account.setInsertDateTime(optionalAccount.get().getInsertDateTime());
            account.setEmail(optionalAccount.get().getEmail());
            account.setTeam(optionalAccount.get().getTeam());
            account.setRole(optionalAccount.get().getRole());
            account.setUsername(optionalAccount.get().getUsername());

        }
        account.setModify_id(currentuserid);
        account.setModifyDateTime(LocalDateTime.now());




        Account accountSave =  this.accountService.saveAccount(account);

        log.info("사용자 저장 성공 : " + accountSave.toString() );
        return ResponseEntity.ok(res.success());

    }


    @PostMapping("list")
    public ResponseEntity accountList(@RequestParam(value="userid", defaultValue="") String userid,
                                      @RequestParam(value="username", defaultValue="") String username,
                                      @RequestParam(value="teamname", defaultValue="") String teamname,
                                      Pageable pageable){

        log.info("부서 리스트 조회 / 조회조건 : userid / '" + userid + "' username / '" + username + "', teamname / '" + teamname + "'");


        Page<AccountDtoWithTeam> accounts = this.accountService.findAllBySearchStrings(userid, username, teamname, pageable);
        return CommonUtils.ResponseEntityPage(accounts);

    }
    @PostMapping("del")
    public ResponseEntity accountdel(@RequestParam (value="userid", defaultValue="") String userid
                                     ){
        log.info("사용자 삭제 / userid: " + userid );
        Optional<Account> optionalAccount = accountService.findByUserid(userid);
        //정보가있는지 체크
        if (!optionalAccount.isPresent()){
            log.info("사용자삭제실패 : 삭제할 데이터가 존재하지않음 , 삭제대상 userid : '" + userid + "'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E003.getCode(),ResponseErrorCode.E003.getDesc()));
        }
        Account account = optionalAccount.get();
        //사용중인지체크
        if( loginlogService.countByLoginAccount(account) > 0){
            log.info("사용자삭제실패 : LoginLog에서 사용중인데이터 , 삭제대상 userid : '" + userid + "'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E002.getCode(),ResponseErrorCode.E002.getDesc()));
        }

        accountService.delete(account);
        return ResponseEntity.ok(res.success());
    }

    @PostMapping("account")
    public ResponseEntity account(@RequestParam (value="userid", defaultValue="") String userid){

        log.info("단일사용자조회  / userid: '" + userid + "'");
        Optional<Account> optionalAccount = accountService.findByUserid(userid);

        if(!optionalAccount.isPresent()){
            log.info("단일사용자조회실패 : 조회할 데이터가 존재하지않음 , 조회대상 userid : '" + userid +"'");
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E004.getCode(),ResponseErrorCode.E004.getDesc()));
        }
        Account account = optionalAccount.get();



        data.clear();
        data.put("datarow",account);
        res.addResponse("data",data);
        log.info("단일사용자 조회 성공 : " + account.toString() );

        return ResponseEntity.ok(res.success());

    }

    /*
    @PostMapping ("team")
    public ResponseEntity team(@RequestParam (value="teamcode", defaultValue="") String teamcode
                            ){
        log.info("단일부서  / teamcode: " + teamcode );
        Optional<Team> optionalTeam = teamService.findByTeamcode(teamcode);

        if (!optionalTeam.isPresent()){
            log.info("단일부서조회실패 : 조회할 데이터가 존재하지않음 , 조회대상 teamcode : " + teamcode);
            return ResponseEntity.ok(res.fail(ResponseErrorCode.E004.getCode(),ResponseErrorCode.E004.getDesc()));
        }
        Team team = optionalTeam.get();

        data.clear();
        data.put("datarow",team);
        res.addResponse("data",data);

        log.info("단일부서 조회 성공 : " + team.toString() );
        return ResponseEntity.ok(res.success());

    }
     */


}
