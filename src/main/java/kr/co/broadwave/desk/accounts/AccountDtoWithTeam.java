package kr.co.broadwave.desk.accounts;

import kr.co.broadwave.desk.bscodes.DisasterType;
import lombok.*;

/**
 * @author InSeok
 * Date : 2019-03-29
 * Time : 10:31
 * Remark :
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDtoWithTeam {
    private String userid;
    private String username;
    private String email;
    private String cellphone;
    private AccountRole role;
    private String teamcode;
    private String teamname;
    private DisasterType disasterType;
    private String positioncode;
    private String positionname;

    public void setDisasterType(DisasterType disasterType) {
        this.disasterType = disasterType;
    }

    public String getDisasterType() {
        return disasterType.getDesc();
    }

    public String getPositioncode() {
        return positioncode;
    }

    public void setPositioncode(String positioncode) {
        this.positioncode = positioncode;
    }

    public String getPositionname() {
        return positionname;
    }

    public void setPositionname(String positionname) {
        this.positionname = positionname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role.getDesc();
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public String getTeamcode() {
        return teamcode;
    }

    public void setTeamcode(String teamcode) {
        this.teamcode = teamcode;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
}
