package kr.co.broadwave.desk.record.responsibil;

import kr.co.broadwave.desk.record.Record;
import kr.co.broadwave.desk.teams.Team;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;

/**
 * @author Minkyu
 * Date : 2019-09-19
 * Remark :
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="ar_record_responsibil")
public class Responsibil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ar_respon_id")
    private Long id;

    @ManyToOne(targetEntity = Record.class,fetch = FetchType.LAZY)
    @JoinColumn(name="ar_id")
    private Record record;

    @Column(name="ar_employee_number") //사원
    private String arEmployeeNumber;
    @Column(name="ar_employee_name") //이름
    private String arEmployeeName;
    @ManyToOne(targetEntity = Team.class,fetch = FetchType.LAZY)
    @JoinColumn(name="ar_department_id")
    private Team team;
    @Column(name="insert_year") //작성연도
    private String insertYear;
}