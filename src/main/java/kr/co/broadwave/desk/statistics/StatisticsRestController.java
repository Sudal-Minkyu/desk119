package kr.co.broadwave.desk.statistics;

import kr.co.broadwave.desk.accounts.AccountService;
import kr.co.broadwave.desk.accounts.AccountTeamDto;
import kr.co.broadwave.desk.common.AjaxResponse;
import kr.co.broadwave.desk.record.Record;
import kr.co.broadwave.desk.record.RecordService;
import kr.co.broadwave.desk.record.RecrodStatisticDto;
import kr.co.broadwave.desk.record.responsibil.Responsibil;
import kr.co.broadwave.desk.record.responsibil.ResponsibilListDto;
import kr.co.broadwave.desk.record.responsibil.ResponsibilRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Minkyu
 * Date : 2019-10-01
 * Remark :
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    private final RecordService recordService;
    private final ResponsibilRepository responsibilRepository;
    private final AccountService accountService;

    @Autowired
    public StatisticsRestController(RecordService recordService,
                                    ResponsibilRepository responsibilRepository,
                                    AccountService accountService) {
        this.recordService = recordService;
        this.responsibilRepository = responsibilRepository;
        this.accountService = accountService;
    }

    @Transactional
    @PostMapping("dataGraph")
    public ResponseEntity<Map<String, Object>> dataGraph() {
        AjaxResponse res = new AjaxResponse();
        HashMap<String, Object> data = new HashMap<>();

        List<Record> records = recordService.findAll();

        List<List<String>> circleDataColumns = new ArrayList<>(); // 원형 그래프데이터

        List<String> masters = new ArrayList<>();
        List<String> mastersSize = new ArrayList<>();
        List<String> masterCodeNames = new ArrayList<>();
        List<Integer> arState = new ArrayList<>();

        records.forEach(x -> arState.add(x.getArRecordState()));

        for (int i = 0; i < records.size(); i++) {
            if (arState.get(i) == 1) {
                masters.add(records.get(i).getArRelatedId().getName());
            }
        }
//        System.out.println("스테이트번호 arState :"+arState);
//        System.out.println("스테이트번호1번인 masters :"+masters);

        for (int i = 0; i < masters.size(); i++) {
            if (!mastersSize.contains(masters.get(i))) {
                mastersSize.add(masters.get(i));
            }
        }
//        System.out.println("스테이트번호 1번인 mastersSize :" + mastersSize);
//
//        log.info("mastersSize.size() :"+mastersSize.size());
//        log.info("masters.size() :"+masters.size());

        int count = 0;
        for (int j = 0; j < mastersSize.size(); j++) {
            String master = mastersSize.get(j);
            masterCodeNames.clear();
            for (int i = 0; i < mastersSize.size(); i++) {
                if (!masterCodeNames.contains(master)) {
                    masterCodeNames.add(master);
                }
            }
            for (int i = 0; i < masters.size(); i++) {
                if (masterCodeNames.contains(masters.get(i))) {
                    count++;
                }
            }
            masterCodeNames.add(Integer.toString(count));

//            System.out.println("masterCodeNames 데이터 : "+masterCodeNames);
//            System.out.println("건수 : "+count);

            int cnt = 0;
            int cnt2 = 1;
            circleDataColumns.add(Arrays.asList(masterCodeNames.get(cnt), masterCodeNames.get(cnt2)));
            count = 0;
        }

        List<List<String>> disastergraphDataColumns = new ArrayList<>();  // 재해재난 그래프데이터
        List<List<String>> facgraphDataColumns = new ArrayList<>();  // 조사시설물 그래프데이터
        List<List<String>> monthgraphDataColumns = new ArrayList<>(); // 월별 출동현황 그래프데이터

        LocalDate currentDate = LocalDate.now();
        int nowYear = currentDate.getYear();
        int productionYear = currentDate.getYear() - 1;
        String now = Integer.toString(nowYear);
        String production = Integer.toString(productionYear);
//        log.info("현재년도 : "+now);
//        log.info("재작년도 : "+production);
//        log.info("스테이트값 : "+arState);

        List<String> disasters = new ArrayList<>();
        List<String> facs = new ArrayList<>();
        List<String> monthDate = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            if (arState.get(i) == 1) {
                disasters.add(records.get(i).getArDisasterType());
                facs.add(records.get(i).getArFac());
                monthDate.add(records.get(i).getArIntoEnd());
            }
        }

//        log.info("재해분난 : "+disasters);
//        log.info("조사시설물 : "+facs);
//        log.info("월별건수 : "+monthDate);

        List<String> years = new ArrayList<>();
        List<String> months = new ArrayList<>();

        for (int i = 0; i < monthDate.size(); i++) {
            if (monthDate.get(i).equals("")) {
                String stryear = "";
                String strmonth = "";
                years.add(stryear);
                months.add(strmonth);
            } else {
                String stryear = monthDate.get(i).substring(0, 4);
                String strmonth = monthDate.get(i).substring(4, 6);
                years.add(stryear);
                months.add(strmonth);
            }
        }
//        log.info("years : "+years);
//        log.info("months : "+months);

        List<String> nowDisastersCnt = new ArrayList<>();
        List<String> productionDisastersCnt = new ArrayList<>();

        List<String> nowFacCnt = new ArrayList<>();
        List<String> productionFacCnt = new ArrayList<>();

        //재해·재난 유형
        int nowcnt01 = 0;
        int nowcnt02 = 0;
        int nowcnt03 = 0;
        int nowcnt04 = 0;
        int nowcnt05 = 0;
        int nowcnt06 = 0;
        int nowcnt07 = 0;

        int productioncnt01 = 0;
        int productioncnt02 = 0;
        int productioncnt03 = 0;
        int productioncnt04 = 0;
        int productioncnt05 = 0;
        int productioncnt06 = 0;
        int productioncnt07 = 0;

        for (int i = 0; i < disasters.size(); i++) {
            if (years.get(i).equals(now)) {
                if (disasters.get(i).startsWith("1")) {
                    nowcnt01++;
                }
                if (disasters.get(i).startsWith("1", 2)) {
                    nowcnt02++;
                }
                if (disasters.get(i).startsWith("1", 4)) {
                    nowcnt03++;
                }
                if (disasters.get(i).startsWith("1", 6)) {
                    nowcnt04++;
                }
                if (disasters.get(i).startsWith("1", 8)) {
                    nowcnt05++;
                }
                if (disasters.get(i).startsWith("1", 10)) {
                    nowcnt06++;
                }
                if (disasters.get(i).startsWith("1", 12)) {
                    nowcnt07++;
                }
            } else if (years.get(i).equals(production)) {
                if (disasters.get(i).startsWith("1")) {
                    productioncnt01++;
                }
                if (disasters.get(i).startsWith("1", 2)) {
                    productioncnt02++;
                }
                if (disasters.get(i).startsWith("1", 4)) {
                    productioncnt03++;
                }
                if (disasters.get(i).startsWith("1", 6)) {
                    productioncnt04++;
                }
                if (disasters.get(i).startsWith("1", 8)) {
                    productioncnt05++;
                }
                if (disasters.get(i).startsWith("1", 10)) {
                    productioncnt06++;
                }
                if (disasters.get(i).startsWith("1", 12)) {
                    productioncnt07++;
                }
            }
        }

        nowDisastersCnt.add(now);
        nowDisastersCnt.add(Integer.toString(nowcnt01));
        nowDisastersCnt.add(Integer.toString(nowcnt02));
        nowDisastersCnt.add(Integer.toString(nowcnt03));
        nowDisastersCnt.add(Integer.toString(nowcnt04));
        nowDisastersCnt.add(Integer.toString(nowcnt05));
        nowDisastersCnt.add(Integer.toString(nowcnt06));
        nowDisastersCnt.add(Integer.toString(nowcnt07));

        productionDisastersCnt.add(production);
        productionDisastersCnt.add(Integer.toString(productioncnt01));
        productionDisastersCnt.add(Integer.toString(productioncnt02));
        productionDisastersCnt.add(Integer.toString(productioncnt03));
        productionDisastersCnt.add(Integer.toString(productioncnt04));
        productionDisastersCnt.add(Integer.toString(productioncnt05));
        productionDisastersCnt.add(Integer.toString(productioncnt06));
        productionDisastersCnt.add(Integer.toString(productioncnt07));

//        log.info("2019 재해분난 카운트 : "+nowDisastersCnt);
//        log.info("2018 재해분난 카운트 : "+productionDisastersCnt);

        disastergraphDataColumns.add(productionDisastersCnt);
        disastergraphDataColumns.add(nowDisastersCnt);

        //조사시설물 현황
        nowcnt01 = 0;
        nowcnt02 = 0;
        nowcnt03 = 0;
        nowcnt04 = 0;
        nowcnt05 = 0;
        nowcnt06 = 0;
        nowcnt07 = 0;
        int nowcnt08 = 0;
        int nowcnt09 = 0;
        int nowcnt10 = 0;

        productioncnt01 = 0;
        productioncnt02 = 0;
        productioncnt03 = 0;
        productioncnt04 = 0;
        productioncnt05 = 0;
        productioncnt06 = 0;
        productioncnt07 = 0;
        int productioncnt08 = 0;
        int productioncnt09 = 0;
        int productioncnt10 = 0;

        for (int i = 0; i < facs.size(); i++) {
            if (years.get(i).equals(now)) {
                if (facs.get(i).startsWith("1")) {
                    nowcnt01++;
                }
                if (facs.get(i).startsWith("1", 2)) {
                    nowcnt02++;
                }
                if (facs.get(i).startsWith("1", 4)) {
                    nowcnt03++;
                }
                if (facs.get(i).startsWith("1", 6)) {
                    nowcnt04++;
                }
                if (facs.get(i).startsWith("1", 8)) {
                    nowcnt05++;
                }
                if (facs.get(i).startsWith("1", 10)) {
                    nowcnt06++;
                }
                if (facs.get(i).startsWith("1", 12)) {
                    nowcnt07++;
                }
                if (facs.get(i).startsWith("1", 14)) {
                    nowcnt08++;
                }
                if (facs.get(i).startsWith("1", 16)) {
                    nowcnt09++;
                }
                if (facs.get(i).startsWith("1", 18)) {
                    nowcnt10++;
                }
            } else if (years.get(i).equals(production)) {
                if (facs.get(i).startsWith("1")) {
                    productioncnt01++;
                }
                if (facs.get(i).startsWith("1", 2)) {
                    productioncnt02++;
                }
                if (facs.get(i).startsWith("1", 4)) {
                    productioncnt03++;
                }
                if (facs.get(i).startsWith("1", 6)) {
                    productioncnt04++;
                }
                if (facs.get(i).startsWith("1", 8)) {
                    productioncnt05++;
                }
                if (facs.get(i).startsWith("1", 10)) {
                    productioncnt06++;
                }
                if (facs.get(i).startsWith("1", 12)) {
                    productioncnt07++;
                }
                if (facs.get(i).startsWith("1", 14)) {
                    productioncnt08++;
                }
                if (facs.get(i).startsWith("1", 16)) {
                    productioncnt09++;
                }
                if (facs.get(i).startsWith("1", 18)) {
                    productioncnt10++;
                }
            }
        }

        nowFacCnt.add(now);
        nowFacCnt.add(Integer.toString(nowcnt01));
        nowFacCnt.add(Integer.toString(nowcnt02));
        nowFacCnt.add(Integer.toString(nowcnt03));
        nowFacCnt.add(Integer.toString(nowcnt04));
        nowFacCnt.add(Integer.toString(nowcnt05));
        nowFacCnt.add(Integer.toString(nowcnt06));
        nowFacCnt.add(Integer.toString(nowcnt07));
        nowFacCnt.add(Integer.toString(nowcnt08));
        nowFacCnt.add(Integer.toString(nowcnt09));
        nowFacCnt.add(Integer.toString(nowcnt10));

        productionFacCnt.add(production);
        productionFacCnt.add(Integer.toString(productioncnt01));
        productionFacCnt.add(Integer.toString(productioncnt02));
        productionFacCnt.add(Integer.toString(productioncnt03));
        productionFacCnt.add(Integer.toString(productioncnt04));
        productionFacCnt.add(Integer.toString(productioncnt05));
        productionFacCnt.add(Integer.toString(productioncnt06));
        productionFacCnt.add(Integer.toString(productioncnt07));
        productionFacCnt.add(Integer.toString(productioncnt08));
        productionFacCnt.add(Integer.toString(productioncnt09));
        productionFacCnt.add(Integer.toString(productioncnt10));

        System.out.println("2019 조사시설물 카운트 : "+nowFacCnt);
        System.out.println("2018 조사시설물 카운트 : "+productionFacCnt);

        facgraphDataColumns.add(productionFacCnt);
        facgraphDataColumns.add(nowFacCnt);

        //월별 출동 현황
        nowcnt01 = 0;
        nowcnt02 = 0;
        nowcnt03 = 0;
        nowcnt04 = 0;
        nowcnt05 = 0;
        nowcnt06 = 0;
        nowcnt07 = 0;
        nowcnt08 = 0;
        nowcnt09 = 0;
        nowcnt10 = 0;
        int nowcnt11 = 0;
        int nowcnt12 = 0;

        productioncnt01 = 0;
        productioncnt02 = 0;
        productioncnt03 = 0;
        productioncnt04 = 0;
        productioncnt05 = 0;
        productioncnt06 = 0;
        productioncnt07 = 0;
        productioncnt08 = 0;
        productioncnt09 = 0;
        productioncnt10 = 0;
        int productioncnt11 = 0;
        int productioncnt12 = 0;

        for (int i = 0; i < months.size(); i++) {
            if (years.get(i).equals(now)) {
                if (months.get(i).equals("01")) {
                    nowcnt01++;
                } else if (months.get(i).equals("02")) {
                    nowcnt02++;
                } else if (months.get(i).equals("03")) {
                    nowcnt03++;
                } else if (months.get(i).equals("04")) {
                    nowcnt04++;
                } else if (months.get(i).equals("05")) {
                    nowcnt05++;
                } else if (months.get(i).equals("06")) {
                    nowcnt06++;
                } else if (months.get(i).equals("07")) {
                    nowcnt07++;
                } else if (months.get(i).equals("08")) {
                    nowcnt08++;
                } else if (months.get(i).equals("09")) {
                    nowcnt09++;
                } else if (months.get(i).equals("10")) {
                    nowcnt10++;
                } else if (months.get(i).equals("11")) {
                    nowcnt11++;
                } else if (months.get(i).equals("12")) {
                    nowcnt12++;
                }
            } else if (years.get(i).equals(production)) {
                if (months.get(i).equals("01")) {
                    productioncnt01++;
                } else if (months.get(i).equals("02")) {
                    productioncnt02++;
                } else if (months.get(i).equals("03")) {
                    productioncnt03++;
                } else if (months.get(i).equals("04")) {
                    productioncnt04++;
                } else if (months.get(i).equals("05")) {
                    productioncnt05++;
                } else if (months.get(i).equals("06")) {
                    productioncnt06++;
                } else if (months.get(i).equals("07")) {
                    productioncnt07++;
                } else if (months.get(i).equals("08")) {
                    productioncnt08++;
                } else if (months.get(i).equals("09")) {
                    productioncnt09++;
                } else if (months.get(i).equals("10")) {
                    productioncnt10++;
                } else if (months.get(i).equals("11")) {
                    productioncnt11++;
                } else if (months.get(i).equals("12")) {
                    productioncnt12++;
                }
            }
        }

        List<String> nowCnt = new ArrayList<>();
        List<String> productionCnt = new ArrayList<>();

        nowCnt.add(now);
        nowCnt.add(Integer.toString(nowcnt01));
        nowCnt.add(Integer.toString(nowcnt02));
        nowCnt.add(Integer.toString(nowcnt03));
        nowCnt.add(Integer.toString(nowcnt04));
        nowCnt.add(Integer.toString(nowcnt05));
        nowCnt.add(Integer.toString(nowcnt06));
        nowCnt.add(Integer.toString(nowcnt07));
        nowCnt.add(Integer.toString(nowcnt08));
        nowCnt.add(Integer.toString(nowcnt09));
        nowCnt.add(Integer.toString(nowcnt10));
        nowCnt.add(Integer.toString(nowcnt11));
        nowCnt.add(Integer.toString(nowcnt12));

        productionCnt.add(production);
        productionCnt.add(Integer.toString(productioncnt01));
        productionCnt.add(Integer.toString(productioncnt02));
        productionCnt.add(Integer.toString(productioncnt03));
        productionCnt.add(Integer.toString(productioncnt04));
        productionCnt.add(Integer.toString(productioncnt05));
        productionCnt.add(Integer.toString(productioncnt06));
        productionCnt.add(Integer.toString(productioncnt07));
        productionCnt.add(Integer.toString(productioncnt08));
        productionCnt.add(Integer.toString(productioncnt09));
        productionCnt.add(Integer.toString(productioncnt10));
        productionCnt.add(Integer.toString(productioncnt11));
        productionCnt.add(Integer.toString(productioncnt12));

//        log.info("현재 카운트 : "+nowCnt);
//        log.info("작년 카운트 : "+productionCnt);

        monthgraphDataColumns.add(productionCnt);
        monthgraphDataColumns.add(nowCnt);

//        log.info("출동요청기관 데이터 : "+circleDataColumns);
//        log.info("재해재난유형 데이터 : "+disastergraphDataColumns);
//        log.info("조사시설물 데이터 : "+facgraphDataColumns);
//        log.info("월별 출동 현황 데이터 : "+monthgraphDataColumns);

        // 부서별 출동 현황
        List<Responsibil> responsibils = responsibilRepository.findAll();

        List<String> teamsData = new ArrayList<>();
        List<String> teamgraphDataColumns = new ArrayList<>();

        List<String> teamNames = new ArrayList<>();
        List<String> teamName = new ArrayList<>();

        List<String> rankNames = new ArrayList<>();
        List<String> rankNamesNow = new ArrayList<>();
        List<String> rankNamesProduction = new ArrayList<>();
        List<String> rankNamesSize = new ArrayList<>();
        List<String> rankNamesCountNow = new ArrayList<>();
        List<String> rankNamesCountPro = new ArrayList<>();

        for (int i=0; i<responsibils.size(); i++) {
            if (responsibils.get(i).getInsertYear().equals(now)) {
                rankNamesNow.add(responsibils.get(i).getArEmployeeName());
            } else if (responsibils.get(i).getInsertYear().equals(production)) {
                rankNamesProduction.add(responsibils.get(i).getArEmployeeName());
            } else {
                rankNames.add(responsibils.get(i).getArEmployeeName());
            }
        }
        Map<String,Integer> map = new HashMap<>();
        List<String> nameList = null;
        List<String> nameList2 = null;
        if(rankNamesNow.size()>0) {
            int max1 = 0;
            map.clear();
            rankNamesSize.clear();
            for (int i = 0; i < rankNamesNow.size(); i++) {
                if (!rankNamesSize.contains(rankNamesNow.get(i))) {
                    rankNamesSize.add(rankNamesNow.get(i));
                    String name = rankNamesNow.get(i);
                    int cnt = 0;
                    for(int j=0; j<rankNamesNow.size(); j++){
                        if(rankNamesNow.get(j).equals(name)){
                            cnt++;
                        }
                    }
                    if(max1<cnt) {
                        max1 = cnt;
                    }

                    rankNamesCountNow.add(String.valueOf(cnt));
                    map.put(name,cnt);
                }
            }
            Collections.sort(rankNamesCountNow, Collections.reverseOrder() );
            nameList = new ArrayList<>(map.keySet());
            // 내림차순 //
            Collections.sort(nameList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return map.get(o2).compareTo(map.get(o1));
                }
            });

            data.put("max1",  max1);
            rankNamesCountNow.add(0,now+"년도 출동건수");
        }
        if(rankNamesProduction.size()>0) {
            int max2 = 0;
            map.clear();
            rankNamesSize.clear();
            for (int i = 0; i < rankNamesProduction.size(); i++) {
                if (!rankNamesSize.contains(rankNamesProduction.get(i))) {
                    rankNamesSize.add(rankNamesProduction.get(i));
                    String name = rankNamesProduction.get(i);
                    int cnt = 0;
                    for(int j=0; j<rankNamesProduction.size(); j++){
                        if(rankNamesProduction.get(j).equals(name)){
                            cnt++;
                        }
                    }
                    if(max2<cnt) {
                        max2 = cnt;
                    }
                    rankNamesCountPro.add(String.valueOf(cnt));
                    map.put(name,cnt);
                }
            }
            Collections.sort(rankNamesCountPro, Collections.reverseOrder() );
            nameList2 = new ArrayList<>(map.keySet());
            // 내림차순 //
            Collections.sort(nameList2, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return map.get(o2).compareTo(map.get(o1));
                }
            });

            data.put("max2",  max2);
            rankNamesCountPro.add(0,production+"년도 출동건수");
        }


//        log.info("스테이트번호 arState :"+arState);
        responsibils.forEach(x -> teamNames.add(x.getTeam().getTeamname()));
//        log.info("팀이름들 : "+teamNames);

        //배열 맨앞 빈칸채우기
        teamgraphDataColumns.add(" ");

        //등록된 중복부서 중복되지않게 정렬
        for (int i = 0; i < teamNames.size(); i++) {
            if (!teamsData.contains(teamNames.get(i))) {
                teamsData.add(teamNames.get(i));
            }
        }

        for (int j = 0; j < teamsData.size(); j++) {
            String team = teamsData.get(j);
            teamName.clear();
            for (int i = 0; i < teamsData.size(); i++) {
                if (!teamName.contains(team)) {
                    teamName.add(team);
                }
            }
            teamgraphDataColumns.add(Long.toString(teamNames.stream().filter(x -> x.contains(team)).count()));
        }

//        System.out.println("등록된 팀당 건수 team_data_columns : "+teamgraphDataColumns);
//        System.out.println("등록된 부서팀 teamsData : "+teamsData);

        data.put("nowYear",now);
        data.put("production",production);

        // 원형 그래프데이터
        data.put("circle_data_columns", circleDataColumns);
        // 재해재난 그래프데이터
        data.put("disaster_data_columns", disastergraphDataColumns);
        // 조사시설물 그래프데이터
        data.put("fac_data_columns", facgraphDataColumns);
        // 부서별 출동현황 그래프데이터
        data.put("team_data_columns", teamgraphDataColumns);
        data.put("teamsData", teamsData);
        // 월별 출동현황 그래프데이터
        data.put("month_data_columns", monthgraphDataColumns);
        // now 년도 출동랭킹 데이터
        data.put("nameList",nameList);
        data.put("rankNamesCountNow",rankNamesCountNow);
        // pro 년도 출동랭킹 데이터
        data.put("nameList2",nameList2);
        data.put("rankNamesCountPro",rankNamesCountPro);

        res.addResponse("data", data);
        return ResponseEntity.ok(res.success());
    }

    @PostMapping("dataGraphTypeList")
    public ResponseEntity<Map<String, Object>> dataGraphTypeList(@RequestParam(value = "typeName", defaultValue = "") String typeName,
                                                             @RequestParam(value = "num", defaultValue = "") String num,
                                                             Pageable pageable) {

        AjaxResponse res = new AjaxResponse();
        HashMap<String, Object> data = new HashMap<>();

//        log.info("typeName : " + typeName);

        Page<RecrodStatisticDto> records = recordService.findByStatisticList(typeName, pageable, num);
//        log.info("records : " + records.getContent());
//        log.info("records : " + records.getTotalElements());
        if (records.getTotalElements() > 0) {

            List<String> writeTeam = new ArrayList<>();
            for (int i = 0; i < records.getNumberOfElements(); i++) {
                AccountTeamDto accountLineUpDto = accountService.findByTeamUserid(records.getContent().get(i).getModify_id());
//                log.info("accountLineUpDto : "+accountLineUpDto.getTeamname());
                writeTeam.add(accountLineUpDto.getTeamname());
            }

            data.put("writeTeam", writeTeam);
            data.put("datalist", records.getContent());
            data.put("total_page", records.getTotalPages());
            data.put("current_page", records.getNumber() + 1);
            data.put("total_rows", records.getTotalElements());
            data.put("current_rows", records.getNumberOfElements());
        } else {
            data.put("total_page", records.getTotalPages());
            data.put("current_page", records.getNumber() + 1);
            data.put("total_rows", records.getTotalElements());
            data.put("current_rows", records.getNumberOfElements());
        }

        res.addResponse("data",data);
        return ResponseEntity.ok(res.success());
    }

    @Transactional
    @PostMapping("dataGraphType")
    public ResponseEntity<Map<String,Object>> dataGraphType(@RequestParam(value = "typeName", defaultValue = "") String typeName,
                                                            @RequestParam(value = "num", defaultValue = "") String num){

        AjaxResponse res = new AjaxResponse();
        HashMap<String, Object> data = new HashMap<>();

        List<RecrodStatisticDto> records = recordService.findByStatisticList2(typeName, num);

        List<List<String>> circleDataColumns = new ArrayList<>(); // 원형 그래프데이터

        List<String> masters = new ArrayList<>();
        List<String> mastersSize = new ArrayList<>();
        List<String> masterCodeNames = new ArrayList<>();
        List<Integer> arState  = new ArrayList<>();
        List<Long> ids  = new ArrayList<>();
        records.forEach(x -> arState.add(x.getArRecordState()));

        for(int i=0; i<records.size(); i++){
            ids.add(records.get(i).getId());
            if(arState.get(i) == 1) {
                masters.add(records.get(i).getArRelatedId());
            }
        }
//        log.info("스테이트번호 arState :"+arState);
//        log.info("스테이트번호1번인 masters :"+masters);

        for(int i=0; i<masters.size(); i++){
            if (!mastersSize.contains(masters.get(i))) {
                mastersSize.add(masters.get(i));
            }
        }
//        log.info("스테이트번호 1번인 mastersSize :"+mastersSize);
//
//        log.info("mastersSize.size() :"+mastersSize.size());
//        log.info("masters.size() :"+masters.size());

        int count = 0;
        for(int j=0; j<mastersSize.size(); j++) {
            String master = mastersSize.get(j);
            masterCodeNames.clear();
            for (int i = 0; i < mastersSize.size(); i++) {
                if (!masterCodeNames.contains(master)) {
                    masterCodeNames.add(master);
                }
            }
            for (int i = 0; i < masters.size(); i++) {
                if (masterCodeNames.contains(masters.get(i))) {
                    count++;
                }
            }
            masterCodeNames.add(Integer.toString(count));

//            System.out.println("masterCodeNames 데이터 : "+masterCodeNames);
//            System.out.println("건수 : "+count);

            int cnt = 0;
            int cnt2 = 1;
            circleDataColumns.add(Arrays.asList(masterCodeNames.get(cnt),masterCodeNames.get(cnt2)));
            count = 0;
        }
//        System.out.println("circleDataColumns : "+circleDataColumns);


        List<List<String>> disastergraphDataColumns = new ArrayList<>();  // 재해재난 그래프데이터
        List<List<String>> facgraphDataColumns = new ArrayList<>();  // 조사시설물 그래프데이터
        List<List<String>> monthgraphDataColumns = new ArrayList<>(); // 월별 출동현황 그래프데이터

        LocalDate currentDate = LocalDate.now();
        int nowYear = currentDate.getYear();
        int productionYear = currentDate.getYear()-1;
        String now = Integer.toString(nowYear);
        String production = Integer.toString(productionYear);
//        System.out.println("현재년도 : "+now);
//        System.out.println("재작년도 : "+production);
//        System.out.println("스테이트값 : "+arState);

        List<String> disasters = new ArrayList<>();
        List<String> facs = new ArrayList<>();
        List<String> monthDate = new ArrayList<>();

        for (int i=0; i<records.size(); i++){
            if(arState.get(i) == 1) {
                disasters.add(records.get(i).getArDisasterType());
                facs.add(records.get(i).getArFac());
                monthDate.add(records.get(i).getArIntoEnd());
            }
        }

//        System.out.println("재해분난 : "+disasters);
//        System.out.println("조사시설물 : "+facs);
//        System.out.println("월별건수 : "+monthDate);

        List<String> years = new ArrayList<>();
        List<String> months = new ArrayList<>();

        for(int i=0; i<monthDate.size(); i++){
            if(monthDate.get(i).equals("")){
                String stryear ="";
                String strmonth ="";
                years.add(stryear);
                months.add(strmonth);
            }else{
                String stryear =monthDate.get(i).substring(0,4);
                String strmonth =monthDate.get(i).substring(4,6);
                years.add(stryear);
                months.add(strmonth);
            }
        }
//        System.out.println("years : "+years);
//        System.out.println("months : "+months);

        List<String> nowDisastersCnt = new ArrayList<>();
        List<String> productionDisastersCnt = new ArrayList<>();

        List<String> nowFacCnt = new ArrayList<>();
        List<String> productionFacCnt = new ArrayList<>();

        //재해·재난 유형
        int nowcnt01 = 0; int nowcnt02 = 0; int nowcnt03 = 0;
        int nowcnt04 = 0; int nowcnt05 = 0; int nowcnt06 = 0;
        int nowcnt07 = 0;

        int productioncnt01 = 0; int productioncnt02 = 0; int productioncnt03 = 0;
        int productioncnt04 = 0; int productioncnt05 = 0; int productioncnt06 = 0;
        int productioncnt07 = 0;

        for (int i = 0; i < disasters.size(); i++) {
            if (years.get(i).equals(now)) {
                if (disasters.get(i).startsWith("1")) {
                    nowcnt01++;
                }
                if (disasters.get(i).startsWith("1", 2)) {
                    nowcnt02++;
                }
                if (disasters.get(i).startsWith("1", 4)) {
                    nowcnt03++;
                }
                if (disasters.get(i).startsWith("1", 6)) {
                    nowcnt04++;
                }
                if (disasters.get(i).startsWith("1", 8)) {
                    nowcnt05++;
                }
                if (disasters.get(i).startsWith("1", 10)) {
                    nowcnt06++;
                }
                if (disasters.get(i).startsWith("1", 12)) {
                    nowcnt07++;
                }
            } else if (years.get(i).equals(production)) {
                if (disasters.get(i).startsWith("1")) {
                    productioncnt01++;
                }
                if (disasters.get(i).startsWith("1", 2)) {
                    productioncnt02++;
                }
                if (disasters.get(i).startsWith("1", 4)) {
                    productioncnt03++;
                }
                if (disasters.get(i).startsWith("1", 6)) {
                    productioncnt04++;
                }
                if (disasters.get(i).startsWith("1", 8)) {
                    productioncnt05++;
                }
                if (disasters.get(i).startsWith("1", 10)) {
                    productioncnt06++;
                }
                if (disasters.get(i).startsWith("1", 12)) {
                    productioncnt07++;
                }
            }
        }

        nowDisastersCnt.add(now);
        nowDisastersCnt.add(Integer.toString(nowcnt01));
        nowDisastersCnt.add(Integer.toString(nowcnt02));
        nowDisastersCnt.add(Integer.toString(nowcnt03));
        nowDisastersCnt.add(Integer.toString(nowcnt04));
        nowDisastersCnt.add(Integer.toString(nowcnt05));
        nowDisastersCnt.add(Integer.toString(nowcnt06));
        nowDisastersCnt.add(Integer.toString(nowcnt07));

        productionDisastersCnt.add(production);
        productionDisastersCnt.add(Integer.toString(productioncnt01));
        productionDisastersCnt.add(Integer.toString(productioncnt02));
        productionDisastersCnt.add(Integer.toString(productioncnt03));
        productionDisastersCnt.add(Integer.toString(productioncnt04));
        productionDisastersCnt.add(Integer.toString(productioncnt05));
        productionDisastersCnt.add(Integer.toString(productioncnt06));
        productionDisastersCnt.add(Integer.toString(productioncnt07));

//        System.out.println("2019 재해분난 카운트 : "+nowDisastersCnt);
//        System.out.println("2018 재해분난 카운트 : "+productionDisastersCnt);

        disastergraphDataColumns.add(productionDisastersCnt);
        disastergraphDataColumns.add(nowDisastersCnt);

        //조사시설물 현황
        nowcnt01 = 0; nowcnt02 = 0; nowcnt03 = 0;
        nowcnt04 = 0; nowcnt05 = 0; nowcnt06 = 0;
        nowcnt07 = 0; int nowcnt08 = 0; int nowcnt09 = 0;
        int nowcnt10 = 0;

        productioncnt01 = 0; productioncnt02 = 0; productioncnt03 = 0;
        productioncnt04 = 0; productioncnt05 = 0; productioncnt06 = 0;
        productioncnt07 = 0; int productioncnt08 = 0; int productioncnt09 = 0;
        int productioncnt10 = 0;

        for (int i = 0; i < facs.size(); i++) {
            if (years.get(i).equals(now)) {
                if (facs.get(i).startsWith("1")) {
                    nowcnt01++;
                }
                if (facs.get(i).startsWith("1", 2)) {
                    nowcnt02++;
                }
                if (facs.get(i).startsWith("1", 4)) {
                    nowcnt03++;
                }
                if (facs.get(i).startsWith("1", 6)) {
                    nowcnt04++;
                }
                if (facs.get(i).startsWith("1", 8)) {
                    nowcnt05++;
                }
                if (facs.get(i).startsWith("1", 10)) {
                    nowcnt06++;
                }
                if (facs.get(i).startsWith("1", 12)) {
                    nowcnt07++;
                }
                if (facs.get(i).startsWith("1", 14)) {
                    nowcnt08++;
                }
                if (facs.get(i).startsWith("1", 16)) {
                    nowcnt09++;
                }
                if (facs.get(i).startsWith("1", 18)) {
                    nowcnt10++;
                }
            } else if (years.get(i).equals(production)) {
                if (facs.get(i).startsWith("1")) {
                    productioncnt01++;
                }
                if (facs.get(i).startsWith("1", 2)) {
                    productioncnt02++;
                }
                if (facs.get(i).startsWith("1", 4)) {
                    productioncnt03++;
                }
                if (facs.get(i).startsWith("1", 6)) {
                    productioncnt04++;
                }
                if (facs.get(i).startsWith("1", 8)) {
                    productioncnt05++;
                }
                if (facs.get(i).startsWith("1", 10)) {
                    productioncnt06++;
                }
                if (facs.get(i).startsWith("1", 12)) {
                    productioncnt07++;
                }
                if (facs.get(i).startsWith("1", 14)) {
                    productioncnt08++;
                }
                if (facs.get(i).startsWith("1", 16)) {
                    productioncnt09++;
                }
                if (facs.get(i).startsWith("1", 18)) {
                    productioncnt10++;
                }
            }
        }

        nowFacCnt.add(now);
        nowFacCnt.add(Integer.toString(nowcnt01));
        nowFacCnt.add(Integer.toString(nowcnt02));
        nowFacCnt.add(Integer.toString(nowcnt03));
        nowFacCnt.add(Integer.toString(nowcnt04));
        nowFacCnt.add(Integer.toString(nowcnt05));
        nowFacCnt.add(Integer.toString(nowcnt06));
        nowFacCnt.add(Integer.toString(nowcnt07));
        nowFacCnt.add(Integer.toString(nowcnt08));
        nowFacCnt.add(Integer.toString(nowcnt09));
        nowFacCnt.add(Integer.toString(nowcnt10));

        productionFacCnt.add(production);
        productionFacCnt.add(Integer.toString(productioncnt01));
        productionFacCnt.add(Integer.toString(productioncnt02));
        productionFacCnt.add(Integer.toString(productioncnt03));
        productionFacCnt.add(Integer.toString(productioncnt04));
        productionFacCnt.add(Integer.toString(productioncnt05));
        productionFacCnt.add(Integer.toString(productioncnt06));
        productionFacCnt.add(Integer.toString(productioncnt07));
        productionFacCnt.add(Integer.toString(productioncnt08));
        productionFacCnt.add(Integer.toString(productioncnt09));
        productionFacCnt.add(Integer.toString(productioncnt10));

//        System.out.println("2019 조사시설물 카운트 : "+nowFacCnt);
//        System.out.println("2018 조사시설물 카운트 : "+productionFacCnt);

        facgraphDataColumns.add(productionFacCnt);
        facgraphDataColumns.add(nowFacCnt);

        //월별 출동 현황
        nowcnt01 = 0; nowcnt02 = 0; nowcnt03 = 0;
        nowcnt04 = 0; nowcnt05 = 0; nowcnt06 = 0;
        nowcnt07 = 0; nowcnt08 = 0; nowcnt09 = 0;
        nowcnt10 = 0; int nowcnt11 = 0; int nowcnt12 = 0;

        productioncnt01 = 0; productioncnt02 = 0; productioncnt03 = 0;
        productioncnt04 = 0; productioncnt05 = 0; productioncnt06 = 0;
        productioncnt07 = 0; productioncnt08 = 0; productioncnt09 = 0;
        productioncnt10 = 0; int productioncnt11 = 0; int productioncnt12 = 0;

        for(int i=0; i<months.size(); i++) {
            if (years.get(i).equals(now)) {
                if (months.get(i).equals("01")) {
                    nowcnt01++;
                } else if (months.get(i).equals("02")) {
                    nowcnt02++;
                } else if (months.get(i).equals("03")) {
                    nowcnt03++;
                } else if (months.get(i).equals("04")) {
                    nowcnt04++;
                } else if (months.get(i).equals("05")) {
                    nowcnt05++;
                } else if (months.get(i).equals("06")) {
                    nowcnt06++;
                } else if (months.get(i).equals("07")) {
                    nowcnt07++;
                } else if (months.get(i).equals("08")) {
                    nowcnt08++;
                } else if (months.get(i).equals("09")) {
                    nowcnt09++;
                } else if (months.get(i).equals("10")) {
                    nowcnt10++;
                } else if (months.get(i).equals("11")) {
                    nowcnt11++;
                } else if (months.get(i).equals("12")) {
                    nowcnt12++;
                }
            } else if (years.get(i).equals(production)) {
                if (months.get(i).equals("01")) {
                    productioncnt01++;
                } else if (months.get(i).equals("02")) {
                    productioncnt02++;
                } else if (months.get(i).equals("03")) {
                    productioncnt03++;
                } else if (months.get(i).equals("04")) {
                    productioncnt04++;
                } else if (months.get(i).equals("05")) {
                    productioncnt05++;
                } else if (months.get(i).equals("06")) {
                    productioncnt06++;
                } else if (months.get(i).equals("07")) {
                    productioncnt07++;
                } else if (months.get(i).equals("08")) {
                    productioncnt08++;
                } else if (months.get(i).equals("09")) {
                    productioncnt09++;
                } else if (months.get(i).equals("10")) {
                    productioncnt10++;
                } else if (months.get(i).equals("11")) {
                    productioncnt11++;
                } else if (months.get(i).equals("12")) {
                    productioncnt12++;
                }
            }
        }

        List<String> nowCnt = new ArrayList<>();
        List<String> productionCnt = new ArrayList<>();

        nowCnt.add(now);
        nowCnt.add(Integer.toString(nowcnt01));
        nowCnt.add(Integer.toString(nowcnt02));
        nowCnt.add(Integer.toString(nowcnt03));
        nowCnt.add(Integer.toString(nowcnt04));
        nowCnt.add(Integer.toString(nowcnt05));
        nowCnt.add(Integer.toString(nowcnt06));
        nowCnt.add(Integer.toString(nowcnt07));
        nowCnt.add(Integer.toString(nowcnt08));
        nowCnt.add(Integer.toString(nowcnt09));
        nowCnt.add(Integer.toString(nowcnt10));
        nowCnt.add(Integer.toString(nowcnt11));
        nowCnt.add(Integer.toString(nowcnt12));

        productionCnt.add(production);
        productionCnt.add(Integer.toString(productioncnt01));
        productionCnt.add(Integer.toString(productioncnt02));
        productionCnt.add(Integer.toString(productioncnt03));
        productionCnt.add(Integer.toString(productioncnt04));
        productionCnt.add(Integer.toString(productioncnt05));
        productionCnt.add(Integer.toString(productioncnt06));
        productionCnt.add(Integer.toString(productioncnt07));
        productionCnt.add(Integer.toString(productioncnt08));
        productionCnt.add(Integer.toString(productioncnt09));
        productionCnt.add(Integer.toString(productioncnt10));
        productionCnt.add(Integer.toString(productioncnt11));
        productionCnt.add(Integer.toString(productioncnt12));

//        System.out.println("현재 카운트 : "+nowCnt);
//        System.out.println("작년 카운트 : "+productionCnt);

        monthgraphDataColumns.add(productionCnt);
        monthgraphDataColumns.add(nowCnt);

//        System.out.println("출동요청기관 데이터 : "+circleDataColumns);
//        System.out.println("재해재난유형 데이터 : "+disastergraphDataColumns);
//        System.out.println("조사시설물 데이터 : "+facgraphDataColumns);
//        System.out.println("월별 출동 현황 데이터 : "+monthgraphDataColumns);


        // 부서별 출동 현황
//        log.info("now : "+now);
//        log.info("production : "+production);
        List<ResponsibilListDto> responsibils = recordService.recordResponList(ids);
//        log.info("responsibils : "+responsibils);
        List<String> teamsData = new ArrayList<>();
        List<String> teamgraphDataColumns = new ArrayList<>();

        List<String> teamNames = new ArrayList<>();
        List<String> teamName = new ArrayList<>();

        List<String> rankNames = new ArrayList<>();
        List<String> rankNamesNow = new ArrayList<>();
        List<String> rankNamesProduction = new ArrayList<>();
        List<String> rankNamesSize = new ArrayList<>();
        List<String> rankNamesCountNow = new ArrayList<>();
        List<String> rankNamesCountPro = new ArrayList<>();

        for (int i=0; i<responsibils.size(); i++) {
            if (responsibils.get(i).getInsertYear().equals(now)) {
                rankNamesNow.add(responsibils.get(i).getArEmployeeName());
            } else if (responsibils.get(i).getInsertYear().equals(production)) {
                rankNamesProduction.add(responsibils.get(i).getArEmployeeName());
            } else {
                rankNames.add(responsibils.get(i).getArEmployeeName());
            }
        }
        Map<String,Integer> map = new HashMap<>();
        List<String> nameList = null;
        List<String> nameList2 = null;
        if(rankNamesNow.size()>0) {
            int max1 = 0;
            map.clear();
            rankNamesSize.clear();
            for (int i = 0; i < rankNamesNow.size(); i++) {
                if (!rankNamesSize.contains(rankNamesNow.get(i))) {
                    rankNamesSize.add(rankNamesNow.get(i));
                    String name = rankNamesNow.get(i);
                    int cnt = 0;
                    for(int j=0; j<rankNamesNow.size(); j++){
                        if(rankNamesNow.get(j).equals(name)){
                            cnt++;
                        }
                    }
                    if(max1<cnt) {
                        max1 = cnt;
                    }

                    rankNamesCountNow.add(String.valueOf(cnt));
                    map.put(name,cnt);
                }
            }
            Collections.sort(rankNamesCountNow, Collections.reverseOrder() );
            nameList = new ArrayList<>(map.keySet());
            // 내림차순 //
            Collections.sort(nameList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return map.get(o2).compareTo(map.get(o1));
                }
            });

            data.put("max1",  max1);
            rankNamesCountNow.add(0,now+"년도 출동건수");
        }
        if(rankNamesProduction.size()>0) {
            int max2 = 0;
            map.clear();
            rankNamesSize.clear();
            for (int i = 0; i < rankNamesProduction.size(); i++) {
                if (!rankNamesSize.contains(rankNamesProduction.get(i))) {
                    rankNamesSize.add(rankNamesProduction.get(i));
                    String name = rankNamesProduction.get(i);
                    int cnt = 0;
                    for(int j=0; j<rankNamesProduction.size(); j++){
                        if(rankNamesProduction.get(j).equals(name)){
                            cnt++;
                        }
                    }
                    if(max2<cnt) {
                        max2 = cnt;
                    }
                    rankNamesCountPro.add(String.valueOf(cnt));
                    map.put(name,cnt);
                }
            }
            Collections.sort(rankNamesCountPro, Collections.reverseOrder() );
            nameList2 = new ArrayList<>(map.keySet());
            // 내림차순 //
            Collections.sort(nameList2, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return map.get(o2).compareTo(map.get(o1));
                }
            });

            data.put("max2",  max2);
            rankNamesCountPro.add(0,production+"년도 출동건수");
        }

//        log.info("스테이트번호 arState :"+arState);
        responsibils.forEach(x -> teamNames.add(x.getTeam()));

        //배열 맨앞 빈칸채우기
        teamgraphDataColumns.add(" ");

        //등록된 중복부서 중복되지않게 정렬
        for(int i=0; i<teamNames.size(); i++){
            if(!teamsData.contains(teamNames.get(i))){
                teamsData.add(teamNames.get(i));
            }
        }

        for(int j=0; j<teamsData.size(); j++) {
            String team = teamsData.get(j);
            teamName.clear();
            for (int i = 0; i < teamsData.size(); i++) {
                if (!teamName.contains(team)) {
                    teamName.add(team);
                }
            }
            teamgraphDataColumns.add(Long.toString(teamNames.stream().filter(x -> x.contains(team)).count()));
        }

//        System.out.println("등록된 팀당 건수 team_data_columns : "+teamgraphDataColumns);
//        System.out.println("등록된 부서팀 teamsData : "+teamsData);

        data.put("nowYear",now);
        data.put("production",production);

        // 원형 그래프데이터
        data.put("circle_data_columns",circleDataColumns);
        // 재해재난 그래프데이터
        data.put("disaster_data_columns",disastergraphDataColumns);
        // 조사시설물 그래프데이터
        data.put("fac_data_columns",facgraphDataColumns);
//        // 부서별 출동현황 그래프데이터
        data.put("team_data_columns",teamgraphDataColumns);
        data.put("teamsData",teamsData);
        // 월별 출동현황 그래프데이터
        data.put("month_data_columns",monthgraphDataColumns);

        // now 년도 출동랭킹 데이터
        data.put("nameList",nameList);
        data.put("rankNamesCountNow",rankNamesCountNow);
        // pro 년도 출동랭킹 데이터
        data.put("nameList2",nameList2);
        data.put("rankNamesCountPro",rankNamesCountPro);

        res.addResponse("data",data);
        return ResponseEntity.ok(res.success());
    }

}