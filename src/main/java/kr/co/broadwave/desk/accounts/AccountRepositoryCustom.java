package kr.co.broadwave.desk.accounts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author InSeok
 * Date : 2019-03-29
 * Time : 10:22
 * Remark :
 */
public interface AccountRepositoryCustom {
    Page<AccountDtoWithTeam> findAllBySearchStrings(String userid, String username,String teamname, Pageable pageable);
    Page<AccountDto> findAllByApproval(String username,String startDate, String endDate, Pageable pageable);

}
